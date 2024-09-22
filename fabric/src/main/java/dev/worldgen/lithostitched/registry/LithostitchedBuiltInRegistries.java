package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.structure.condition.StructureCondition;
import dev.worldgen.lithostitched.worldgen.surface.LithostitchedSurfaceRules;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

import static dev.worldgen.lithostitched.LithostitchedCommon.createResourceKey;

import java.util.function.BiConsumer;

/**
 * Built-in registries for Lithostitched on Fabric.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LithostitchedBuiltInRegistries {
	public static final WritableRegistry<Codec<? extends Modifier>> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.MODIFIER_TYPE).buildAndRegister();
	public static final WritableRegistry<Codec<? extends ModifierPredicate>> MODIFIER_PREDICATE_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.MODIFIER_PREDICATE_TYPE).buildAndRegister();
	public static final WritableRegistry<MapCodec<? extends StructureCondition>> STRUCTURE_CONDITION_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.STRUCTURE_CONDITION_TYPE).buildAndRegister();

	public static void init() {
		DynamicRegistries.register(LithostitchedRegistryKeys.WORLDGEN_MODIFIER, Modifier.CODEC);

		LithostitchedCommon.registerCommonModifiers((name, codec) -> register(MODIFIER_TYPE, name, codec));
		registerFabricModifiers((name, codec) -> register(MODIFIER_TYPE, name, codec));
		LithostitchedCommon.registerCommonModifierPredicates((name, codec) -> register(MODIFIER_PREDICATE_TYPE, name, codec));
		LithostitchedCommon.registerCommonStructureConditions((name, codec) -> register(STRUCTURE_CONDITION_TYPE, name, codec));

		LithostitchedCommon.registerCommonFeatureTypes((name, feature) -> register(BuiltInRegistries.FEATURE, name, feature));
		LithostitchedCommon.registerCommonBlockEntityModifiers((name, type) -> register(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER, name, type));
		LithostitchedCommon.registerCommonDensityFunctions((name, codec) -> register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, name, codec));
		LithostitchedCommon.registerCommonPoolElementTypes((name, type) -> register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, name, type));
		LithostitchedCommon.registerCommonStructureProcessors((name, type) -> register(BuiltInRegistries.STRUCTURE_PROCESSOR, name, type));
		LithostitchedCommon.registerCommonStructureTypes((name, type) -> register(BuiltInRegistries.STRUCTURE_TYPE, name, type));
		LithostitchedCommon.registerCommonRuleTests((name, type) -> register(BuiltInRegistries.RULE_TEST, name, type));

		Registry.register(BuiltInRegistries.MATERIAL_RULE, LithostitchedMaterialRules.TRANSIENT_MERGED, LithostitchedSurfaceRules.TransientMergedRuleSource.CODEC.codec());
	}

	private static <T> void register(Registry<T> registry, String name, T object) {
		Registry.register(registry, createResourceKey(registry.key(), name), object);
	}

	public static void registerFabricModifiers(BiConsumer<String, Codec<? extends Modifier>> consumer) {
		consumer.accept("add_biome_spawns", AddBiomeSpawnsModifier.CODEC);
		consumer.accept("add_features", AddFeaturesModifier.CODEC);
		consumer.accept("remove_features", RemoveFeaturesModifier.CODEC);
		consumer.accept("remove_biome_spawns", RemoveBiomeSpawnsModifier.CODEC);
		consumer.accept("replace_climate", ReplaceClimateModifier.CODEC);
		consumer.accept("replace_effects", ReplaceEffectsModifier.CODEC);
	}
}
