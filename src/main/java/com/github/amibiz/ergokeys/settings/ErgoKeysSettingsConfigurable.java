/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

final class ErgoKeysSettingsConfigurable implements Configurable {

    private ErgoKeysSettingsComponent component;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ErgoKeys";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return component.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        component = new ErgoKeysSettingsComponent();
        return component.getPanel();
    }

    @Override
    public boolean isModified() {
        ErgoKeysSettingsState settings = ErgoKeysSettingsState.getInstance();
        return component.getCommandModeToggle() != settings.getCommandModeToggle();
    }

    @Override
    public void apply() {
        ErgoKeysSettingsState settings = ErgoKeysSettingsState.getInstance();
        settings.setCommandModeToggle(component.getCommandModeToggle());
    }

    @Override
    public void reset() {
        ErgoKeysSettingsState settings = ErgoKeysSettingsState.getInstance();
        component.setCommandModeToggle(settings.getCommandModeToggle());
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }

}
