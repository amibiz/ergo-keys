/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.github.amibiz.ergokeys.settings.ErgoKeysSettingsState;
import com.intellij.find.actions.FindInPathAction;
import com.intellij.ide.actions.GotoActionAction;
import com.intellij.ide.actions.SearchEverywhereAction;
import com.intellij.ide.actions.Switcher;
import com.intellij.ide.actions.ViewStructureAction;
import com.intellij.ide.actions.runAnything.RunAnythingAction;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.IncrementalFindAction;
import com.intellij.openapi.editor.actions.ReplaceAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.FocusEvent;
import java.util.HashSet;
import java.util.Set;

@Service
public final class ErgoKeysService {
    private static final Logger LOG = Logger.getInstance(ErgoKeysService.class);

    private static final String PLUGIN_ID = "com.github.amibiz.ergokeys";

    private static final String ROOT_ERGOKEYS_KEYMAP = "$ergokeys";
    private static final String DEFAULT_ERGOKEYS_KEYMAP = "ErgoKeys (QWERTY)";
    public static final String INSERT_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME = "insertModeKeymapName";
    public static final String COMMAND_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME = "commandModeKeymapName";

    private Keymap insertModeKeymap;
    private Keymap commandModeKeymap;
    private Editor lastEditorUsed;
    private final Set<Keymap> ergoKeysKeymaps = new HashSet<>();

    private ModeState state;

    public ErgoKeysService() {
        LOG.debug("Started");

        KeymapManagerEx keymapManagerEx = KeymapManagerEx.getInstanceEx();

        // NOTE: we use the keymap parent relationship to extend derived
        // shortcuts and to identify command mode keymaps. At this stage,
        // the keymaps parent reference is null (lazy).
        // To overcome this, we force all keymaps to load by calling the
        // getActionIdList() method.
        for (Keymap keymap : keymapManagerEx.getAllKeymaps()) {
            keymap.getActionIdList();
        }

        // Setup command mode keymap
        String commandModeKeymapName = loadPersistentProperty(COMMAND_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME);
        if (commandModeKeymapName == null) {
            // First time plugin loaded
            commandModeKeymapName = DEFAULT_ERGOKEYS_KEYMAP;
        }
        setCommandModeKeymap(keymapManagerEx.getKeymap(commandModeKeymapName));

        // Setup insert mode keymap
        String insertModeKeymapName = loadPersistentProperty(INSERT_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME);
        if (insertModeKeymapName == null) {
            // First time plugin loaded
            insertModeKeymapName = KeymapManager.DEFAULT_IDEA_KEYMAP;
        }
        setInsertModeKeymap(keymapManagerEx.getKeymap(insertModeKeymapName));

        // Setup ergokeys keymaps
        for (Keymap keymap : keymapManagerEx.getAllKeymaps()) {
            if (isErgoKeysKeymap(keymap)) {
                ergoKeysKeymaps.add(keymap);
            }
        }

        // Extend all command mode keymaps with insert mode keymap shortcuts
        extendCommandModeShortcuts(insertModeKeymap);

        // Set initial state based on active keymap
        if (isErgoKeysKeymap(keymapManagerEx.getActiveKeymap())) {
            setState(ModeState.CMD);
        } else {
            setState(ModeState.INS);
        }
    }

    private void setState(ModeState state) {
        LOG.debug("setState: state=" + state);
        this.state = state;
    }

    public static ErgoKeysService getInstance() {
        return ApplicationManager.getApplication().getService(ErgoKeysService.class);
    }

    private String loadPersistentProperty(String key) {
        return PropertiesComponent.getInstance().getValue(persistentPropertyName(key));
    }

    private void storePersistentProperty(String key, String value) {
        PropertiesComponent.getInstance().setValue(persistentPropertyName(key), value);
    }

    private String persistentPropertyName(String key) {
        return PLUGIN_ID + "." + key;
    }

    public void activateInsertMode(Editor editor, Boolean tran) {
        setState(tran ? ModeState.TRAN_INS : ModeState.INS);
        setActiveKeymap(insertModeKeymap);
        editor.getSettings().setBlockCursor(false);
    }

    private void setCommandModeKeymap(Keymap keymap) {
        assert keymap != null : "Command mode keymap must not be null";

        if (commandModeKeymap != keymap) {
            storePersistentProperty(COMMAND_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME, keymap.getName());
        }

        commandModeKeymap = keymap;
    }

    private void setInsertModeKeymap(Keymap keymap) {
        assert keymap != null : "Insert mode keymap must not be null";

        if (insertModeKeymap != keymap) {
            storePersistentProperty(INSERT_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME, keymap.getName());
        }

        insertModeKeymap = keymap;
    }

    private void setActiveKeymap(@NotNull Keymap keymap) {
        KeymapManagerEx.getInstanceEx().setActiveKeymap(keymap);
    }

    public boolean inCommandMode() {
        return isErgoKeysKeymap(KeymapManagerEx.getInstanceEx().getActiveKeymap());
    }

    public void activateCommandMode(Editor editor) {
        ErgoKeysSettingsState settings = ErgoKeysSettingsState.getInstance();
        if (settings.getCommandModeToggle() && inCommandMode()) {
            activateInsertMode(editor, false);
            return;
        }
        setState(ModeState.CMD);
        setActiveKeymap(commandModeKeymap);
        editor.getSettings().setBlockCursor(true);
    }

    public void activeKeymapChanged(Keymap keymap) {
        if (keymap == null || keymap == commandModeKeymap || keymap == insertModeKeymap) {
            return;
        }

        if (isErgoKeysKeymap(keymap)) {
            LOG.debug("activeKeymapChanged: changed to ergokeys new keymap");
            setCommandModeKeymap(keymap);
        } else {
            LOG.debug("activeKeymapChanged: changed to new non ergokeys keymap");
            purgeCommandModeShortcuts(insertModeKeymap);
            extendCommandModeShortcuts(keymap);
            setInsertModeKeymap(keymap);
            activateInsertMode(lastEditorUsed, false);
        }
    }

    private boolean isErgoKeysKeymap(@Nullable Keymap keymap) {
        for (; keymap != null; keymap = keymap.getParent()) {
            if (ROOT_ERGOKEYS_KEYMAP.equalsIgnoreCase(keymap.getName())) {
                return true;
            }
        }
        return false;
    }

    private void extendCommandModeShortcuts(@NotNull Keymap src) {
        for (Keymap keymap : ergoKeysKeymaps) {
            this.extendShortcuts(keymap, src);
        }
    }

    private void extendShortcuts(@NotNull Keymap dst, Keymap src) {
        for (String actionId : src.getActionIds()) {
            for (Shortcut shortcut : src.getShortcuts(actionId)) {
                dst.addShortcut(actionId, shortcut);
            }
        }
    }

    private void purgeCommandModeShortcuts(@NotNull Keymap dst) {
        for (Keymap keymap : ergoKeysKeymaps) {
            this.purgeShortcuts(keymap, dst);
        }
    }

    private void purgeShortcuts(@NotNull Keymap dst, Keymap src) {
        for (String actionId : src.getActionIds()) {
            for (Shortcut shortcut : src.getShortcuts(actionId)) {
                dst.removeShortcut(actionId, shortcut);
            }
        }
    }

    public void keymapAdded(@NotNull Keymap keymap) {
        if (isErgoKeysKeymap(keymap)) {
            ergoKeysKeymaps.add(keymap);
        }
    }

    public void keymapRemoved(@NotNull Keymap keymap) {
        ergoKeysKeymaps.remove(keymap);
    }

    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
        if (action.getClass().equals(SearchEverywhereAction.class) ||
                action.getClass().equals(RunAnythingAction.class) ||
                action.getClass().equals(IncrementalFindAction.class) ||
                action.getClass().equals(FindInPathAction.class) ||
                action.getClass().equals(ViewStructureAction.class) ||
                action.getClass().equals(Switcher.class) ||
                action.getClass().equals(GotoActionAction.class) ||
                action.getClass().equals(ReplaceAction.class)) {
            final Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
            if (editor != null) {
                activateInsertMode(editor, true);
            }
        }
    }

    private enum ModeState {
        CMD, // Command mode
        INS, // Insert mode
        TRAN_INS, // Transient insert mode
    }

    private boolean isFileEditor(Editor editor) {
        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        VirtualFile virtualFile = documentManager.getFile(editor.getDocument());
        return virtualFile != null && !(virtualFile instanceof LightVirtualFile);
    }

    public void editorFocusGained(Editor editor) {
        if (!isFileEditor(editor)) {
            // Do not change state if we are in an EditorComponent that
            // has no file (for example, the editor text field component
            // used when right-clicking rename a file in project view).
            return;
        }
        if (state == ModeState.TRAN_INS) {
            activateCommandMode(editor);
            return;
        }
        editor.getSettings().setBlockCursor(inCommandMode());
    }

    public void editorFocusLost(FocusEvent focusEvent, Editor editor) {
        if (focusEvent.getOppositeComponent() != null) {
            String name = focusEvent.getOppositeComponent().getClass().getName();
            if (name.equals("com.intellij.terminal.JBTerminalPanel") ||
                    name.equals("com.intellij.ui.EditorTextField") ||
                    name.startsWith("com.intellij.ui.EditorComboBoxEditor") ||
                    name.equals("com.intellij.ide.ui.newItemPopup.NewItemWithTemplatesPopupPanel$JBExtendableTextFieldWithMixedAccessibleContext") ||
                    name.equals("com.intellij.ide.projectView.impl.ProjectViewPane$1")) {
                activateInsertMode(lastEditorUsed, true);
            }
        }
        lastEditorUsed = editor;
    }
}