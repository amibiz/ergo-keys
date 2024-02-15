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
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;

public class ErgoKeysFileStructurePopup extends DumbAwareAction {
    private static final Logger LOG = Logger.getInstance(ErgoKeysFileStructurePopup.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        LOG.debug("actionPerformed: event.getInputEvent=", e.getInputEvent());

        // Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        // Switch to insert mode
        ErgoKeysService service = ErgoKeysService.getInstance();
        service.activateInsertMode(editor, true);

        // Call file structure popup action directly
        final ActionManager actionManager = ActionManager.getInstance();
        actionManager.getAction(IdeActions.ACTION_FILE_STRUCTURE_POPUP).actionPerformed(e);
    }
}
