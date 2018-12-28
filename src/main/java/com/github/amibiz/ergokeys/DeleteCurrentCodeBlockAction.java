package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;

public class DeleteCurrentCodeBlockAction extends AnAction {
    final private ActionManager actionManager = ActionManager.getInstance();

    @Override
    public void actionPerformed(AnActionEvent e) {
        actionManager.getAction("EditorCodeBlockStart").actionPerformed(e);
        actionManager.getAction("EditorCodeBlockEndWithSelection").actionPerformed(e);
        actionManager.getAction(IdeActions.ACTION_DELETE).actionPerformed(e);
    }
}
