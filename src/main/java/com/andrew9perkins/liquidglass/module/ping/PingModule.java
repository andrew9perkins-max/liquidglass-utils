package com.andrew9perkins.liquidglass.module.ping;

import com.andrew9perkins.liquidglass.module.Module;
import com.andrew9perkins.liquidglass.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.network.PlayerListEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.Identifier;

public class PingModule extends Module {
    private final RingBuffer samples;
    private int tickCounter = 0;
    private long sessionStart = 0L;

    public PingModule() {
        super("ping");
        this.samples = new RingBuffer(ConfigManager.get().ping.ringBufferSize);
        setEnabled(ConfigManager.get().ping.enabled);
    }

    @Override
    public void onEnable() {
        sessionStart = System.currentTimeMillis();
        System.out.println("[LiquidGlass|Ping] Enabled");
    }

    @Override
    public void onDisable() {
        System.out.println("[LiquidGlass|Ping] Disabled");
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled()) return;
        tickCounter++;
        int interval = Math.max(1, ConfigManager.get().ping.sampleIntervalTicks);
        if (tickCounter % interval != 0) return;

        if (client.player == null || client.getNetworkHandler() == null) return;
        try {
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) {
                int latency = entry.getLatency();
                samples.add(latency);
            }
        } catch (Throwable t) {
            // safe guard
            t.printStackTrace();
        }
    }

    public RingBuffer getSamples() { return samples; }

    // Simple ring buffer implementation
    public static class RingBuffer {
        private final int capacity;
        private final int[] data;
        private int index = 0;
        private int size = 0;

        public RingBuffer(int capacity) {
            this.capacity = capacity;
            this.data = new int[capacity];
        }

        public synchronized void add(int v) {
            data[index] = v;
            index = (index + 1) % capacity;
            if (size < capacity) size++;
        }

        public synchronized int[] snapshot() {
            int[] out = new int[size];
            for (int i = 0; i < size; i++) {
                int idx = (index - size + i);
                if (idx < 0) idx += capacity;
                out[i] = data[idx];
            }
            return out;
        }

        public synchronized int size() { return size; }
    }
}
