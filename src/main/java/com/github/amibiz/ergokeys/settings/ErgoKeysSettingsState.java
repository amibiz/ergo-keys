/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.github.amibiz.ergokeys.settings.ErgoKeysSettingsState",
        storages = @Storage("ergokeys.xml")
)
public class ErgoKeysSettingsState implements PersistentStateComponent<ErgoKeysSettingsState> {

    // Defaults
    public boolean commandModeToggle = false;

    public static ErgoKeysSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(ErgoKeysSettingsState.class);
    }

    @Nullable
    @Override
    public ErgoKeysSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ErgoKeysSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public boolean getCommandModeToggle() {
        return commandModeToggle;
    }

    public void setCommandModeToggle(boolean commandModeToggle) {
        this.commandModeToggle = commandModeToggle;
    }
}
