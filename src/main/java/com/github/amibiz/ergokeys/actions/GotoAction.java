/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys.actions;

import com.github.amibiz.ergokeys.ErgoKeysService;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;

public class GotoAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(GotoAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        LOG.debug("actionPerformed: event.getInputEvent=", e.getInputEvent());

        // Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        ErgoKeysService service = ErgoKeysService.getInstance();
        service.activateInsertMode(editor);

        AnAction action = ActionManager.getInstance().getAction("GotoAction");
        action.actionPerformed(e);
    }
}
