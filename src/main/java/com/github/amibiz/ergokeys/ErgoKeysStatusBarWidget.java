/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ErgoKeysStatusBarWidget extends EditorBasedWidget {
    public static final String COMMAND_MODE_WIDGET_TEXT = "ErgoKeys:CMD";

    public static final String WIDGET_ID = "ErgoKeys";
    public static final String INSERT_MODE_WIDGET_TEXT = "ErgoKeys:INS";
    private static final Logger LOG = Logger.getInstance(ErgoKeysStatusBarWidget.class);

    protected ErgoKeysStatusBarWidget(@NotNull Project project) {
        super(project);

        ApplicationManager.getApplication().getMessageBus().connect().subscribe(KeymapManagerListener.TOPIC, new KeymapManagerListener() {
            @Override
            public void activeKeymapChanged(@Nullable Keymap keymap) {
                LOG.debug("activeKeymapChanged: keymap=", keymap != null ? keymap.getName() : "null");

                // Update status bar
                WindowManager.getInstance().getStatusBar(project).updateWidget(ErgoKeysStatusBarWidget.WIDGET_ID);
            }
        });
    }

    @NotNull
    @Override
    public String ID() {
        return WIDGET_ID;
    }

    @Nullable
    @Override
    public WidgetPresentation getPresentation() {
        return new TextPresentation() {
            @Nullable
            @Override
            public String getTooltipText() {
                return null;
            }

            @NotNull
            @Override
            public String getText() {
                LOG.debug("getText");

                ErgoKeysService service = ErgoKeysService.getInstance();
                if (service.inCommandMode()) {
                    return COMMAND_MODE_WIDGET_TEXT;
                }
                return INSERT_MODE_WIDGET_TEXT;
            }

            @Override
            public float getAlignment() {
                return 0;
            }
        };
    }
}
