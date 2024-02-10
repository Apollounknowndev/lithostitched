package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.surface.LithostitchedSurfaceRules;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.*;

import java.util.function.BiConsumer;

import static dev.worldgen.lithostitched.LithostitchedCommon.createResourceKey;

/**
 * Built-in registries for Lithostitched on Neoforge.
 */
public final class LithostitchedBuiltInRegistries {
	private static final DeferredRegister<Codec<? extends Modifier>> DEFERRED_MODIFIER_TYPES = DeferredRegister.create(LithostitchedRegistries.MODIFIER_TYPE, LithostitchedCommon.MOD_ID);
	public static final Registry<Codec<? extends Modifier>> MODIFIER_TYPE = DEFERRED_MODIFIER_TYPES.makeRegistry(builder -> builder.sync(false));

	private static final DeferredRegister<Codec<? extends ModifierPredicate>> DEFERRED_MODIFIER_PREDICATES_TYPES = DeferredRegister.create(LithostitchedRegistries.MODIFIER_PREDICATE_TYPE, LithostitchedCommon.MOD_ID);
	public static final Registry<Codec<? extends ModifierPredicate>> MODIFIER_PREDICATE_TYPE = DEFERRED_MODIFIER_PREDICATES_TYPES.makeRegistry(builder -> builder.sync(false));

	private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "lithostitched");
	public static void init(IEventBus bus) {

		bus.addListener((RegisterEvent event) -> {
			event.register(Registries.MATERIAL_RULE, helper -> helper.register(createResourceKey(Registries.MATERIAL_RULE, "transient_merged"), LithostitchedSurfaceRules.TransientMergedRuleSource.CODEC.codec()));

			LithostitchedCommon.registerCommonFeatureTypes((name, feature) -> event.register(Registries.FEATURE, helper -> helper.register(name, feature)));
			LithostitchedCommon.registerCommonPoolElementTypes((name, codec) -> event.register(Registries.STRUCTURE_POOL_ELEMENT, helper -> helper.register(createResourceKey(Registries.STRUCTURE_POOL_ELEMENT, name), () -> (Codec<StructurePoolElement>)codec)));
			LithostitchedCommon.registerCommonStructureTypes((name, codec) -> event.register(Registries.STRUCTURE_TYPE, helper -> helper.register(createResourceKey(Registries.STRUCTURE_TYPE, name), () -> (Codec<Structure>)codec)));
			LithostitchedCommon.registerCommonStructureProcessors((name, codec) -> event.register(Registries.STRUCTURE_PROCESSOR, helper -> helper.register(createResourceKey(Registries.STRUCTURE_PROCESSOR, name), () -> (Codec<StructureProcessor>)codec)));
			LithostitchedCommon.registerCommonBlockEntityModifiers((name, codec) -> event.register(Registries.RULE_BLOCK_ENTITY_MODIFIER, helper -> helper.register(createResourceKey(Registries.RULE_BLOCK_ENTITY_MODIFIER, name), () -> (Codec<RuleBlockEntityModifier>)codec)));
		});

		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			event.dataPackRegistry(LithostitchedRegistries.WORLDGEN_MODIFIER, Modifier.CODEC);
		});

		registerForgeBiomeModifiers((name, codec) -> BIOME_MODIFIER_SERIALIZERS.register(name, () -> codec));
		BIOME_MODIFIER_SERIALIZERS.register(bus);

		LithostitchedCommon.registerCommonModifiers((name, codec) -> DEFERRED_MODIFIER_TYPES.register(name, () -> codec));
		registerForgeModifiers((name, codec) -> DEFERRED_MODIFIER_TYPES.register(name, () -> codec));
		DEFERRED_MODIFIER_TYPES.register(bus);

		LithostitchedCommon.registerCommonModifierPredicates((name, codec) -> DEFERRED_MODIFIER_PREDICATES_TYPES.register(name, () -> codec));
		DEFERRED_MODIFIER_PREDICATES_TYPES.register(bus);

	}

	public static void registerForgeModifiers(BiConsumer<String, Codec<? extends Modifier>> consumer) {
		consumer.accept("add_biome_spawns", AddBiomeSpawnsModifier.CODEC);
		consumer.accept("add_features", AddFeaturesModifier.CODEC);
		consumer.accept("remove_biome_spawns", RemoveBiomeSpawnsModifier.CODEC);
		consumer.accept("remove_features", RemoveFeaturesModifier.CODEC);
		consumer.accept("replace_climate", ReplaceClimateModifier.CODEC);
		consumer.accept("replace_effects", ReplaceEffectsModifier.CODEC);
	}

	public static void registerForgeBiomeModifiers(BiConsumer<String, Codec<? extends BiomeModifier>> consumer) {
		consumer.accept("replace_climate", LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier.CODEC);
		consumer.accept("replace_effects", LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier.CODEC);
	}
}
