package com.andrew9perkins.liquidglass.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import com.andrew9perkins.liquidglass.module.ping.PingModule;
import com.andrew9perkins.liquidglass.module.ModuleManager;
import net.minecraft.client.gui.DrawableHelper;

public class PingHud implements HudRenderCallback {
    private final PingModule pingModule;

    public PingHud(PingModule pm) {
        this.pingModule = pm;
    }

    @Override
    public void onHudRender(MatrixStack matrices, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (!pingModule.isEnabled()) return;

        int x = 10;
        int y = 10;
        int w = 120;
        int h = 28;

        // background glass pill
        DrawableHelper.fill(matrices, x, y, x + w, y + h, 0x8AD6EBFF);
        // outline
        DrawableHelper.fill(matrices, x, y, x + w, y + 1, 0x40FFFFFF);

        int current = 0;
        int[] snap = pingModule.getSamples().snapshot();
        if (snap.length > 0) current = snap[snap.length - 1];

        String s = "Ping: " + current + "ms";
        client.textRenderer.draw(matrices, s, x + 8, y + 8, 0xFF1C1C1E);
    }

    public static void register(PingModule pm) {
        HudRenderCallback.EVENT.register(new PingHud(pm));
    }
}
