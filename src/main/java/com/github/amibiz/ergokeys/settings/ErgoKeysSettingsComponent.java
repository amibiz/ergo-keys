/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class ErgoKeysSettingsComponent {
    private final JPanel panel;

    @SuppressWarnings("DialogTitleCapitalization")
    private final JBCheckBox commandModeToggle =
            new JBCheckBox("Enable activating Insert Mode with Command Mode shortcut");

    public ErgoKeysSettingsComponent() {
        panel = FormBuilder.createFormBuilder()
                .addComponent(commandModeToggle, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return panel;
    }

    public JComponent getPreferredFocusedComponent() {
        return commandModeToggle;
    }

    public boolean getCommandModeToggle() {
        return commandModeToggle.isSelected();
    }

    public void setCommandModeToggle(boolean newStatus) {
        commandModeToggle.setSelected(newStatus);
    }
}
