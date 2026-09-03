package com.andrew9perkins.liquidglass.module.weather;

import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WeatherService {
    private static final HttpClient CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final Gson GSON = new Gson();

    public static class WeatherSnapshot {
        public boolean ok = true;
        public String errorMessage = null;
        public Instant fetchedAt;
        public CurrentWeather current;
        public List<DailyEntry> daily = new ArrayList<>();
    }

    public static class CurrentWeather {
        public double temperature; // °C or °F
        public double windSpeed;
        public int weatherCode;
    }

    public static class DailyEntry {
        public String date;
        public double tempMax;
        public double tempMin;
        public int weatherCode;
    }

    public CompletableFuture<WeatherSnapshot> fetchWeather(double lat, double lon, int days, boolean unitsMetric) {
        String units = unitsMetric ? "celsius" : "fahrenheit";
        // Open-Meteo API: current_weather=true, daily fields, timezone=auto
        String url = String.format(
            "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true&daily=temperature_2m_max,temperature_2m_min,weathercode&forecast_days=%d&timezone=auto&temperature_unit=%s",
            lat, lon, days, units
        );

        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .header("User-Agent", "LiquidGlass-Utils/1.0 (https://github.com/andrew9perkins-max/liquidglass-utils)")
            .build();

        return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString())
            .thenApply(resp -> {
                WeatherSnapshot snap = new WeatherSnapshot();
                snap.fetchedAt = Instant.now();
                if (resp.statusCode() != 200) {
                    snap.ok = false;
                    snap.errorMessage = "HTTP " + resp.statusCode();
                    return snap;
                }
                try {
                    OpenMeteoResponse r = GSON.fromJson(resp.body(), OpenMeteoResponse.class);
                    if (r == null) {
                        snap.ok = false;
                        snap.errorMessage = "Invalid JSON";
                        return snap;
                    }
                    if (r.current_weather != null) {
                        CurrentWeather cw = new CurrentWeather();
                        cw.temperature = r.current_weather.temperature;
                        cw.windSpeed = r.current_weather.windspeed;
                        cw.weatherCode = r.current_weather.weathercode;
                        snap.current = cw;
                    }
                    if (r.daily != null && r.daily.time != null) {
                        int n = Math.min(r.daily.time.length, r.daily.temperature_2m_max.length);
                        for (int i = 0; i < n; i++) {
                            DailyEntry de = new DailyEntry();
                            de.date = r.daily.time[i];
                            de.tempMax = r.daily.temperature_2m_max[i];
                            de.tempMin = r.daily.temperature_2m_min[i];
                            // Open-Meteo daily weathercode may be missing; handle gracefully
                            if (r.daily.weathercode != null && r.daily.weathercode.length > i) de.weatherCode = r.daily.weathercode[i];
                            snap.daily.add(de);
                        }
                    }
                    snap.ok = true;
                    return snap;
                } catch (Exception e) {
                    snap.ok = false;
                    snap.errorMessage = e.getMessage();
                    return snap;
                }
            })
            .exceptionally(ex -> {
                WeatherSnapshot snap = new WeatherSnapshot();
                snap.ok = false;
                snap.errorMessage = ex.getMessage();
                snap.fetchedAt = Instant.now();
                return snap;
            });
    }

    // Minimal JSON binding classes for Open-Meteo responses
    private static class OpenMeteoResponse {
        Current current_weather;
        Daily daily;

        static class Current {
            double temperature;
            double windspeed;
            int weathercode;
        }

        static class Daily {
            String[] time;
            double[] temperature_2m_max;
            double[] temperature_2m_min;
            int[] weathercode;
        }
    }
}
