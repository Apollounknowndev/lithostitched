package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.structure.condition.StructureCondition;
import dev.worldgen.lithostitched.worldgen.surface.LithostitchedSurfaceRules;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

import java.util.function.BiConsumer;

import static dev.worldgen.lithostitched.LithostitchedCommon.createResourceKey;
import static dev.worldgen.lithostitched.registry.LithostitchedMaterialRules.TRANSIENT_MERGED;

/**
 * Built-in registries for Lithostitched on Fabric.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LithostitchedBuiltInRegistries {
	public static final WritableRegistry<MapCodec<? extends Modifier>> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistries.MODIFIER_TYPE).buildAndRegister();
	public static final WritableRegistry<MapCodec<? extends StructureCondition>> STRUCTURE_CONDITION_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistries.STRUCTURE_CONDITION_TYPE).buildAndRegister();

	public static void init() {
		Registry.register(BuiltInRegistries.MATERIAL_RULE, TRANSIENT_MERGED, LithostitchedSurfaceRules.TransientMergedRuleSource.CODEC.codec());


		LithostitchedCommon.registerCommonModifiers((name, codec) -> {
			MODIFIER_TYPE.register(createResourceKey(LithostitchedRegistries.MODIFIER_TYPE, name), codec, RegistrationInfo.BUILT_IN);
		});
		registerFabricModifiers((name, codec) -> {
			MODIFIER_TYPE.register(createResourceKey(LithostitchedRegistries.MODIFIER_TYPE, name), codec, RegistrationInfo.BUILT_IN);
		});
		LithostitchedCommon.registerCommonStructureConditions((name, codec) -> {
			STRUCTURE_CONDITION_TYPE.register(createResourceKey(LithostitchedRegistries.STRUCTURE_CONDITION_TYPE, name), codec, RegistrationInfo.BUILT_IN);
		});


		LithostitchedCommon.registerCommonFeatureTypes((name, feature) -> {
			Registry.register(BuiltInRegistries.FEATURE, createResourceKey(Registries.FEATURE, name), feature);
		});
		LithostitchedCommon.registerCommonPoolElementTypes((name, codec) -> {
			Registry.register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, createResourceKey(Registries.STRUCTURE_POOL_ELEMENT, name), () -> (MapCodec<StructurePoolElement>)codec);
		});
		LithostitchedCommon.registerCommonPoolAliasBindings((name, codec) -> {
			Registry.register(BuiltInRegistries.POOL_ALIAS_BINDING_TYPE, createResourceKey(Registries.POOL_ALIAS_BINDING, name), codec);
		});
		LithostitchedCommon.registerCommonStructureTypes((name, codec) -> {
			Registry.register(BuiltInRegistries.STRUCTURE_TYPE, createResourceKey(Registries.STRUCTURE_TYPE, name), () -> (MapCodec<Structure>)codec);
		});
		LithostitchedCommon.registerCommonStructureProcessors((name, codec) -> {
			Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, createResourceKey(Registries.STRUCTURE_PROCESSOR, name), () -> (MapCodec<StructureProcessor>)codec);
		});
		LithostitchedCommon.registerCommonBlockEntityModifiers((name, codec) -> {
			Registry.register(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER, createResourceKey(Registries.RULE_BLOCK_ENTITY_MODIFIER, name), () -> (MapCodec<RuleBlockEntityModifier>)codec);
		});

		DynamicRegistries.register(LithostitchedRegistries.WORLDGEN_MODIFIER, Modifier.CODEC);
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
