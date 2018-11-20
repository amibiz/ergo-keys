/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ErgoKeysSettings", storages = @Storage("ergokeys.xml"))
public class ErgoKeysSettings implements PersistentStateComponent<ErgoKeysSettings> {
    public static final String KEYBOARD_LAYOUT_QWERTY = "qwerty";
    public static final String KEYBOARD_LAYOUT_DVORAK = "dvorak";

    // Defaults
    private String keyboardLayout = KEYBOARD_LAYOUT_QWERTY;
    private boolean commandModeToggle = false;

    public static ErgoKeysSettings getInstance() {
        return ServiceManager.getService(ErgoKeysSettings.class);
    }

    @Nullable
    @Override
    public ErgoKeysSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ErgoKeysSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getKeyboardLayout() {
        return keyboardLayout;
    }

    public void setKeyboardLayout(String keyboardLayout) {
        this.keyboardLayout = keyboardLayout;
    }

    public boolean isCommandModeToggle() {
        return commandModeToggle;
    }

    public void setCommandModeToggle(boolean commandModeToggle) {
        this.commandModeToggle = commandModeToggle;
    }
}
