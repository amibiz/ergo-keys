/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import javax.swing.*;

public class ErgoKeysConfigurationPanel {
    private JRadioButton qwertyRadioButton;
    private JRadioButton dvorakRadioButton;
    private JPanel rootPanel;
    private JCheckBox commandModeToggleCheckBox;
    private ButtonGroup keyboardLayoutButtonGroup;

    public ErgoKeysConfigurationPanel() {
        qwertyRadioButton.setActionCommand(ErgoKeysSettings.KEYBOARD_LAYOUT_QWERTY);
        dvorakRadioButton.setActionCommand(ErgoKeysSettings.KEYBOARD_LAYOUT_DVORAK);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getKeyboardLayout() {
        return keyboardLayoutButtonGroup.getSelection().getActionCommand();
    }

    public void setKeyboardLayout(String keyboardLayout) {
        JRadioButton button;
        switch (keyboardLayout) {
            case ErgoKeysSettings.KEYBOARD_LAYOUT_QWERTY:
                button = qwertyRadioButton;
                break;

            case ErgoKeysSettings.KEYBOARD_LAYOUT_DVORAK:
                button = dvorakRadioButton;
                break;

            default:
                throw new IllegalArgumentException(String.format("unknown keyboard layout: \"%s\"", keyboardLayout));
        }
        keyboardLayoutButtonGroup.setSelected(button.getModel(), true);
    }

    public boolean isCommandModeToggle() {
        return commandModeToggleCheckBox.isSelected();
    }

    public void setCommandModeToggle(boolean selected) {
        commandModeToggleCheckBox.setSelected(selected);
    }
}
