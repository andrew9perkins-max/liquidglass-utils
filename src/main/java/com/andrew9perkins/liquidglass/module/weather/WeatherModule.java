package com.andrew9perkins.liquidglass.module.weather;

import com.andrew9perkins.liquidglass.module.Module;
import com.andrew9perkins.liquidglass.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import java.util.concurrent.CompletableFuture;
import java.time.Instant;

public class WeatherModule extends Module {
    private final WeatherService service;
    private WeatherService.WeatherSnapshot latest = null;
    private Instant lastFetch = Instant.EPOCH;
    private int tickCounter = 0;

    public WeatherModule() {
        super("weather");
        this.service = new WeatherService();
        setEnabled(ConfigManager.get().weather.enabled);
    }

    @Override
    public void onEnable() {
        scheduleFetch();
        System.out.println("[LiquidGlass|Weather] Enabled");
    }

    @Override
    public void onDisable() {
        latest = null;
        System.out.println("[LiquidGlass|Weather] Disabled");
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled()) return;
        tickCounter++;
        int intervalMinutes = Math.max(1, ConfigManager.get().weather.updateIntervalMinutes);
        // every 20*60 ticks per minute; check once per second (20 ticks)
        if (tickCounter % 20 != 0) return;
        if (Instant.now().minusSeconds(intervalMinutes * 60L).isAfter(lastFetch)) {
            scheduleFetch();
        }
    }

    private void scheduleFetch() {
        double lat = ConfigManager.get().weather.latitude;
        double lon = ConfigManager.get().weather.longitude;
        int days = Math.max(1, Math.min(7, ConfigManager.get().weather.forecastDays));
        boolean metric = ConfigManager.get().weather.unitsMetric;

        CompletableFuture<WeatherService.WeatherSnapshot> future = service.fetchWeather(lat, lon, days, metric);
        future.whenComplete((snapshot, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                lastFetch = Instant.now();
            } else {
                this.latest = snapshot;
                this.lastFetch = Instant.now();
                System.out.println("[LiquidGlass|Weather] Fetched weather: " + (snapshot != null && snapshot.current != null ? snapshot.current.temperature : "null"));
            }
        });
    }

    public WeatherService.WeatherSnapshot getLatestSnapshot() { return latest; }

    @Override
    public void onClientJoin(Object handler, MinecraftClient client) {
        // optionally fetch immediately on join
        scheduleFetch();
    }
}
