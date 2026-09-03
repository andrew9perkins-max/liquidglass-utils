package com.andrew9perkins.liquidglass.module.ping;

import com.andrew9perkins.liquidglass.module.Module;
import com.andrew9perkins.liquidglass.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PingModule extends Module {
    private final RingBuffer samples;
    private int tickCounter = 0;
    private long sessionStart = 0L;
    private final List<PingSession> history = new ArrayList<>();
    private static final Path SESSIONS_PATH = Paths.get("config", "liquidglass_sessions.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public PingModule() {
        super("ping");
        this.samples = new RingBuffer(ConfigManager.get().ping.ringBufferSize);
        setEnabled(ConfigManager.get().ping.enabled);
        loadHistory();
    }

    @Override
    public void onEnable() {
        sessionStart = System.currentTimeMillis();
        samples.clear();
        System.out.println("[LiquidGlass|Ping] Enabled");
    }

    @Override
    public void onClientJoin(Object handler, MinecraftClient client) {
        // Start a new session on join
        sessionStart = System.currentTimeMillis();
        samples.clear();
    }

    @Override
    public void onClientDisconnect(MinecraftClient client) {
        // finalize session
        long duration = System.currentTimeMillis() - sessionStart;
        if (samples.size() > 0) {
            PingSession s = new PingSession();
            s.startTimestamp = sessionStart;
            s.durationMs = duration;
            int[] snap = samples.snapshot();
            s.sampleCount = snap.length;
            s.avg = computeAverage(snap);
            s.min = computeMin(snap);
            s.max = computeMax(snap);
            s.p95 = computePercentile(snap, 95);
            s.jitter = computeJitter(snap);
            s.samples = snap;
            history.add(0, s);
            if (history.size() > ConfigManager.get().ping.historySize) history.remove(history.size() - 1);
            saveHistory();
        }
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

    public List<PingSession> getHistory() { return List.copyOf(history); }

    private void loadHistory() {
        try {
            if (Files.exists(SESSIONS_PATH)) {
                String j = Files.readString(SESSIONS_PATH);
                PingSession[] arr = GSON.fromJson(j, PingSession[].class);
                if (arr != null) {
                    history.clear();
                    history.addAll(Arrays.asList(arr));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveHistory() {
        try {
            Files.createDirectories(SESSIONS_PATH.getParent());
            Files.writeString(SESSIONS_PATH, GSON.toJson(history));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static int computeAverage(int[] a) {
        if (a.length == 0) return 0;
        long s = 0; for (int v : a) s += v; return (int)(s / a.length);
    }
    private static int computeMin(int[] a) { if (a.length==0) return 0; int m = Integer.MAX_VALUE; for(int v:a) if(v<m)m=v; return m; }
    private static int computeMax(int[] a) { if (a.length==0) return 0; int m = Integer.MIN_VALUE; for(int v:a) if(v>m)m=v; return m; }
    private static int computePercentile(int[] a, int p) {
        if (a.length==0) return 0;
        int[] c = a.clone(); java.util.Arrays.sort(c);
        int idx = Math.max(0, Math.min(c.length - 1, (int)Math.ceil((p/100.0)*c.length)-1));
        return c[idx];
    }
    private static double computeJitter(int[] a) { if (a.length<2) return 0.0; double sum=0; for(int i=1;i<a.length;i++) sum += Math.abs(a[i]-a[i-1]); return sum/(a.length-1); }

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
        public synchronized void clear() { index = 0; size = 0; }
    }

    public static class PingSession {
        public long startTimestamp;
        public long durationMs;
        public int sampleCount;
        public int avg;
        public int min;
        public int max;
        public int p95;
        public double jitter;
        public int[] samples;

        public String formattedStart() {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(startTimestamp));
        }
    }
}
