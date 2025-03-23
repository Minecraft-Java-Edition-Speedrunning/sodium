package me.jellysquid.mods.sodium.mixin.features.options;

import com.mojang.blaze3d.platform.GlStateManager;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.client.render.BackgroundRenderer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.NVFogDistance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer {
    @Redirect(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupNvFogDistance()V"))
    private static void redirectSetupNvFogDistance() {
        if (GL.getCapabilities().GL_NV_fog_distance) {
            int fogType = SodiumClientMod.options().speedrun.usePlanarFog ? NVFogDistance.GL_EYE_PLANE_ABSOLUTE_NV : NVFogDistance.GL_EYE_RADIAL_NV;
            GlStateManager.fogi(NVFogDistance.GL_FOG_DISTANCE_MODE_NV, fogType);
        }
    }
}
