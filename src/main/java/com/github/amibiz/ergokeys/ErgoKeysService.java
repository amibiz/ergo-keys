package com.github.amibiz.ergokeys;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.keymap.Keymap;
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

    private final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    private Keymap insertModeKeymap;
    private Keymap commandModeKeymap;
    private Editor lastEditorUsed;

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
        return insertModeKeymap;
    }

    public void setInsertModeKeymap(Keymap insertModeKeymap) {
        this.insertModeKeymap = insertModeKeymap;
    }

    public Keymap getCommandModeKeymap() {
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
        if (ErgoKeysSettings.getInstance().isCommandModeToggle() && this.inCommandMode()) {
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
}