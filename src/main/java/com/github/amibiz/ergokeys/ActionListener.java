/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnActionResult;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

public class ActionListener implements AnActionListener {
    private static final Logger LOG = Logger.getInstance(ActionListener.class);

    @Override
    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {
        LOG.debug("beforeActionPerformed:", " action=", action.getClass(), " event=", event.getInputEvent());
        ErgoKeysService.getInstance().beforeActionPerformed(action, event);
    }

    @Override
    public void afterActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event, @NotNull AnActionResult result) {
        LOG.debug("afterActionPerformed:", " action=", action.getClass(), " event=",
                event.getInputEvent(), " result=", result.getClass());
    }

    @Override
    public void beforeEditorTyping(char c, @NotNull DataContext dataContext) {
        LOG.debug("beforeEditorTyping: c=", c);
    }
}
