package com.andrew9perkins.liquidglass.widget;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class GlassPanel {

    public static void drawGlass(MatrixStack matrices, int x, int y, int w, int h, int radius) {
        // Placeholder: We try to approximate the Liquid Glass look using a translucent rounded panel
        // Real implementation would use a proper rounded rect tessellator and DrawContext.applyBlur if available.

        // draw backdrop shadow
        fill(matrices, x + 4, y + 6, x + w + 4, y + h + 6, 0x44000000);
        // draw main translucent fill (sky-blue tint)
        fill(matrices, x, y, x + w, y + h, 0x8AD6EBFF);
        // subtle specular top-left
        fill(matrices, x, y, x + w, y + 6, 0x40FFFFFF);
        // subtle darker bottom-right
        fill(matrices, x, y + h - 4, x + w, y + h, 0x30000000);

        // Rounded corners are not implemented in this placeholder; replace with proper rounded geometry in final version.
    }

    private static void fill(MatrixStack matrices, int left, int top, int right, int bottom, int color) {
        net.minecraft.client.gui.DrawableHelper.fill(matrices, left, top, right, bottom, color);
    }
}
