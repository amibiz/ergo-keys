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
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;

public class SelectStringAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        while (true) {
            AnAction action = ActionManager.getInstance().getAction("EditorSelectWord");
            action.actionPerformed(e);

            // Access document, caret, and selection
            final Document document = editor.getDocument();
            final SelectionModel selectionModel = editor.getSelectionModel();
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
