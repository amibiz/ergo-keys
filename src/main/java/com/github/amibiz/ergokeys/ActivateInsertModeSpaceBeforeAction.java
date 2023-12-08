package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;

public class ActivateInsertModeSpaceBeforeAction extends DumbAwareAction {
    private static final Logger LOG = Logger.getInstance(ActivateInsertModeSpaceBeforeAction.class);
    final private ActionManager actionManager = ActionManager.getInstance();

    @Override
    public void actionPerformed(AnActionEvent e) {
        LOG.debug("actionPerformed: event.getInputEvent=", e.getInputEvent());

        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getProject();
        final Document document = editor.getDocument();
        final CaretModel caretModel = editor.getCaretModel();
        final Caret caret = caretModel.getCurrentCaret();

        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                document.insertString(caret.getOffset(), " ");
            }
        });
        actionManager.getAction("ErgoKeysInsertMode").actionPerformed(e);
    }
}
