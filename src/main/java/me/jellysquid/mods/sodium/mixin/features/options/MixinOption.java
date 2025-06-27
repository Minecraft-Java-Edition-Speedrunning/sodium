package me.jellysquid.mods.sodium.mixin.features.options;

import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.Option;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Option.class)
public abstract class MixinOption {
    @Redirect(method = "method_18545", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/DoubleOption;getRatio(D)D"))
    private static double correctGammaText(DoubleOption instance, double value) {
        return instance.getRatio(value) * instance.getMax(); // 1 when in a world
    }
}
