/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;

public class GotoAction extends AnAction {
    private final ErgoKeysPlugin plugin;

    public GotoAction() {
            this.plugin = ApplicationManager.getApplication().getComponent(ErgoKeysPlugin.class);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        this.plugin.activateInsertMode(editor);

        AnAction action = ActionManager.getInstance().getAction("GotoAction");
        action.actionPerformed(e);
    }
}
