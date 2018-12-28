package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.util.DocumentUtil;

public class StartOfLineOrParagraphAction extends AnAction {
    final private ActionManager actionManager = ActionManager.getInstance();

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Document document = editor.getDocument();
        final CaretModel caretModel = editor.getCaretModel();
        final Caret caret = caretModel.getCurrentCaret();

        if (DocumentUtil.isAtLineStart(caret.getOffset(), document)) {
            actionManager.getAction(IdeActions.ACTION_EDITOR_BACKWARD_PARAGRAPH).actionPerformed(e);
        } else {
            actionManager.getAction(IdeActions.ACTION_EDITOR_MOVE_LINE_START).actionPerformed(e);
        }
    }
}