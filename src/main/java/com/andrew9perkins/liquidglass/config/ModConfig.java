package com.andrew9perkins.liquidglass.config;

public class ModConfig {
    public boolean masterEnable = true;
    public PingConfig ping = new PingConfig();
    public WeatherConfig weather = new WeatherConfig();

    public static class PingConfig {
        public boolean enabled = true;
        public int ringBufferSize = 120;
        public int sampleIntervalTicks = 1;
        public int historySize = 50;
    }

    public static class WeatherConfig {
        public boolean enabled = true;
        public int updateIntervalMinutes = 15; // fetch every 15 minutes by default
        public double latitude = 40.7128; // default: New York City
        public double longitude = -74.0060;
        public boolean unitsMetric = true; // true = metric (C), false = imperial (F)
        public int forecastDays = 3; // 1..7
    }
}
