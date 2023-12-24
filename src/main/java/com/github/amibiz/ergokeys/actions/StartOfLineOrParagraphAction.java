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
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.util.DocumentUtil;

public class StartOfLineOrParagraphAction extends DumbAwareAction {
    private static final Logger LOG = Logger.getInstance(StartOfLineOrParagraphAction.class);

    final private ActionManager actionManager = ActionManager.getInstance();

    @Override
    public void actionPerformed(AnActionEvent e) {
        LOG.debug("actionPerformed: event.getInputEvent=", e.getInputEvent());

        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Document document = editor.getDocument();
        final CaretModel caretModel = editor.getCaretModel();
        final Caret caret = caretModel.getCurrentCaret();

        String ideActionId = IdeActions.ACTION_EDITOR_MOVE_LINE_START;
        if (DocumentUtil.isAtLineStart(caret.getOffset(), document)) {
            ideActionId = IdeActions.ACTION_EDITOR_BACKWARD_PARAGRAPH;
        }

        actionManager.getAction(ideActionId).actionPerformed(e);
    }
}
