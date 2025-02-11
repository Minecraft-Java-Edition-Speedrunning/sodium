package me.jellysquid.mods.sodium.client.gui;

import me.contaria.speedrunapi.config.SpeedrunConfigAPI;
import me.contaria.speedrunapi.config.api.SpeedrunConfig;
import me.contaria.speedrunapi.config.api.SpeedrunConfigStorage;
import me.contaria.speedrunapi.config.api.SpeedrunOption;
import me.contaria.speedrunapi.config.api.annotations.Config;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.chunk.backends.gl20.GL20ChunkRenderBackend;
import me.jellysquid.mods.sodium.client.render.chunk.backends.gl30.GL30ChunkRenderBackend;
import me.jellysquid.mods.sodium.client.render.chunk.backends.gl43.GL43ChunkRenderBackend;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.WorldRenderer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

// ensure mac sodium is launched before StandardSettings with priority
@Config(init = Config.InitPoint.POSTLAUNCH, priority = 500)
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
        public ChunkRendererBackendOption chunkRendererBackend = ChunkRendererBackendOption.BEST;
        public boolean useChunkFaceCulling = true;
        public boolean useCompactVertexFormat = true;
        public boolean useFogOcclusion = true;
        public boolean useEntityCulling = false;
        public boolean useParticleCulling = true;
        public boolean animateOnlyVisibleTextures = true;
        public boolean useMemoryIntrinsics = true;
        public boolean disableDriverBlacklist = false;

        @Override
        public @Nullable SpeedrunOption<?> parseField(Field field, SpeedrunConfig config, String... idPrefix) {
            if (ChunkRendererBackendOption.class.equals(field.getType())) {
                return new SpeedrunConfigAPI.CustomOption.Builder<ChunkRendererBackendOption>(config, this, field, idPrefix)
                        .createWidget((option, innerConfig, configStorage, optionField) -> new ButtonWidget(0, 0, 150, 20, option.getText(), button -> {
                            ChunkRendererBackendOption[] options = ChunkRendererBackendOption.getAvailableOptions(SodiumClientMod.options().advanced.disableDriverBlacklist);
                            ChunkRendererBackendOption current = option.get();
                            int index = -1;
                            for (int i = 0; i < options.length; ++i) {
                                if (options[i].equals(current)) {
                                    index = i;
                                }
                            }
                            option.set(options[(index + 1) % options.length]);
                            button.setMessage(option.getText());
                        }))
                        .build();
            }
            // this is how you call default super methods
            return SpeedrunConfigStorage.super.parseField(field, config, idPrefix);
        }
    }

    public static class QualitySettings implements SpeedrunConfigStorage {
        public boolean enableVignette = false;
    }

    public static class SpeedrunSettings implements SpeedrunConfigStorage {
        public boolean usePlanarFog = true;
        public boolean showEntityCulling = true;
        public boolean showFogOcclusion = true;
    }

    public enum ChunkRendererBackendOption {
        GL43(GL43ChunkRenderBackend::isSupported),
        GL30(GL30ChunkRenderBackend::isSupported),
        GL20(GL20ChunkRenderBackend::isSupported);

        public static final ChunkRendererBackendOption BEST = pickBestBackend();

        private final SupportCheck supportedFunc;

        ChunkRendererBackendOption(SupportCheck supportedFunc) {
            this.supportedFunc = supportedFunc;
        }

        public boolean isSupported(boolean disableBlacklist) {
            return this.supportedFunc.isSupported(disableBlacklist);
        }

        public static ChunkRendererBackendOption[] getAvailableOptions(boolean disableBlacklist) {
            return streamAvailableOptions(disableBlacklist)
                    .toArray(ChunkRendererBackendOption[]::new);
        }

        public static Stream<ChunkRendererBackendOption> streamAvailableOptions(boolean disableBlacklist) {
            return Arrays.stream(ChunkRendererBackendOption.values())
                    .filter((o) -> o.isSupported(disableBlacklist));
        }

        private static ChunkRendererBackendOption pickBestBackend() {
            return streamAvailableOptions(false)
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
        }

        private interface SupportCheck {
            boolean isSupported(boolean disableBlacklist);
        }
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
        return "sodiummac";
    }
}
