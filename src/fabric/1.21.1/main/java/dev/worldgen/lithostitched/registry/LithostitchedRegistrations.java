package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.resource.BreaksSeedParityCondition;
import dev.worldgen.lithostitched.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.worldgen.densityfunction.fastnoise.config.FastNoiseConfig;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateList;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.function.BiConsumer;

import static dev.worldgen.lithostitched.api.registry.LithostitchedRegistries.MODIFIER_TYPE;
import static dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries.*;

public class LithostitchedRegistrations {
    public static void init() {
        DynamicRegistries.register(LithostitchedRegistryKeys.WORLDGEN_MODIFIER, WorldgenModifier.CODEC);
        DynamicRegistries.register(LithostitchedRegistryKeys.SURFACE_RULE, SurfaceRules.RuleSource.CODEC);
        DynamicRegistries.register(LithostitchedRegistryKeys.BANDLANDS, Bandlands.CODEC);
        DynamicRegistries.register(LithostitchedRegistryKeys.TEMPLATE_LIST, TemplateList.CODEC);
        DynamicRegistries.register(LithostitchedRegistryKeys.BIOME_INJECTOR, BiomeInjector.CODEC);
        DynamicRegistries.register(LithostitchedRegistryKeys.FAST_NOISE_CONFIG, FastNoiseConfig.CODEC);
        DynamicRegistries.register(LithostitchedRegistryKeys.REGION, Region.CODEC);

        Lithostitched.registerCommonModifiers((name, codec) -> register(MODIFIER_TYPE, name, codec));
        registerFabricModifiers((name, codec) -> register(MODIFIER_TYPE, name, codec));
        Lithostitched.registerCommonPlacementConditions((name, codec) -> register(PLACEMENT_CONDITION_TYPE, name, codec));
        Lithostitched.registerCommonProcessorConditions((name, codec) -> register(PROCESSOR_CONDITION_TYPE, name, codec));
        Lithostitched.registerCommonBandlandsBandTypes((name, codec) -> register(BANDLANDS_BAND_TYPE, name, codec));
        Lithostitched.registerCommonBiomeInjectorTypes((name, codec) -> register(BIOME_INJECTOR_TYPE, name, codec));
        Lithostitched.registerCommonFastNoiseConfigTypes((name, codec) -> register(FAST_NOISE_CONFIG_TYPE, name, codec));

        Lithostitched.registerCommonBiomeSources((name, codec) -> register(BuiltInRegistries.BIOME_SOURCE, name, codec));
        Lithostitched.registerCommonBlockPredicateTypes((name, type) -> register(BuiltInRegistries.BLOCK_PREDICATE_TYPE, name, type));
        Lithostitched.registerCommonStateProviders((name, type) -> register(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE, name, type));
        Lithostitched.registerCommonPlacementModifiers((name, type) -> register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, name, type));
        Lithostitched.registerCommonFeatureTypes((name, feature) -> register(BuiltInRegistries.FEATURE, name, feature));
        Lithostitched.registerCommonPoolElementTypes((name, type) -> register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, name, type));
        Lithostitched.registerCommonDensityFunctions((name, codec) -> register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, name, codec));
        Lithostitched.registerCommonPoolAliasBindings((name, codec) -> register(BuiltInRegistries.POOL_ALIAS_BINDING_TYPE, name, codec));
        Lithostitched.registerCommonStructureTypes((name, type) -> register(BuiltInRegistries.STRUCTURE_TYPE, name, type));
        Lithostitched.registerCommonStructureProcessors((name, type) -> register(BuiltInRegistries.STRUCTURE_PROCESSOR, name, type));
        Lithostitched.registerCommonBlockEntityModifiers((name, type) -> register(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER, name, type));
        Lithostitched.registerCommonRuleSources((name, codec) -> register(BuiltInRegistries.MATERIAL_RULE, name, codec));
        Lithostitched.registerCommonSurfaceConditions((name, codec) -> register(BuiltInRegistries.MATERIAL_CONDITION, name, codec));

        ResourceConditions.register(BreaksSeedParityCondition.TYPE);
    }

    public static void registerFabricModifiers(BiConsumer<String, MapCodec<? extends WorldgenModifier>> consumer) {
        consumer.accept("add_biome_spawns", AddBiomeSpawnsModifier.CODEC);
        consumer.accept("add_features", AddFeaturesModifier.CODEC);
        consumer.accept("remove_biome_spawns", RemoveBiomeSpawnsModifier.CODEC);
        consumer.accept("remove_features", RemoveFeaturesModifier.CODEC);
        consumer.accept("replace_climate", ReplaceClimateModifier.CODEC);
        consumer.accept("replace_effects", ReplaceEffectsModifier.CODEC);
    }

    public static <T> void register(Registry<T> registry, String name, T object) {
        Registry.register(registry, Lithostitched.key(registry.key(), name), object);
    }
}
