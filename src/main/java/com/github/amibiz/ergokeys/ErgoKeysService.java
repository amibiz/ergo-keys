package com.github.amibiz.ergokeys;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.keymap.Keymap;

@Service
public final class ErgoKeysService {
    private static final String PLUGIN_ID = "com.github.amibiz.ergokeys";

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
}