package com.andrew9perkins.liquidglass.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import com.andrew9perkins.liquidglass.widget.GlassPanel;
import net.minecraft.client.MinecraftClient;

public class DashboardScreen extends Screen {
    protected DashboardScreen() { super(Text.of("Liquid Glass")); }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        MinecraftClient client = MinecraftClient.getInstance();

        // Draw a central glass panel as a placeholder
        int w = this.width * 3 / 4;
        int h = this.height * 3 / 4;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        GlassPanel.drawGlass(matrices, x, y, w, h, 12);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
