/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.TextRange;

public class SelectStringAction extends DumbAwareAction {
    private static final Logger LOG = Logger.getInstance(ErgoKeysPlugin.class);
    final private ActionManager actionManager = ActionManager.getInstance();

    @Override
    public void actionPerformed(AnActionEvent e) {
        LOG.debug("actionPerformed: event.getInputEvent=", e.getInputEvent());

        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();

        while (true) {
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
