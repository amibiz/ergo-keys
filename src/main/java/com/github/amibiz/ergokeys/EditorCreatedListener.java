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
                ErgoKeysService service = ErgoKeysService.getInstance();

                LOG.debug("focusGained: focusEvent=", focusEvent);
                editor.getSettings().setBlockCursor(service.inCommandMode());
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                LOG.debug("focusLost: focusEvent=", focusEvent);

                ErgoKeysService service = ErgoKeysService.getInstance();

                if (focusEvent.getOppositeComponent() != null &&
                        focusEvent.getOppositeComponent().getClass().getName().equals("com.intellij.terminal.JBTerminalPanel")) {
                    service.setActiveKeymap(service.getInsertModeKeymap());
                }
                service.setLastEditorUsed(editor);
            }
        });

        EditorFactoryListener.super.editorCreated(event);
    }
}
