package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.resource.BreaksSeedParityCondition;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.surface.LithostitchedSurfaceRules;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.BiConsumer;

import static dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries.*;
import static dev.worldgen.lithostitched.registry.LithostitchedMaterialRules.TRANSIENT_MERGED;

public class LithostitchedRegistrations {
    public static void init() {
        DynamicRegistries.register(LithostitchedRegistryKeys.WORLDGEN_MODIFIER, Modifier.CODEC);

        Lithostitched.registerCommonModifiers((name, codec) -> register(MODIFIER_TYPE, name, codec));
        registerFabricModifiers((name, codec) -> register(MODIFIER_TYPE, name, codec));
        Lithostitched.registerCommonPlacementConditions((name, codec) -> register(PLACEMENT_CONDITION_TYPE, name, codec));
        Lithostitched.registerCommonProcessorConditions((name, codec) -> register(PROCESSOR_CONDITION_TYPE, name, codec));

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

        Registry.register(BuiltInRegistries.MATERIAL_RULE, TRANSIENT_MERGED, LithostitchedSurfaceRules.TransientMergedRuleSource.CODEC.codec());

        ResourceConditions.register(BreaksSeedParityCondition.TYPE);
    }

    public static void registerFabricModifiers(BiConsumer<String, MapCodec<? extends Modifier>> consumer) {
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
