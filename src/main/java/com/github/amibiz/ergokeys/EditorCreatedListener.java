/*
 * Copyright (c) 2018-present The ErgoKeys authors
 *
 * All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import org.jetbrains.annotations.NotNull;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class EditorCreatedListener implements EditorFactoryListener {
    private static final Logger LOG = Logger.getInstance(EditorCreatedListener.class);

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        LOG.debug("editorCreated: event=", event);

        Editor editor = event.getEditor();

        // Enable character based shortcuts (disabled by default since 2021.2)
        // for newly created editors.
        editor.getContentComponent().putClientProperty(ActionUtil.ALLOW_PlAIN_LETTER_SHORTCUTS, true);

        editor.getContentComponent().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                LOG.debug("focusGained: focusEvent=", focusEvent);
                ErgoKeysService.getInstance().editorFocusGained(editor, focusEvent);
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                LOG.debug("focusLost: focusEvent=", focusEvent);
                ErgoKeysService.getInstance().editorFocusLost(focusEvent);
            }
        });
    }
}
