package me.jellysquid.mods.sodium.client.gui;

import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunConfigStorage;
import me.contaria.speedrunapi.config.api.annotations.Config;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.MultidrawChunkRenderBackend;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;

public class SodiumGameOptions implements SpeedrunConfig {
    @Config.Category("quality")
    public final QualitySettings quality = new QualitySettings();

    @Config.Category("advanced")
    public final AdvancedSettings advanced = new AdvancedSettings();

    @Config.Category("speedrun")
    public final SpeedrunSettings speedrun = new SpeedrunSettings();

    public static class AdvancedSettings implements SpeedrunConfigStorage {
        @Config.Numbers.Whole.Bounds(min = 0, max = 64)
        public int maxChunkThreads;
        @Config.Numbers.Whole.Bounds(min = 0, max = 64)
        public int targetChunkThreads;
        @Config.Numbers.Whole.Bounds(min = 0, max = 64)
        public int initialChunkThreads;
        @Config.Numbers.Whole.Bounds(min = 1, max = 2000)
        public int quickThreadCreationInterval = 100;
        @Config.Numbers.Whole.Bounds(min = 1, max = 10000)
        public int slowThreadCreationInterval = 1000;
        public boolean useChunkMultidraw = true;
        public boolean useBlockFaceCulling = true;
        public boolean useCompactVertexFormat = true;
        public boolean useFogOcclusion = true;
        public boolean useEntityCulling = false;
        public boolean useParticleCulling = true;
        public boolean animateOnlyVisibleTextures = true;
        public boolean allowDirectMemoryAccess = true;
        public boolean ignoreDriverBlacklist = false;
    }

    public static class QualitySettings implements SpeedrunConfigStorage {
        public boolean enableVignette = false;
    }

    public static class SpeedrunSettings implements SpeedrunConfigStorage {
        public boolean usePlanarFog = true;
        public boolean showEntityCulling = true;
        public boolean showFogOcclusion = true;
    }

    {
        SodiumClientMod.CONFIG = this;
    }

    @Override
    public void finishSaving() {
        SodiumClientMod.onConfigChanged(this);
        WorldRenderer worldRenderer = MinecraftClient.getInstance().worldRenderer;
        if (worldRenderer != null) worldRenderer.reload();
    }

    @Override
    public String modID() {
        return "sodium";
    }

    @Override
    public boolean shouldShowOption(String option) {
        if (option.equals("useChunkMultidraw")) {
            return MultidrawChunkRenderBackend.isSupported(this.advanced.ignoreDriverBlacklist);
        }
        return SpeedrunConfig.super.shouldShowOption(option);
    }
}
