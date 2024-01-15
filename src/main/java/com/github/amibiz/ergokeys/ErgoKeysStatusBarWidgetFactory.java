/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ErgoKeysStatusBarWidgetFactory implements StatusBarWidgetFactory {
    private static final Logger LOG = Logger.getInstance(ErgoKeysStatusBarWidgetFactory.class);

    @Override
    public @NotNull @NonNls String getId() {
        return "ErgoKeys";
    }

    @Override
    public @NotNull @NlsContexts.ConfigurableName String getDisplayName() {
        return "ErgoKeys";
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar statusBar) {
        return true;
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        LOG.debug("createWidget: project=", project);

        return new ErgoKeysStatusBarWidget(project);
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project, @NotNull CoroutineScope scope) {
        LOG.debug("createWidget: project=", project, " scope=", scope);

        return createWidget(project);
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        LOG.debug("disposeWidget: widget=", widget);

        Disposer.dispose(widget);
    }
}