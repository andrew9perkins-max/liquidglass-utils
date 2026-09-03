package com.andrew9perkins.liquidglass.config;

public class ModConfig {
    public boolean masterEnable = true;
    public PingConfig ping = new PingConfig();

    public static class PingConfig {
        public boolean enabled = true;
        public int ringBufferSize = 120;
        public int sampleIntervalTicks = 1;
        public int historySize = 50;
    }
}
