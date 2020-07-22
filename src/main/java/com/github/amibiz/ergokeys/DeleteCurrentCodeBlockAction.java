package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.project.DumbAwareAction;

public class DeleteCurrentCodeBlockAction extends DumbAwareAction {
    final private ActionManager actionManager = ActionManager.getInstance();

    @Override
    public void actionPerformed(AnActionEvent e) {
        actionManager.getAction("EditorCodeBlockStart").actionPerformed(e);
        actionManager.getAction("EditorCodeBlockEndWithSelection").actionPerformed(e);
        actionManager.getAction(IdeActions.ACTION_DELETE).actionPerformed(e);
    }
}
