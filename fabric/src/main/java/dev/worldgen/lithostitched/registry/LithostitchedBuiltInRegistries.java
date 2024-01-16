package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.surface.LithostitchedSurfaceRules;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

import static dev.worldgen.lithostitched.LithostitchedCommon.LOGGER;
import static dev.worldgen.lithostitched.LithostitchedCommon.createResourceKey;

import java.util.function.BiConsumer;

/**
 * Built-in registries for Lithostitched on Fabric.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LithostitchedBuiltInRegistries {
	public static final WritableRegistry<Codec<? extends Modifier>> MODIFIER_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistries.MODIFIER_TYPE).buildAndRegister();
	public static final WritableRegistry<Codec<? extends ModifierPredicate>> MODIFIER_PREDICATE_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistries.MODIFIER_PREDICATE_TYPE).buildAndRegister();

	public static void init() {
		Registry.register(BuiltInRegistries.MATERIAL_RULE, LithostitchedMaterialRules.TRANSIENT_MERGED, LithostitchedSurfaceRules.TransientMergedRuleSource.CODEC.codec());


		LithostitchedCommon.registerCommonModifiers((name, codec) -> {
			MODIFIER_TYPE.register(createResourceKey(LithostitchedRegistries.MODIFIER_TYPE, name), codec, Lifecycle.stable());
		});
		registerFabricModifiers((name, codec) -> {
			MODIFIER_TYPE.register(createResourceKey(LithostitchedRegistries.MODIFIER_TYPE, name), codec, Lifecycle.stable());
		});
		LithostitchedCommon.registerCommonModifierPredicates((name, codec) -> {
			MODIFIER_PREDICATE_TYPE.register(createResourceKey(LithostitchedRegistries.MODIFIER_PREDICATE_TYPE, name), codec, Lifecycle.stable());
		});


		LithostitchedCommon.registerCommonStructureTypes((name, codec) -> {
			Registry.register(BuiltInRegistries.STRUCTURE_TYPE, createResourceKey(Registries.STRUCTURE_TYPE, name), () -> (Codec<Structure>)codec);
		});
		LithostitchedCommon.registerCommonStructureProcessors((name, codec) -> {
			Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, createResourceKey(Registries.STRUCTURE_PROCESSOR, name), () -> (Codec<StructureProcessor>)codec);
		});
		LithostitchedCommon.registerCommonBlockEntityModifiers((name, codec) -> {
			Registry.register(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER, createResourceKey(Registries.RULE_BLOCK_ENTITY_MODIFIER, name), () -> (Codec<RuleBlockEntityModifier>)codec);
		});

		DynamicRegistries.register(LithostitchedRegistries.WORLDGEN_MODIFIER, Modifier.CODEC);
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
