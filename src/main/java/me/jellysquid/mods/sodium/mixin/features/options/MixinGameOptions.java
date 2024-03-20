package me.jellysquid.mods.sodium.mixin.features.options;

import net.minecraft.client.options.GameOptions;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameOptions.class)
public abstract class MixinGameOptions {
    private static float parseFloat(String string) {
        if (string.equals("true")) {
            return 1.0F;
        }
        if (string.equals("false")) {
            return 0.0F;
        }
        return Float.parseFloat(string);
    }

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;parseFloat(Ljava/lang/String;)F", ordinal = 2))
    float maxGamma(String string) {
        return MathHelper.clamp(parseFloat(string), 0 , 5);
    }
}
