/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.find.actions.FindInPathAction;
import com.intellij.ide.actions.SearchEverywhereAction;
import com.intellij.ide.actions.Switcher;
import com.intellij.ide.actions.ViewStructureAction;
import com.intellij.ide.actions.runAnything.RunAnythingAction;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actions.IncrementalFindAction;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManagerListener;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

public class ErgoKeysPlugin implements ApplicationComponent {

    private static final Logger LOG = Logger.getInstance(ErgoKeysPlugin.class);

    private static final String PLUGIN_ID = "com.github.amibiz.ergokeys";
    private static final String ROOT_ERGOKEYS_KEYMAP = "$ergokeys";
    private static final String DEFAULT_ERGOKEYS_KEYMAP = "ErgoKeys (QWERTY)";

    private final ErgoKeysSettings settings;
    private final Application application;
    private final KeymapManagerEx keymapManagerEx;
    private final PropertiesComponent propertiesComponent;

    private final ErgoKeysService service =
            ApplicationManager.getApplication().getService(ErgoKeysService.class);

    public ErgoKeysPlugin() {
        settings = ErgoKeysSettings.getInstance();
        application = ApplicationManager.getApplication();
        keymapManagerEx = KeymapManagerEx.getInstanceEx();
        propertiesComponent = PropertiesComponent.getInstance();

        ActionManager.getInstance().registerAction("ErgoKeysNoopAction", new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // noop
            }
        });

    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ErgoKeysPlugin";
    }

    @Override
    public void initComponent() {
        LOG.debug("initComponent");

        // NOTE: we use the keymap parent relationship to extend derived
        // shortcuts and to identify command mode keymaps. at this stage,
        // the keymaps parent reference is null (lazy).
        // to overcome this, we force all keymaps to load by calling the
        // getActionIdList() method.
        for (Keymap keymap : this.keymapManagerEx.getAllKeymaps()) {
            keymap.getActionIdList();
        }

        String insertModeKeymapName = this.loadPersistentProperty("insertModeKeymapName");
        if (insertModeKeymapName == null) {
            service.setInsertModeKeymap(keymapManagerEx.getActiveKeymap());
        } else {
            service.setInsertModeKeymap(keymapManagerEx.getKeymap(insertModeKeymapName));
            if (service.getInsertModeKeymap() == null) {
                service.setInsertModeKeymap(keymapManagerEx.getKeymap("$default"));
                assert service.getInsertModeKeymap() != null;
            }
        }
        this.storePersistentProperty("insertModeKeymapName", service.getInsertModeKeymap().getName());

        String commandModeKeymapName = this.loadPersistentProperty("commandModeKeymapName");
        if (commandModeKeymapName == null) {
            service.setCommandModeKeymap(keymapManagerEx.getKeymap(DEFAULT_ERGOKEYS_KEYMAP));
            assert service.getCommandModeKeymap() != null;
        } else {
            service.setCommandModeKeymap(keymapManagerEx.getKeymap(commandModeKeymapName));
            if (service.getCommandModeKeymap() == null) {
                service.setCommandModeKeymap(keymapManagerEx.getKeymap(DEFAULT_ERGOKEYS_KEYMAP));
                assert service.getCommandModeKeymap() != null;
            }
        }
        this.storePersistentProperty("commandModeKeymapName", service.getCommandModeKeymap().getName());

        extendCommandModeShortcuts(service.getInsertModeKeymap());

        application.getMessageBus().connect().subscribe(KeymapManagerListener.TOPIC, new KeymapManagerListener() {

            @Override
            public void activeKeymapChanged(@Nullable Keymap keymap) {
                LOG.debug("activeKeymapChanged: keymap " + keymap.getName());

                if (keymap.equals(service.getCommandModeKeymap()) || keymap.equals(service.getInsertModeKeymap())) {
                    return;
                }

                String key;
                if (isErgoKeysKeymap(keymap)) {
                    service.setCommandModeKeymap(keymap);
                    key = "commandModeKeymapName";
                } else {
                    purgeCommandModeShortcuts(service.getInsertModeKeymap());
                    service.setInsertModeKeymap(keymap);
                    key = "insertModeKeymapName";
                    extendCommandModeShortcuts(service.getInsertModeKeymap());
                    activateInsertMode(service.getLastEditorUsed());
                }
                storePersistentProperty(key, keymap.getName());
            }
        });

        ApplicationManager.getApplication().getMessageBus().connect().subscribe(AnActionListener.TOPIC, new AnActionListener() {
            @Override
            public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
                LOG.debug("beforeActionPerformed:", " action=", action.getClass(), " event=", event.getInputEvent());

                if (action.getClass().equals(SearchEverywhereAction.class) ||
                        action.getClass().equals(RunAnythingAction.class) ||
                        action.getClass().equals(IncrementalFindAction.class) ||
                        action.getClass().equals(FindInPathAction.class) ||
                        action.getClass().equals(ViewStructureAction.class) ||
                        action.getClass().equals(Switcher.class)) {
                    final Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
                    if (editor != null) {
                        activateInsertMode(editor);
                    }
                }

                AnActionListener.super.beforeActionPerformed(action, event);
            }

            @Override
            public void afterActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event, @NotNull AnActionResult result) {
                LOG.debug("afterActionPerformed:", " action=", action.getClass(), " event=",
                        event.getInputEvent(), " result=", result.getClass());

                AnActionListener.super.afterActionPerformed(action, event, result);
            }

            @Override
            public void beforeEditorTyping(char c, @NotNull DataContext dataContext) {
                LOG.debug("beforeEditorTyping: c=", c);
            }
        });

        EditorFactory.getInstance().addEditorFactoryListener(
                new EditorFactoryListener() {
                    @Override
                    public void editorCreated(@NotNull EditorFactoryEvent event) {
                        Editor editor = event.getEditor();

                        // Enable character based shortcuts (disabled by default since 2021.2)
                        // for newly created editors.
                        editor.getContentComponent().putClientProperty(ActionUtil.ALLOW_PlAIN_LETTER_SHORTCUTS, true);

                        editor.getContentComponent().addFocusListener(new FocusListener() {
                            @Override
                            public void focusGained(FocusEvent focusEvent) {
                                LOG.debug("focusGained: focusEvent=", focusEvent);
                                editor.getSettings().setBlockCursor(inCommandMode());
                            }

                            @Override
                            public void focusLost(FocusEvent focusEvent) {
                                LOG.debug("focusLost: focusEvent=", focusEvent);

                                if (focusEvent.getOppositeComponent() != null &&
                                        focusEvent.getOppositeComponent().getClass().getName().equals("com.intellij.terminal.JBTerminalPanel")) {
                                    setActiveKeymap(service.getInsertModeKeymap());
                                }
                                service.setLastEditorUsed(editor);
                            }
                        });

                        EditorFactoryListener.super.editorCreated(event);
                    }

                    @Override
                    public void editorReleased(@NotNull EditorFactoryEvent event) {
                        EditorFactoryListener.super.editorReleased(event);
                    }
                },
                new Disposable() {
                    @Override
                    public void dispose() {

                    }
                }
        );
    }

    public void applySettings() {
    }

    public void activateCommandMode(Editor editor) {
        if (settings.isCommandModeToggle() && inCommandMode()) {
            activateInsertMode(editor);
            return;
        }
        editor.getSettings().setBlockCursor(true);
        this.keymapManagerEx.setActiveKeymap(service.getCommandModeKeymap());
    }

    public void activateInsertMode(Editor editor) {
        editor.getSettings().setBlockCursor(false);
        this.setActiveKeymap(service.getInsertModeKeymap());
    }

    public void setActiveKeymap(@NotNull Keymap keymap) {
        this.keymapManagerEx.setActiveKeymap(keymap);
    }

    private String persistentPropertyName(String key) {
        return PLUGIN_ID + "." + key;
    }

    private String loadPersistentProperty(String key) {
        return propertiesComponent.getValue(persistentPropertyName(key));
    }

    private void storePersistentProperty(String key, String value) {
        propertiesComponent.setValue(persistentPropertyName(key), value);
    }

    private boolean inCommandMode() {
        return isErgoKeysKeymap(keymapManagerEx.getActiveKeymap());
    }

    private void extendCommandModeShortcuts(@NotNull Keymap dst) {
        for (Keymap keymap : this.getAllErgoKeysKeymaps()) {
            this.extendShortcuts(keymap, dst);
        }
    }

    private void purgeCommandModeShortcuts(@NotNull Keymap dst) {
        for (Keymap keymap : this.getAllErgoKeysKeymaps()) {
            this.purgeShortcuts(keymap, dst);
        }
    }

    private void extendShortcuts(@NotNull Keymap dst, Keymap src) {
        for (String actionId : src.getActionIds()) {
            for (Shortcut shortcut : src.getShortcuts(actionId)) {
                dst.addShortcut(actionId, shortcut);
            }
        }
    }

    private void purgeShortcuts(@NotNull Keymap dst, Keymap src) {
        for (String actionId : src.getActionIds()) {
            for (Shortcut shortcut : src.getShortcuts(actionId)) {
                dst.removeShortcut(actionId, shortcut);
            }
        }
    }

    private Keymap[] getAllErgoKeysKeymaps() {
        List<Keymap> keymaps = new ArrayList<>();
        for (Keymap keymap : this.keymapManagerEx.getAllKeymaps()) {
            LOG.debug("getAllErgoKeysKeymaps: check keymap ", keymap);
            if (isErgoKeysKeymap(keymap)) {
                keymaps.add(keymap);
            }
        }
        return keymaps.toArray(new Keymap[0]);
    }

    private boolean isErgoKeysKeymap(@Nullable Keymap keymap) {
        for (; keymap != null; keymap = keymap.getParent()) {
            if (ROOT_ERGOKEYS_KEYMAP.equalsIgnoreCase(keymap.getName())) {
                return true;
            }
        }
        return false;
    }
}
