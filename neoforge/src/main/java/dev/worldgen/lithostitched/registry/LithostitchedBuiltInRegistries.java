package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.resource.BreaksSeedParityCondition;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.structure.condition.StructureCondition;
import dev.worldgen.lithostitched.worldgen.surface.LithostitchedSurfaceRules;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;

import static dev.worldgen.lithostitched.LithostitchedCommon.MOD_ID;
import static dev.worldgen.lithostitched.LithostitchedCommon.createResourceKey;

/**
 * Built-in registries for Lithostitched on Neoforge.
 */
public final class LithostitchedBuiltInRegistries {
	private static final DeferredRegister<MapCodec<? extends Modifier>> DEFERRED_MODIFIER_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.MODIFIER_TYPE, LithostitchedCommon.MOD_ID);
	public static final Registry<MapCodec<? extends Modifier>> MODIFIER_TYPE = DEFERRED_MODIFIER_TYPES.makeRegistry(builder -> builder.sync(false));

	private static final DeferredRegister<MapCodec<? extends StructureCondition>> DEFERRED_STRUCTURE_CONDITION_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.STRUCTURE_CONDITION_TYPE, LithostitchedCommon.MOD_ID);
	public static final Registry<MapCodec<? extends StructureCondition>> STRUCTURE_CONDITION_TYPE = DEFERRED_STRUCTURE_CONDITION_TYPES.makeRegistry(builder -> builder.sync(false));

	private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "lithostitched");
	private static final DeferredRegister<MapCodec<? extends ICondition>> RESOURCE_CONDITION_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MOD_ID);


	public static void init(IEventBus bus) {
		bus.addListener((RegisterEvent event) -> {
			event.register(Registries.MATERIAL_RULE, helper -> helper.register(createResourceKey(Registries.MATERIAL_RULE, "transient_merged"), LithostitchedSurfaceRules.TransientMergedRuleSource.CODEC.codec()));

			LithostitchedCommon.registerCommonFeatureTypes((name, feature) -> register(event, Registries.FEATURE, name, feature));
			LithostitchedCommon.registerCommonPoolElementTypes((name, type) -> register(event, Registries.STRUCTURE_POOL_ELEMENT, name, type));
			LithostitchedCommon.registerCommonDensityFunctions((name, codec) -> register(event, Registries.DENSITY_FUNCTION_TYPE, name, codec));
			LithostitchedCommon.registerCommonPoolAliasBindings((name, codec) -> register(event, Registries.POOL_ALIAS_BINDING, name, codec));
			LithostitchedCommon.registerCommonStructureTypes((name, type) -> register(event, Registries.STRUCTURE_TYPE, name, type));
			LithostitchedCommon.registerCommonStructureProcessors((name, type) -> register(event, Registries.STRUCTURE_PROCESSOR, name, type));
			LithostitchedCommon.registerCommonRuleTests((name, type) -> register(event, Registries.RULE_TEST, name, type));
			LithostitchedCommon.registerCommonBlockEntityModifiers((name, type) -> register(event, Registries.RULE_BLOCK_ENTITY_MODIFIER, name, type));
		});

		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			event.dataPackRegistry(LithostitchedRegistryKeys.WORLDGEN_MODIFIER, Modifier.CODEC);
		});

		LithostitchedCommon.registerCommonModifiers((name, codec) -> DEFERRED_MODIFIER_TYPES.register(name, () -> codec));
		registerForgeModifiers((name, codec) -> DEFERRED_MODIFIER_TYPES.register(name, () -> codec));
		DEFERRED_MODIFIER_TYPES.register(bus);

		LithostitchedCommon.registerCommonStructureConditions((name, codec) -> DEFERRED_STRUCTURE_CONDITION_TYPES.register(name, () -> codec));
		DEFERRED_STRUCTURE_CONDITION_TYPES.register(bus);

		registerForgeBiomeModifiers((name, codec) -> BIOME_MODIFIER_TYPES.register(name, () -> codec));
		BIOME_MODIFIER_TYPES.register(bus);

		registerForgeResourceConditions((name, codec) -> RESOURCE_CONDITION_TYPES.register(name, () -> codec));
		RESOURCE_CONDITION_TYPES.register(bus);
	}

	private static <T> void register(RegisterEvent event, ResourceKey<Registry<T>> registry, String name, T object) {
		event.register(registry, helper -> helper.register(createResourceKey(registry, name), object));
	}

	public static void registerForgeModifiers(BiConsumer<String, MapCodec<? extends Modifier>> consumer) {
		consumer.accept("add_biome_spawns", AddBiomeSpawnsModifier.CODEC);
		consumer.accept("add_features", AddFeaturesModifier.CODEC);
		consumer.accept("remove_biome_spawns", RemoveBiomeSpawnsModifier.CODEC);
		consumer.accept("remove_features", RemoveFeaturesModifier.CODEC);
		consumer.accept("replace_climate", ReplaceClimateModifier.CODEC);
		consumer.accept("replace_effects", ReplaceEffectsModifier.CODEC);
	}

	private static void registerForgeResourceConditions(BiConsumer<String, MapCodec<? extends ICondition>> consumer) {
		consumer.accept("breaks_seed_parity", BreaksSeedParityCondition.CODEC);
	}

	public static void registerForgeBiomeModifiers(BiConsumer<String, MapCodec<? extends BiomeModifier>> consumer) {
		consumer.accept("replace_climate", LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier.CODEC);
		consumer.accept("replace_effects", LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier.CODEC);
	}
}
