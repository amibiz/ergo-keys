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
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    }

    public static ErgoKeysService getInstance() {
        return ApplicationManager.getApplication().getService(ErgoKeysService.class);
    }

    public String loadPersistentProperty(String key) {
        return PropertiesComponent.getInstance().getValue(persistentPropertyName(key));
    }

    public void storePersistentProperty(String key, String value) {
        PropertiesComponent.getInstance().setValue(persistentPropertyName(key), value);
    }

    private String persistentPropertyName(String key) {
        return PLUGIN_ID + "." + key;
    }

    public Keymap getInsertModeKeymap() {
        return insertModeKeymap;
    }

    public void setCommandModeKeymap(Keymap keymap) {
        assert keymap != null : "Command mode keymap must not be null";

        if (commandModeKeymap != keymap) {
            storePersistentProperty(COMMAND_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME, keymap.getName());
        }

        commandModeKeymap = keymap;
    }

    public void setInsertModeKeymap(Keymap keymap) {
        assert keymap != null : "Insert mode keymap must not be null";

        if (insertModeKeymap != keymap) {
            storePersistentProperty(INSERT_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME, keymap.getName());
        }

        insertModeKeymap = keymap;
    }

    public Editor getLastEditorUsed() {
        return lastEditorUsed;
    }

    public void setLastEditorUsed(Editor editor) {
        lastEditorUsed = editor;
    }

    public void setActiveKeymap(@NotNull Keymap keymap) {
        KeymapManagerEx.getInstanceEx().setActiveKeymap(keymap);
    }

    public boolean inCommandMode() {
        return isErgoKeysKeymap(KeymapManagerEx.getInstanceEx().getActiveKeymap());
    }

    public void activateInsertMode(Editor editor) {
        editor.getSettings().setBlockCursor(false);
        setActiveKeymap(insertModeKeymap);
    }

    public void activateCommandMode(Editor editor) {
        ErgoKeysSettingsState settings = ErgoKeysSettingsState.getInstance();
        if (settings.getCommandModeToggle() && this.inCommandMode()) {
            this.activateInsertMode(editor);
            return;
        }
        editor.getSettings().setBlockCursor(true);
        setActiveKeymap(commandModeKeymap);
    }

    private boolean isErgoKeysKeymap(@Nullable Keymap keymap) {
        for (; keymap != null; keymap = keymap.getParent()) {
            if (ROOT_ERGOKEYS_KEYMAP.equalsIgnoreCase(keymap.getName())) {
                return true;
            }
        }
        return false;
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
            activateInsertMode(getLastEditorUsed());
        }
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
}