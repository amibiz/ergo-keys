/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ErgoKeysConfigurable implements SearchableConfigurable {
    private final ErgoKeysSettings settings;
    private final ErgoKeysPlugin plugin;
    private ErgoKeysConfigurationPanel ui;

    public ErgoKeysConfigurable() {
        settings = ServiceManager.getService(ErgoKeysSettings.class);
        plugin = ApplicationManager.getApplication().getComponent(ErgoKeysPlugin.class);
    }

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
        ui.setKeyboardLayout(settings.getKeyboardLayout());
        ui.setCommandModeToggle(settings.isCommandModeToggle());
    }

    @Override
    public boolean isModified() {
        return !settings.getKeyboardLayout().equals(ui.getKeyboardLayout()) ||
                settings.isCommandModeToggle() != ui.isCommandModeToggle();
    }

    @Override
    public void apply() {
        settings.setKeyboardLayout(ui.getKeyboardLayout());
        settings.setCommandModeToggle(ui.isCommandModeToggle());
        plugin.applySettings();
    }
}
