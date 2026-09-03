package com.andrew9perkins.liquidglass.core;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import com.andrew9perkins.liquidglass.module.ModuleManager;
import com.andrew9perkins.liquidglass.module.ping.PingModule;
import com.andrew9perkins.liquidglass.config.ConfigManager;
import com.andrew9perkins.liquidglass.keybind.Keybinds;
import net.minecraft.client.MinecraftClient;
import com.andrew9perkins.liquidglass.screen.DashboardScreen;

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

        // Register keybinds
        Keybinds.register();

        // Register lifecycle hooks for tick & connection
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModuleManager.tick(client);

            // Open dashboard when key pressed
            try {
                if (Keybinds.OPEN_DASHBOARD.wasPressed()) {
                    client.setScreen(new DashboardScreen());
                }
            } catch (Throwable t) { t.printStackTrace(); }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ModuleManager.onJoin(handler, client));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ModuleManager.onDisconnect(client));

        // Save config & cleanup on stop
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            try {
                ModuleManager.onShutdown(client);
                ConfigManager.save();
            } catch (Throwable t) { t.printStackTrace(); }
        });

        System.out.println("[LiquidGlass] Initialized (UI + Ping)");
    }

    public static ModMain get() { return instance; }
}
