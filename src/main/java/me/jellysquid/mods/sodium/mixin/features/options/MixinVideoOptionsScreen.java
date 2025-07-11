package me.jellysquid.mods.sodium.mixin.features.options;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.gui.VanillaOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoOptionsScreen;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(VideoOptionsScreen.class)
public class MixinVideoOptionsScreen extends GameOptionsScreen {

    public MixinVideoOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonListWidget;addAll([Lnet/minecraft/client/options/Option;)V"))
    private void optionsSwap(ButtonListWidget list, Option[] old_options) {
        Option.GAMMA.setMax(this.client.world == null ? 5 : 1);
        ((DoubleOptionAccessor) Option.GAMMA).setStep(this.client.world == null ? 0.01f : 0); // 0.01f step ensures we get an exact .2f so "Bright" shows up even when slider goes to 500
        List<Option> options = new ArrayList<>(Arrays.asList(old_options));
        SodiumGameOptions.SpeedrunSettings speedrunSettings = SodiumClientMod.options().speedrun;
        if (speedrunSettings.showEntityCulling) {
            options.add(VanillaOptions.ENTITY_CULLING);
        }
        if (speedrunSettings.showFogOcclusion) {
            options.add(VanillaOptions.FOG_OCCLUSION);
        }
        list.addAll(options.toArray(new Option[0]));
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void resetGammaOptionProperties(CallbackInfo ci) {
        Option.GAMMA.setMax(1);
        ((DoubleOptionAccessor) Option.GAMMA).setStep(0);
    }

    @Inject(method = "mouseReleased", at = @At("RETURN"))
    public void onRelease(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        VanillaOptions.applySettingsChanges();
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyReleased(keyCode, scanCode, modifiers);
        VanillaOptions.applySettingsChanges();
        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            VanillaOptions.applySettingsChanges();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
