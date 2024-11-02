/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.TextRange;

public class SelectStringAction extends DumbAwareAction {
    private static final Logger LOG = Logger.getInstance(SelectStringAction.class);
    private static final int MAX_ITERATIONS = 100;

    @Override
    public void actionPerformed(AnActionEvent e) {
        LOG.debug("actionPerformed: event.getInputEvent=", e.getInputEvent());

        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            LOG.debug("actionPerformed: editor is null");
            return;
        }

        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();
        final ActionManager actionManager = ActionManager.getInstance();

        int iterations = 0;

        while (iterations < MAX_ITERATIONS) {
            iterations++;

            AnAction action = actionManager.getAction(IdeActions.ACTION_EDITOR_SELECT_WORD_AT_CARET);
            action.actionPerformed(e);

            final int start = selectionModel.getSelectionStart();
            final int end = selectionModel.getSelectionEnd();

            if (start == 0) {
                selectionModel.removeSelection();
                return;
            }

            String mark = document.getText(TextRange.create(start - 1, start));
            switch (mark) {
                case "\"":
                case "'":
                case "`":
                    if (document.getText(TextRange.create(end, end + 1)).equals(mark)) {
                        return;
                    }
            }
        }
    }
}
