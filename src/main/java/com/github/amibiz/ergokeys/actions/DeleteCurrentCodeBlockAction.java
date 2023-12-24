/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;

public class DeleteCurrentCodeBlockAction extends DumbAwareAction {
    private static final Logger LOG = Logger.getInstance(DeleteCurrentCodeBlockAction.class);

    final private ActionManager actionManager = ActionManager.getInstance();

    @Override
    public void actionPerformed(AnActionEvent e) {
        LOG.debug("actionPerformed: event.getInputEvent=", e.getInputEvent());

        actionManager.getAction("EditorCodeBlockStart").actionPerformed(e);
        actionManager.getAction("EditorCodeBlockEndWithSelection").actionPerformed(e);
        actionManager.getAction(IdeActions.ACTION_DELETE).actionPerformed(e);
    }
}
