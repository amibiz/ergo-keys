/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ErgoKeysStatusBarWidget extends EditorBasedStatusBarPopup {
    private static final Logger LOG = Logger.getInstance(ErgoKeysStatusBarWidget.class);

    public static final String WIDGET_ID = "ErgoKeys";

    private static final String COMMAND_MODE_WIDGET_PANEL_TEXT = "CMD";
    private static final String INSERT_MODE_WIDGET_PANEL_TEXT = "INS";
    private static final String COMMAND_MODE_WIDGET_TOOLTIP_TEXT = "Command Mode";
    private static final String INSERT_MODE_WIDGET_TOOLTIP_TEXT = "Insert Mode";
    private static final String ERGOKEYS_PLUGIN_ID = "com.github.amibiz.ergokeys";

    protected ErgoKeysStatusBarWidget(@NotNull Project project, @NotNull CoroutineScope scope) {
        super(project, true, scope);

        ApplicationManager.getApplication().getMessageBus().connect().subscribe(
                KeymapManagerListener.TOPIC,
                new KeymapManagerListener() {
                    @Override
                    public void activeKeymapChanged(@Nullable Keymap keymap) {
                        LOG.debug("activeKeymapChanged: keymap=", keymap != null ? keymap.getName() : "null");

                        // Update status bar
                        update();
                    }
                }
        );
    }

    @NotNull
    private static PluginId getPluginId() {
        return PluginId.getId(ERGOKEYS_PLUGIN_ID);
    }

    @NotNull
    private static String getVersion() {
        final IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(getPluginId());
        return plugin != null ? plugin.getVersion() : "SNAPSHOT";
    }

    @Override
    @NotNull
    protected WidgetState getWidgetState(@Nullable VirtualFile file) {
        String toolTipText = ErgoKeysService.getInstance().inCommandMode() ? COMMAND_MODE_WIDGET_TOOLTIP_TEXT : INSERT_MODE_WIDGET_TOOLTIP_TEXT;
        String panelText = ErgoKeysService.getInstance().inCommandMode() ? COMMAND_MODE_WIDGET_PANEL_TEXT : INSERT_MODE_WIDGET_PANEL_TEXT;
        return new WidgetState(toolTipText, panelText, true);
    }

    @Override
    @NotNull
    protected StatusBarWidget createInstance(@NotNull Project project) {
        return new ErgoKeysStatusBarWidget(project, getScope());
    }

    @Override
    @Nullable
    protected ListPopup createPopup(@NotNull DataContext context) {
        ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(
                "ErgoKeys",
                new DefaultActionGroup(),
                context,
                null,
                false
        );
        popup.setAdText(getVersion(), SwingConstants.CENTER);
        return popup;
    }

    @NotNull
    @Override
    public String ID() {
        return WIDGET_ID;
    }
}
