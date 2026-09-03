package com.andrew9perkins.liquidglass.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class ConfigManager {
    public static final Path CONFIG_PATH = Paths.get("config", "liquidglass.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModConfig CONFIG = new ModConfig();

    public static void init() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                CONFIG = GSON.fromJson(json, ModConfig.class);
            } else {
                save();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ModConfig get() { return CONFIG; }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(CONFIG));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
