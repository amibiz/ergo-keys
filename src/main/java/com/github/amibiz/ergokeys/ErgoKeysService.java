package com.github.amibiz.ergokeys;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.keymap.Keymap;

@Service
public final class ErgoKeysService {

    private Keymap insertModeKeymap;
    private Keymap commandModeKeymap;

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
}