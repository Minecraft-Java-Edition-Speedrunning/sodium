package me.jellysquid.mods.sodium.mixin.features.options;

import net.minecraft.client.options.DoubleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DoubleOption.class)
public interface DoubleOptionAccessor {
    @Mutable
    @Accessor
    void setStep(float step);
}
