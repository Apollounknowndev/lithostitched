package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.resource.BreaksSeedParityCondition;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.processor.condition.ProcessorCondition;
import dev.worldgen.lithostitched.worldgen.structure.condition.StructureCondition;
import dev.worldgen.lithostitched.worldgen.surface.LithostitchedSurfaceRules;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.BiConsumer;

import static dev.worldgen.lithostitched.LithostitchedCommon.createResourceKey;
import static dev.worldgen.lithostitched.registry.LithostitchedMaterialRules.TRANSIENT_MERGED;

/**
 * Built-in registries for Lithostitched on Fabric.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LithostitchedBuiltInRegistries {
	public static final WritableRegistry<MapCodec<? extends Modifier>> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.MODIFIER_TYPE).buildAndRegister();
	public static final WritableRegistry<MapCodec<? extends StructureCondition>> STRUCTURE_CONDITION_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.STRUCTURE_CONDITION_TYPE).buildAndRegister();
	public static final WritableRegistry<MapCodec<? extends ProcessorCondition>> PROCESSOR_CONDITION_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.PROCESSOR_CONDITION_TYPE).buildAndRegister();

	public static void init() {
		DynamicRegistries.register(LithostitchedRegistryKeys.WORLDGEN_MODIFIER, Modifier.CODEC);

		LithostitchedCommon.registerCommonModifiers((name, codec) -> register(MODIFIER_TYPE, name, codec));
		registerFabricModifiers((name, codec) -> register(MODIFIER_TYPE, name, codec));
		LithostitchedCommon.registerCommonStructureConditions((name, codec) -> register(STRUCTURE_CONDITION_TYPE, name, codec));
		LithostitchedCommon.registerCommonProcessorConditions((name, codec) -> register(PROCESSOR_CONDITION_TYPE, name, codec));

		LithostitchedCommon.registerCommonBlockPredicateTypes((name, type) -> register(BuiltInRegistries.BLOCK_PREDICATE_TYPE, name, type));
		LithostitchedCommon.registerCommonStateProviders((name, type) -> register(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE, name, type));
		LithostitchedCommon.registerCommonFeatureTypes((name, feature) -> register(BuiltInRegistries.FEATURE, name, feature));
		LithostitchedCommon.registerCommonPoolElementTypes((name, type) -> register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, name, type));
		LithostitchedCommon.registerCommonDensityFunctions((name, codec) -> register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, name, codec));
		LithostitchedCommon.registerCommonPoolAliasBindings((name, codec) -> register(BuiltInRegistries.POOL_ALIAS_BINDING_TYPE, name, codec));
		LithostitchedCommon.registerCommonStructureTypes((name, type) -> register(BuiltInRegistries.STRUCTURE_TYPE, name, type));
		LithostitchedCommon.registerCommonStructureProcessors((name, type) -> register(BuiltInRegistries.STRUCTURE_PROCESSOR, name, type));
		LithostitchedCommon.registerCommonBlockEntityModifiers((name, type) -> register(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER, name, type));

		Registry.register(BuiltInRegistries.MATERIAL_RULE, TRANSIENT_MERGED, LithostitchedSurfaceRules.TransientMergedRuleSource.CODEC.codec());

		ResourceConditions.register(BreaksSeedParityCondition.TYPE);
	}

	private static <T> void register(Registry<T> registry, String name, T object) {
		Registry.register(registry, createResourceKey(registry.key(), name), object);
	}

	public static void registerFabricModifiers(BiConsumer<String, MapCodec<? extends Modifier>> consumer) {
		consumer.accept("add_biome_spawns", AddBiomeSpawnsModifier.CODEC);
		consumer.accept("add_features", AddFeaturesModifier.CODEC);
		consumer.accept("remove_features", RemoveFeaturesModifier.CODEC);
		consumer.accept("remove_biome_spawns", RemoveBiomeSpawnsModifier.CODEC);
		consumer.accept("replace_climate", ReplaceClimateModifier.CODEC);
		consumer.accept("replace_effects", ReplaceEffectsModifier.CODEC);
	}
}
