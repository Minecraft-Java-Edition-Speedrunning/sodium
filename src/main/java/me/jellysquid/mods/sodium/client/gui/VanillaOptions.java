package me.jellysquid.mods.sodium.client.gui;

import me.jellysquid.mods.sodium.client.gui.vanilla.builders.CycleOptionBuilder;
import me.jellysquid.mods.sodium.client.gui.vanilla.options.EntityCulling;
import net.minecraft.client.options.Option;

import java.util.HashSet;
import java.util.Set;

public class VanillaOptions {
    // For reduce log while changing render distance or another double options
    private static final Set<Runnable> DOUBLE_OPTIONS_RUNNABLE = new HashSet<>();

    public static void clearSettingsChanges(){
        DOUBLE_OPTIONS_RUNNABLE.clear();
    }

    public static void applySettingsChanges(){
        DOUBLE_OPTIONS_RUNNABLE.forEach(Runnable::run);
        clearSettingsChanges();
    }

    public static void addSettingsChange(Runnable apply){
        DOUBLE_OPTIONS_RUNNABLE.add(apply);
    }

    public static final Option ENTITY_CULLING = new CycleOptionBuilder<EntityCulling>()
            .setKey("options.entityCulling")
            .setText("Entity Culling")
            .setOptions(EntityCulling.values())
            .setGetter((options) -> EntityCulling.getOption(options.advanced.useAdvancedEntityCulling))
            .setSetter((options, value) -> options.advanced.useAdvancedEntityCulling = value.isEnabled())
            .setTextGetter(EntityCulling::getText)
            .build();
}
