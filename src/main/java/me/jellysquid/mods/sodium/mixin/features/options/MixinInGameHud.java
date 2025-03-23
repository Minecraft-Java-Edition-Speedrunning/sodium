package me.jellysquid.mods.sodium.mixin.features.options;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isFancyGraphicsEnabled()Z"))
    private boolean redirectFancyGraphicsVignette(boolean original) {
        return original && SodiumClientMod.options().quality.enableVignette;
    }
}
