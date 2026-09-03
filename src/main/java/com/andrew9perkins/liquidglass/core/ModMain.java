package com.andrew9perkins.liquidglass.core;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import com.andrew9perkins.liquidglass.module.ModuleManager;
import com.andrew9perkins.liquidglass.module.ping.PingModule;
import com.andrew9perkins.liquidglass.config.ConfigManager;

public class ModMain implements ClientModInitializer {

    public static final String MOD_ID = "liquidglass";
    private static ModMain instance;

    public ModMain() {
        instance = this;
    }

    @Override
    public void onInitializeClient() {
        // Load config
        ConfigManager.init();

        // Register modules
        ModuleManager.init();
        ModuleManager.register(new PingModule());

        // Register lifecycle hooks for tick & connection
        ClientTickEvents.END_CLIENT_TICK.register(client -> ModuleManager.tick(client));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ModuleManager.onJoin(handler, client));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ModuleManager.onDisconnect(client));

        // Register keybinds
        KeyBinding openDashboard = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.liquidglass.open_dashboard",
            InputUtil.Type.KEYSYM,
            InputUtil.fromTranslationKey("\\".replace("\\\\","\\")),
            "key.category.liquidglass.category"
        ));

        // Additional keybinds to be registered by modules

        System.out.println("[LiquidGlass] Initialized");
    }

    public static ModMain get() { return instance; }
}
