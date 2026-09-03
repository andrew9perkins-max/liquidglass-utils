package com.andrew9perkins.liquidglass.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import com.andrew9perkins.liquidglass.widget.GlassPanel;
import net.minecraft.client.MinecraftClient;
import com.andrew9perkins.liquidglass.module.ModuleManager;
import com.andrew9perkins.liquidglass.module.Module;
import java.util.List;

public class DashboardScreen extends Screen {
    protected DashboardScreen() { super(Text.of("Liquid Glass")); }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        MinecraftClient client = MinecraftClient.getInstance();

        // Draw a central glass panel as the main dashboard
        int w = Math.max(400, this.width * 3 / 4);
        int h = Math.max(240, this.height * 3 / 4);
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        GlassPanel.drawGlass(matrices, x, y, w, h, 14);

        // Left sidebar width
        int sidebarW = 160;
        GlassPanel.drawGlass(matrices, x + 12, y + 12, sidebarW - 24, h - 24, 10);

        // Render module list in sidebar
        List<Module> modules = ModuleManager.getModules();
        int listX = x + 24;
        int listY = y + 24;
        int itemH = 28;
        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            int iy = listY + i * (itemH + 6);
            int iw = sidebarW - 48;
            // draw per-item glass pill
            GlassPanel.drawGlass(matrices, listX, iy, iw, itemH, 8);
            // draw module name text
            client.textRenderer.draw(matrices, m.getId(), listX + 8, iy + 8, 0xFF1C1C1E);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Close on ESC
        if (keyCode == 256) { // GLFW_KEY_ESCAPE
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
