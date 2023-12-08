/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ErgoKeysConfigurable implements SearchableConfigurable {
    private ErgoKeysConfigurationPanel ui;

    @NotNull
    @Override
    public String getId() {
        return "ergokeys";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ErgoKeys";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        ui = new ErgoKeysConfigurationPanel();
        return ui.getRootPanel();
    }

    @Override
    public void reset() {
        ErgoKeysSettings settings = ApplicationManager.getApplication().getService(ErgoKeysSettings.class);
        ui.setCommandModeToggle(settings.isCommandModeToggle());
    }

    @Override
    public boolean isModified() {
        ErgoKeysSettings settings = ApplicationManager.getApplication().getService(ErgoKeysSettings.class);
        return settings.isCommandModeToggle() != ui.isCommandModeToggle();
    }

    @Override
    public void apply() {
        ErgoKeysSettings settings = ApplicationManager.getApplication().getService(ErgoKeysSettings.class);
        settings.setCommandModeToggle(ui.isCommandModeToggle());
    }
}
