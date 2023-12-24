/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

public class NoopAction extends DumbAwareAction {
    private static final Logger LOG = Logger.getInstance(CommandModeAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LOG.debug("actionPerformed: event.getInputEvent=", e.getInputEvent());

        // noop
    }
}
