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

    @Override
    public void actionPerformed(AnActionEvent e) {
        LOG.debug("actionPerformed: event.getInputEvent=", e.getInputEvent());

        final ActionManager actionManager = ActionManager.getInstance();

        actionManager.tryToExecute(actionManager.getAction("EditorCodeBlockStart"), null, null, null, true);
        actionManager.tryToExecute(actionManager.getAction("EditorCodeBlockEndWithSelection"), null, null, null, true);
        actionManager.tryToExecute(actionManager.getAction(IdeActions.ACTION_DELETE), null, null, null, true);
    }
}
