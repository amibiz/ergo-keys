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
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManagerListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeymapListener implements KeymapManagerListener {
    private static final Logger LOG = Logger.getInstance(KeymapListener.class);

    private boolean firstTime = true;
    @Override
    public void activeKeymapChanged(@Nullable Keymap keymap) {
        LOG.debug("activeKeymapChanged: keymap=", keymap != null ? keymap.getName() : "null");

        // When activeKeymapChanged() is called for the first time,
        // calling KeymapManagerEx.getInstanceEx() causes PluginException
        // with "Cyclic service initialization" error.
        // Because we are calling ErgoKeysService.getInstance() and
        // that method calls KeymapManagerEx we get the exception above.
        // The solution is not to handle the first time activeKeymapChanged()
        // is called.
        if (firstTime) {
            LOG.debug("activeKeymapChanged: first call");
            firstTime = false;
            return;
        }

        ErgoKeysService.getInstance().activeKeymapChanged(keymap);
    }

    @Override
    public void keymapAdded(@NotNull Keymap keymap) {
        LOG.debug("keymapAdded");
        ErgoKeysService.getInstance().keymapAdded(keymap);
    }

    @Override
    public void keymapRemoved(@NotNull Keymap keymap) {
        LOG.debug("keymapRemoved");
        ErgoKeysService.getInstance().keymapRemoved(keymap);
    }
}
