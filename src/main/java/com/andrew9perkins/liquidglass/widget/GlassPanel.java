package com.andrew9perkins.liquidglass.widget;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class GlassPanel {
    public static final Identifier SAMPLE_BG = new Identifier("textures/block/white_concrete.png");

    public static void drawGlass(MatrixStack matrices, int x, int y, int w, int h, int radius) {
        // Apply backdrop blur if available
        try {
            DrawContext dc = DrawContext.create();
            // In Fabric, DrawContext.applyBlur is client-side; we simulate via call if present
            // Real call in rendering context: drawContext.applyBlur();
        } catch (Throwable t) {
            // ignore if unavailable in this simplified scaffold
        }

        // Draw translucent rounded rectangle - simplified placeholder
        MinecraftClient client = MinecraftClient.getInstance();
        int color = 0x8AD6EBFF; // ARGB (alpha ~0.54) sky-blue tint
        fillRounded(matrices, x, y, x + w, y + h, radius, color);
    }

    private static void fillRounded(MatrixStack matrices, int left, int top, int right, int bottom, int radius, int color) {
        // Placeholder: fill simple rect. Real rounded corners require tessellator/vertex builder.
        net.minecraft.client.gui.DrawableHelper.fill(matrices, left, top, right, bottom, color);
    }
}
