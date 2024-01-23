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

import java.util.ArrayList;
import java.util.List;

@Service
public final class ErgoKeysService {
    private static final Logger LOG = Logger.getInstance(ErgoKeysService.class);

    private static final String PLUGIN_ID = "com.github.amibiz.ergokeys";

    private static final String ROOT_ERGOKEYS_KEYMAP = "$ergokeys";
    public static final String INSERT_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME = "insertModeKeymapName";
    public static final String COMMAND_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME = "commandModeKeymapName";
    private static final String DEFAULT_ERGOKEYS_KEYMAP = "ErgoKeys (QWERTY)";

    private final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    private Keymap insertModeKeymap;
    private Keymap commandModeKeymap;
    private Editor lastEditorUsed;

    public static ErgoKeysService getInstance() {
        return ApplicationManager.getApplication().getService(ErgoKeysService.class);
    }

    public String loadPersistentProperty(String key) {
        return propertiesComponent.getValue(persistentPropertyName(key));
    }

    public void storePersistentProperty(String key, String value) {
        propertiesComponent.setValue(persistentPropertyName(key), value);
    }

    private String persistentPropertyName(String key) {
        return PLUGIN_ID + "." + key;
    }

    public Keymap getInsertModeKeymap() {
        if (insertModeKeymap == null) {
            KeymapManagerEx keymapManagerEx = KeymapManagerEx.getInstanceEx();

            String insertModeKeymapName = loadPersistentProperty(INSERT_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME);
            if (insertModeKeymapName == null) {
                setInsertModeKeymap(keymapManagerEx.getActiveKeymap());
            } else {
                setInsertModeKeymap(keymapManagerEx.getKeymap(insertModeKeymapName));
                if (getInsertModeKeymap() == null) {
                    setInsertModeKeymap(keymapManagerEx.getKeymap(KeymapManager.DEFAULT_IDEA_KEYMAP));
                    assert getInsertModeKeymap() != null;
                }
            }
            storePersistentProperty(INSERT_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME, getInsertModeKeymap().getName());
        }

        return insertModeKeymap;
    }

    public void setInsertModeKeymap(Keymap insertModeKeymap) {
        this.insertModeKeymap = insertModeKeymap;
    }

    public Keymap getCommandModeKeymap() {
        if (commandModeKeymap == null) {
            KeymapManagerEx keymapManagerEx = KeymapManagerEx.getInstanceEx();

            String commandModeKeymapName = loadPersistentProperty(COMMAND_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME);
            if (commandModeKeymapName == null) {
                setCommandModeKeymap(keymapManagerEx.getKeymap(DEFAULT_ERGOKEYS_KEYMAP));
                assert getCommandModeKeymap() != null;
            } else {
                setCommandModeKeymap(keymapManagerEx.getKeymap(commandModeKeymapName));
                if (getCommandModeKeymap() == null) {
                    setCommandModeKeymap(keymapManagerEx.getKeymap(DEFAULT_ERGOKEYS_KEYMAP));
                    assert getCommandModeKeymap() != null;
                }
            }
            storePersistentProperty(COMMAND_MODE_KEYMAP_PERSISTENT_PROPERTY_NAME, getCommandModeKeymap().getName());
        }
        return commandModeKeymap;
    }

    public void setCommandModeKeymap(Keymap commandModeKeymap) {
        this.commandModeKeymap = commandModeKeymap;
    }

    public Editor getLastEditorUsed() {
        return lastEditorUsed;
    }

    public void setLastEditorUsed(Editor lastEditorUsed) {
        this.lastEditorUsed = lastEditorUsed;
    }

    public void setActiveKeymap(@NotNull Keymap keymap) {
        KeymapManagerEx.getInstanceEx().setActiveKeymap(keymap);
    }

    public boolean inCommandMode() {
        return isErgoKeysKeymap(KeymapManagerEx.getInstanceEx().getActiveKeymap());
    }

    public void activateInsertMode(Editor editor) {
        editor.getSettings().setBlockCursor(false);
        this.setActiveKeymap(this.getInsertModeKeymap());
    }

    public void activateCommandMode(Editor editor) {
        ErgoKeysSettingsState settings = ErgoKeysSettingsState.getInstance();
        if (settings.getCommandModeToggle() && this.inCommandMode()) {
            this.activateInsertMode(editor);
            return;
        }
        editor.getSettings().setBlockCursor(true);
        KeymapManagerEx.getInstanceEx().setActiveKeymap(this.getCommandModeKeymap());
    }

    public boolean isErgoKeysKeymap(@Nullable Keymap keymap) {
        for (; keymap != null; keymap = keymap.getParent()) {
            if (ROOT_ERGOKEYS_KEYMAP.equalsIgnoreCase(keymap.getName())) {
                return true;
            }
        }
        return false;
    }

    public Keymap[] getAllErgoKeysKeymaps() {
        KeymapManagerEx keymapManagerEx = KeymapManagerEx.getInstanceEx();

        List<Keymap> keymaps = new ArrayList<>();
        for (Keymap keymap : keymapManagerEx.getAllKeymaps()) {
            LOG.debug("getAllErgoKeysKeymaps: check keymap ", keymap);
            if (isErgoKeysKeymap(keymap)) {
                keymaps.add(keymap);
            }
        }
        return keymaps.toArray(new Keymap[0]);
    }

    public void extendCommandModeShortcuts(@NotNull Keymap dst) {
        for (Keymap keymap : this.getAllErgoKeysKeymaps()) {
            this.extendShortcuts(keymap, dst);
        }
    }

    private void extendShortcuts(@NotNull Keymap dst, Keymap src) {
        for (String actionId : src.getActionIds()) {
            for (Shortcut shortcut : src.getShortcuts(actionId)) {
                dst.addShortcut(actionId, shortcut);
            }
        }
    }

    public void activeKeymapChanged(Keymap keymap) {
        if (keymap == null || keymap.equals(getCommandModeKeymap()) || keymap.equals(getInsertModeKeymap())) {
            return;
        }

        String key;
        if (isErgoKeysKeymap(keymap)) {
            setCommandModeKeymap(keymap);
            key = "commandModeKeymapName";
        } else if (getInsertModeKeymap() != null) {
            purgeCommandModeShortcuts(getInsertModeKeymap());
            setInsertModeKeymap(keymap);
            key = "insertModeKeymapName";
            extendCommandModeShortcuts(getInsertModeKeymap());
            activateInsertMode(getLastEditorUsed());
        } else {
            LOG.debug("activeKeymapChanged: missing insert mode keymap");
            return;
        }
        storePersistentProperty(key, keymap.getName());
    }

    private void purgeCommandModeShortcuts(@NotNull Keymap dst) {
        for (Keymap keymap : getAllErgoKeysKeymaps()) {
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
}