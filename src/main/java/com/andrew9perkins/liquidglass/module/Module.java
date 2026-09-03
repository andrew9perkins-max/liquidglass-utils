package com.andrew9perkins.liquidglass.module;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    private final String id;
    private boolean enabled = false;

    protected Module(String id) { this.id = id; }

    public String getId() { return id; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient client) {}
    public void onHudRender(MinecraftClient client, float tickDelta) {}
    public void onConfigSave() {}

    // Connection lifecycle hooks
    public void onClientJoin(Object handler, MinecraftClient client) {}
    public void onClientDisconnect(MinecraftClient client) {}

    // Called on overall shutdown
    public void onShutdown(MinecraftClient client) {}
}
