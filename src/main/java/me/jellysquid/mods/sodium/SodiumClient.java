package me.jellysquid.mods.sodium;

import me.jellysquid.mods.sodium.config.SodiumRenderConfig;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import me.jellysquid.mods.sodium.interop.fabric.SodiumRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SodiumClient {
    private static SodiumRenderConfig CONFIG;
    private static Logger LOGGER;

    private static String MOD_VERSION;

    public static void init() {
        ModContainer mod = FabricLoader.getInstance()
                .getModContainer("sodium")
                .orElseThrow(NullPointerException::new);

        MOD_VERSION = mod.getMetadata()
                .getVersion()
                .getFriendlyString();

        LOGGER = LogManager.getLogger("Sodium");
        CONFIG = loadConfig();

        RendererAccess.INSTANCE.registerRenderer(SodiumRenderer.INSTANCE);
    }

    public static SodiumRenderConfig options() {
        if (CONFIG == null) {
            throw new IllegalStateException("Config not yet available");
        }

        return CONFIG;
    }

    public static Logger logger() {
        if (LOGGER == null) {
            throw new IllegalStateException("Logger not yet available");
        }

        return LOGGER;
    }

    private static SodiumRenderConfig loadConfig() {
        try {
            return SodiumRenderConfig.load();
        } catch (Exception e) {
            LOGGER.error("Failed to load configuration file", e);
            LOGGER.error("Using default configuration file in read-only mode");

            var config = new SodiumRenderConfig();
            config.setReadOnly();

            return config;
        }
    }

    public static void restoreDefaultOptions() {
        CONFIG = SodiumRenderConfig.defaults();

        try {
            CONFIG.writeChanges();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write config file", e);
        }
    }

    public static String getVersion() {
        if (MOD_VERSION == null) {
            throw new NullPointerException("Mod version hasn't been populated yet");
        }

        return MOD_VERSION;
    }

    public static boolean isDirectMemoryAccessEnabled() {
        return options().advanced.allowDirectMemoryAccess;
    }
}
