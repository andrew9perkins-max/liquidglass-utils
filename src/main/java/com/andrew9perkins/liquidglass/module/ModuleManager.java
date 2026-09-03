package com.andrew9perkins.liquidglass.module;

import net.minecraft.client.MinecraftClient;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    public static void init() {
        MODULES.clear();
    }

    public static void register(Module m) {
        MODULES.add(m);
    }

    public static List<Module> getModules() { return List.copyOf(MODULES); }

    public static void tick(MinecraftClient client) {
        for (Module m : MODULES) {
            try { m.onTick(client); } catch (Throwable t) { t.printStackTrace(); }
        }
    }

    public static void onJoin(Object handler, MinecraftClient client) {
        for (Module m : MODULES) {
            try {
                // Modules may implement connection handling by checking instance types
            } catch (Throwable t) { t.printStackTrace(); }
        }
    }

    public static void onDisconnect(MinecraftClient client) {
        for (Module m : MODULES) {
            try {
                // Finalize sessions if needed
            } catch (Throwable t) { t.printStackTrace(); }
        }
    }
}
