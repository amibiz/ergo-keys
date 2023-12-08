package com.github.amibiz.ergokeys;

import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManagerListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActiveKeymapChangedListener implements KeymapManagerListener {
    private static final Logger LOG = Logger.getInstance(ActiveKeymapChangedListener.class);

    @Override
    public void activeKeymapChanged(@Nullable Keymap keymap) {
        LOG.debug("activeKeymapChanged: keymap=", keymap != null ? keymap.getName() : "null");

        ErgoKeysService service = ApplicationManager.
                getApplication().
                getService(ErgoKeysService.class);

        if (keymap == null || keymap.equals(service.getCommandModeKeymap()) || keymap.equals(service.getInsertModeKeymap())) {
            return;
        }

        String key;
        if (service.isErgoKeysKeymap(keymap)) {
            service.setCommandModeKeymap(keymap);
            key = "commandModeKeymapName";
        } else if (service.getInsertModeKeymap() != null) {
            purgeCommandModeShortcuts(service.getInsertModeKeymap());
            service.setInsertModeKeymap(keymap);
            key = "insertModeKeymapName";
            service.extendCommandModeShortcuts(service.getInsertModeKeymap());
            service.activateInsertMode(service.getLastEditorUsed());
        } else {
            LOG.debug("activeKeymapChanged: missing insert mode keymap");
            return;
        }
        service.storePersistentProperty(key, keymap.getName());
    }

    private void purgeCommandModeShortcuts(@NotNull Keymap dst) {
        ErgoKeysService service = ApplicationManager.
                getApplication().
                getService(ErgoKeysService.class);

        for (Keymap keymap : service.getAllErgoKeysKeymaps()) {
            this.purgeShortcuts(keymap, dst);
        }
    }

    private void purgeShortcuts(@NotNull Keymap dst, Keymap src) {
        for (String actionId : src.getActionIds()) {
            for (Shortcut shortcut : src.getShortcuts(actionId)) {
                dst.removeShortcut(actionId, shortcut);
            }
        }
    }
}
