package com.github.amibiz.ergokeys;

import com.intellij.find.actions.FindInPathAction;
import com.intellij.ide.actions.SearchEverywhereAction;
import com.intellij.ide.actions.Switcher;
import com.intellij.ide.actions.ViewStructureAction;
import com.intellij.ide.actions.runAnything.RunAnythingAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.IncrementalFindAction;
import org.jetbrains.annotations.NotNull;

public class ActionListener implements AnActionListener {
    private static final Logger LOG = Logger.getInstance(ActionListener.class);

    @Override
    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
        LOG.debug("beforeActionPerformed:", " action=", action.getClass(), " event=", event.getInputEvent());

        if (action.getClass().equals(SearchEverywhereAction.class) ||
                action.getClass().equals(RunAnythingAction.class) ||
                action.getClass().equals(IncrementalFindAction.class) ||
                action.getClass().equals(FindInPathAction.class) ||
                action.getClass().equals(ViewStructureAction.class) ||
                action.getClass().equals(Switcher.class)) {
            final Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
            if (editor != null) {
                ErgoKeysService service = ErgoKeysService.getInstance();
                service.activateInsertMode(editor);
            }
        }

        AnActionListener.super.beforeActionPerformed(action, event);
    }

    @Override
    public void afterActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event, @NotNull AnActionResult result) {
        LOG.debug("afterActionPerformed:", " action=", action.getClass(), " event=",
                event.getInputEvent(), " result=", result.getClass());

        AnActionListener.super.afterActionPerformed(action, event, result);
    }

    @Override
    public void beforeEditorTyping(char c, @NotNull DataContext dataContext) {
        LOG.debug("beforeEditorTyping: c=", c);
    }
}
