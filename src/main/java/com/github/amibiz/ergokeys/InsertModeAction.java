/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;

public class InsertModeAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(InsertModeAction.class);

    private final ErgoKeysPlugin plugin;

    public InsertModeAction() {
        this.plugin = ApplicationManager.getApplication().getComponent(ErgoKeysPlugin.class);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        LOG.debug("actionPerformed");
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        plugin.deactivateCommandMode(editor);
    }
}
