package me.jellysquid.mods.sodium.client.gui;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.gui.options.TextProvider;
import me.jellysquid.mods.sodium.client.render.chunk.backends.gl20.GL20ChunkRenderBackend;
import me.jellysquid.mods.sodium.client.render.chunk.backends.gl30.GL30ChunkRenderBackend;
import me.jellysquid.mods.sodium.client.render.chunk.backends.gl43.GL43ChunkRenderBackend;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

public class SodiumGameOptions {
    public final AdvancedSettings advanced = new AdvancedSettings();
    public final UnofficialSettings unofficial = new UnofficialSettings();

    private File file;

    public void notifyListeners() {
        SodiumClientMod.onConfigChanged(this);
    }

    public static class AdvancedSettings {
        public ChunkRendererBackendOption chunkRendererBackend = ChunkRendererBackendOption.BEST;
        public boolean animateOnlyVisibleTextures = true;
        public boolean useAdvancedEntityCulling = false;
        public boolean useParticleCulling = true;
        public boolean useFogOcclusion = true;
        public boolean useCompactVertexFormat = true;
        public boolean useChunkFaceCulling = true;
        public boolean useMemoryIntrinsics = true;
        public boolean disableDriverBlacklist = false;
    }

    public static class UnofficialSettings {
        public boolean usePlanarFog = false;
    }

    public enum ChunkRendererBackendOption implements TextProvider {
        GL43("Multidraw (GL 4.3)", GL43ChunkRenderBackend::isSupported),
        GL30("Oneshot (GL 3.0)", GL30ChunkRenderBackend::isSupported),
        GL20("Oneshot (GL 2.0)", GL20ChunkRenderBackend::isSupported);

        public static final ChunkRendererBackendOption BEST = pickBestBackend();

        private final String name;
        private final SupportCheck supportedFunc;

        ChunkRendererBackendOption(String name, SupportCheck supportedFunc) {
            this.name = name;
            this.supportedFunc = supportedFunc;
        }

        @Override
        public String getLocalizedName() {
            return this.name;
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

    public enum DefaultGraphicsQuality implements TextProvider {
        FAST("Fast"),
        FANCY("Fancy");

        private final String name;

        DefaultGraphicsQuality(String name) {
            this.name = name;
        }

        @Override
        public String getLocalizedName() {
            return this.name;
        }
    }

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();

    public static SodiumGameOptions load(File file) {
        SodiumGameOptions config = null;

        boolean exists = file.exists();
        if (exists) {
            try (FileReader reader = new FileReader(file)) {
                config = gson.fromJson(reader, SodiumGameOptions.class);
            } catch (IOException | JsonSyntaxException e) {
                SodiumClientMod.logger().warn("Could not parse config, falling back to default");
            }
        }
        if (!exists || config == null) {
            config = new SodiumGameOptions();
        }

        config.sanitize();
        config.file = file;
        config.writeChanges();

        return config;
    }

    private void sanitize() {
        if (this.advanced.chunkRendererBackend == null) {
            this.advanced.chunkRendererBackend = ChunkRendererBackendOption.BEST;
        }
    }

    public void writeChanges() {
        File dir = this.file.getParentFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Could not create parent directories");
            }
        } else if (!dir.isDirectory()) {
            throw new RuntimeException("The parent file is not a directory");
        }

        try (FileWriter writer = new FileWriter(this.file)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not save configuration file", e);
        }
    }
}
