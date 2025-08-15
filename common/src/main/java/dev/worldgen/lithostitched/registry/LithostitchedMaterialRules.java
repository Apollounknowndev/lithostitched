package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class LithostitchedMaterialRules {
    // Technical rules, used for merging modifiers into the main rule
    public static final ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> TRANSIENT_MERGED = LithostitchedCommon.createResourceKey(Registries.MATERIAL_RULE, "transient_merged");
    // Content rules, used for adding new features or simplifying existing constructions
    public static final ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> BILAYER_FILL = LithostitchedCommon.createResourceKey(Registries.MATERIAL_RULE, "bilayer_fill");
    public static final ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> NOISE_THRESHOLD_SELECTOR = LithostitchedCommon.createResourceKey(Registries.MATERIAL_RULE, "noise_threshold_selector");
    public static final ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> RANDOM_THRESHOLD_SELECTOR = LithostitchedCommon.createResourceKey(Registries.MATERIAL_RULE, "random_threshold_selector");
    public static final ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> HEIGHT_THRESHOLD_SELECTOR = LithostitchedCommon.createResourceKey(Registries.MATERIAL_RULE, "height_threshold_selector");
    public static final ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> STONE_DEPTH_THRESHOLD_SELECTOR = LithostitchedCommon.createResourceKey(Registries.MATERIAL_RULE, "stone_depth_threshold_selector");
    // Material conditions, which are under a separate registry but are used in the TestRule rule so we incl. here
    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> CLIFF = LithostitchedCommon.createResourceKey(Registries.MATERIAL_CONDITION, "cliff");
    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> FLAT = LithostitchedCommon.createResourceKey(Registries.MATERIAL_CONDITION, "flat");
    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> FLAT_LIQUID = LithostitchedCommon.createResourceKey(Registries.MATERIAL_CONDITION, "flat_liquid");
    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> LAND_TOP_LAYER = LithostitchedCommon.createResourceKey(Registries.MATERIAL_CONDITION, "land_top_layer");
    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> UNDERWATER = LithostitchedCommon.createResourceKey(Registries.MATERIAL_CONDITION, "underwater");
    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> CAVE_DEPTH = LithostitchedCommon.createResourceKey(Registries.MATERIAL_CONDITION, "cave_depth");
    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> EXTENDED_BIOME = LithostitchedCommon.createResourceKey(Registries.MATERIAL_CONDITION, "biome");
}
