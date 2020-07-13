package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;

public class FindInPathAction extends AnAction {
    private final ErgoKeysPlugin plugin;

    public FindInPathAction() {
        this.plugin = ApplicationManager.getApplication().getComponent(ErgoKeysPlugin.class);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get all the required data from data keys
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);

        this.plugin.activateInsertMode(editor);

        AnAction action = ActionManager.getInstance().getAction("FindInPath");
        action.actionPerformed(e);
    }
}
