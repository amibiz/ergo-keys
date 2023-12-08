/*
 * Copyright 2018 Ami E. Bizamcher. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */

package com.github.amibiz.ergokeys;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import org.jetbrains.annotations.NotNull;

public class AppStartedListener implements AppLifecycleListener {
    private static final Logger LOG = Logger.getInstance(AppStartedListener.class);

    private static final String DEFAULT_ERGOKEYS_KEYMAP = "ErgoKeys (QWERTY)";

    @Override
    public void appStarted() {
        LOG.debug("appStarted");

        KeymapManagerEx keymapManagerEx = KeymapManagerEx.getInstanceEx();

        ErgoKeysService service = ApplicationManager.
                getApplication().
                getService(ErgoKeysService.class);

        ActionManager.getInstance().registerAction("ErgoKeysNoopAction", new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // noop
            }
        });

        // NOTE: we use the keymap parent relationship to extend derived
        // shortcuts and to identify command mode keymaps. at this stage,
        // the keymaps parent reference is null (lazy).
        // to overcome this, we force all keymaps to load by calling the
        // getActionIdList() method.
        for (Keymap keymap : keymapManagerEx.getAllKeymaps()) {
            keymap.getActionIdList();
        }

        String insertModeKeymapName = service.loadPersistentProperty("insertModeKeymapName");
        if (insertModeKeymapName == null) {
            service.setInsertModeKeymap(keymapManagerEx.getActiveKeymap());
        } else {
            service.setInsertModeKeymap(keymapManagerEx.getKeymap(insertModeKeymapName));
            if (service.getInsertModeKeymap() == null) {
                service.setInsertModeKeymap(keymapManagerEx.getKeymap("$default"));
                assert service.getInsertModeKeymap() != null;
            }
        }
        service.storePersistentProperty("insertModeKeymapName", service.getInsertModeKeymap().getName());

        String commandModeKeymapName = service.loadPersistentProperty("commandModeKeymapName");
        if (commandModeKeymapName == null) {
            service.setCommandModeKeymap(keymapManagerEx.getKeymap(DEFAULT_ERGOKEYS_KEYMAP));
            assert service.getCommandModeKeymap() != null;
        } else {
            service.setCommandModeKeymap(keymapManagerEx.getKeymap(commandModeKeymapName));
            if (service.getCommandModeKeymap() == null) {
                service.setCommandModeKeymap(keymapManagerEx.getKeymap(DEFAULT_ERGOKEYS_KEYMAP));
                assert service.getCommandModeKeymap() != null;
            }
        }
        service.storePersistentProperty("commandModeKeymapName", service.getCommandModeKeymap().getName());

        service.extendCommandModeShortcuts(service.getInsertModeKeymap());

        AppLifecycleListener.super.appStarted();
    }
}
