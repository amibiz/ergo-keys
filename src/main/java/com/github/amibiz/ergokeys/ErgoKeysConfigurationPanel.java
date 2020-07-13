/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import javax.swing.*;

public class ErgoKeysConfigurationPanel {
    private JPanel rootPanel;
    private JCheckBox commandModeToggleCheckBox;

    public ErgoKeysConfigurationPanel() {
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public boolean isCommandModeToggle() {
        return commandModeToggleCheckBox.isSelected();
    }

    public void setCommandModeToggle(boolean selected) {
        commandModeToggleCheckBox.setSelected(selected);
    }
}
