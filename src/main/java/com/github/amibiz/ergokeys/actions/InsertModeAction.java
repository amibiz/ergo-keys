/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys.actions;

import com.github.amibiz.ergokeys.ErgoKeysService;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;

public class InsertModeAction extends DumbAwareAction {

    private static final Logger LOG = Logger.getInstance(InsertModeAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        LOG.debug("actionPerformed: event.getInputEvent=", e.getInputEvent());

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        ErgoKeysService service = ErgoKeysService.getInstance();
        service.activateInsertMode(editor);
    }
}
