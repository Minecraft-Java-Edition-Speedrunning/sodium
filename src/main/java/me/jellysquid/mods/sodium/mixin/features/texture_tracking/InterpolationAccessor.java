package me.jellysquid.mods.sodium.mixin.features.texture_tracking;

import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sprite.Interpolation.class)
public interface InterpolationAccessor {
    @Invoker("method_24128")
    void callApply();
}
