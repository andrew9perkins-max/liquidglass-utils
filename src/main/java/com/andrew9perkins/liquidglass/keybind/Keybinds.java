package com.andrew9perkins.liquidglass.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import com.mojang.blaze3d.platform.InputConstants;

public class Keybinds {
    public static KeyBinding OPEN_DASHBOARD;

    public static void register() {
        OPEN_DASHBOARD = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.liquidglass.open_dashboard",
            InputUtil.Type.KEYSYM,
            InputConstants.KEY_BACKSLASH,
            "key.category.liquidglass.category"
        ));
    }
}
