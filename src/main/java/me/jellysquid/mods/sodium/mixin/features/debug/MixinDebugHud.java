package me.jellysquid.mods.sodium.mixin.features.debug;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(DebugHud.class)
public abstract class MixinDebugHud {
    @Redirect(method = "renderRightText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;getRightText()Ljava/util/List;"))
    private List<String> redirectRightTextEarly(DebugHud instance) {
        List<String> strings = ((DebugHudAccessor) instance).invokeGetRightText();
        strings.add("");
        strings.add("MCSR Sodium " + SodiumClientMod.getVersion());
        return strings;
    }
}
