package me.jellysquid.mods.sodium.mixin.features.particle.cull;

import net.minecraft.client.render.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Frustum.class)
public interface FrustumAccessor {
    @Invoker
    boolean callIsVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
}
