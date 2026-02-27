package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.resource.BreaksSeedParityCondition;
import dev.worldgen.lithostitched.worldgen.attribute.LithostitchedEnvironmentAttributes;
import dev.worldgen.lithostitched.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.worldgen.bandlands.band.Band;
import dev.worldgen.lithostitched.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.worldgen.densityfunction.fastnoise.config.FastNoiseConfig;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetBiomeAttributesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetDimensionAttributesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetTimelineTracksModifier;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateList;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.processor.condition.ProcessorCondition;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;

import static dev.worldgen.lithostitched.Lithostitched.MOD_ID;
import static dev.worldgen.lithostitched.Lithostitched.key;

/**
 * Built-in registries for Lithostitched on Neoforge.
 */
public final class LithostitchedBuiltInRegistries {
	public static final DeferredRegister<MapCodec<? extends WorldgenModifier>> DEFERRED_MODIFIER_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.MODIFIER_TYPE, MOD_ID);

	private static final DeferredRegister<MapCodec<? extends PlacementCondition>> DEFERRED_PLACEMENT_CONDITION_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.PLACEMENT_CONDITION_TYPE, MOD_ID);
	public static final Registry<MapCodec<? extends PlacementCondition>> PLACEMENT_CONDITION_TYPE = DEFERRED_PLACEMENT_CONDITION_TYPES.makeRegistry(builder -> builder.sync(false));

	private static final DeferredRegister<MapCodec<? extends ProcessorCondition>> DEFERRED_PROCESSOR_CONDITION_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.PROCESSOR_CONDITION_TYPE, MOD_ID);
	public static final Registry<MapCodec<? extends ProcessorCondition>> PROCESSOR_CONDITION_TYPE = DEFERRED_PROCESSOR_CONDITION_TYPES.makeRegistry(builder -> builder.sync(false));

	private static final DeferredRegister<MapCodec<? extends Band>> DEFERRED_BANDLANDS_BAND_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.BANDLANDS_BAND_TYPE, MOD_ID);
	public static final Registry<MapCodec<? extends Band>> BANDLANDS_BAND_TYPE = DEFERRED_BANDLANDS_BAND_TYPES.makeRegistry(builder -> builder.sync(false));
	
	private static final DeferredRegister<MapCodec<? extends BiomeInjector>> DEFERRED_BIOME_INJECTOR_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.BIOME_INJECTOR_TYPE, MOD_ID);
	public static final Registry<MapCodec<? extends BiomeInjector>> BANDLANDS_BIOME_INJECTOR_TYPE = DEFERRED_BIOME_INJECTOR_TYPES.makeRegistry(builder -> builder.sync(false));
	
	private static final DeferredRegister<MapCodec<? extends FastNoiseConfig>> DEFERRED_FAST_NOISE_CONFIG_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.FAST_NOISE_CONFIG_TYPE, MOD_ID);
	public static final Registry<MapCodec<? extends FastNoiseConfig>> BANDLANDS_FAST_NOISE_CONFIG_TYPE = DEFERRED_FAST_NOISE_CONFIG_TYPES.makeRegistry(builder -> builder.sync(false));

	private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MOD_ID);
	private static final DeferredRegister<MapCodec<? extends ICondition>> RESOURCE_CONDITION_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MOD_ID);

	public static void init(IEventBus bus) {
		LithostitchedRegistries.init();
		
		bus.addListener((RegisterEvent event) -> {
			Lithostitched.registerCommonBiomeSources((name, codec) -> register(event, Registries.BIOME_SOURCE, name, codec));
			Lithostitched.registerCommonBlockPredicateTypes((name, type) -> register(event, Registries.BLOCK_PREDICATE_TYPE, name, type));
			Lithostitched.registerCommonStateProviders((name, type) -> register(event, Registries.BLOCK_STATE_PROVIDER_TYPE, name, type));
			Lithostitched.registerCommonPlacementModifiers((name, type) -> register(event, Registries.PLACEMENT_MODIFIER_TYPE, name, type));
			Lithostitched.registerCommonFeatureTypes((name, feature) -> register(event, Registries.FEATURE, name, feature));
			Lithostitched.registerCommonPoolElementTypes((name, type) -> register(event, Registries.STRUCTURE_POOL_ELEMENT, name, type));
			Lithostitched.registerCommonDensityFunctions((name, codec) -> register(event, Registries.DENSITY_FUNCTION_TYPE, name, codec));
			Lithostitched.registerCommonPoolAliasBindings((name, codec) -> register(event, Registries.POOL_ALIAS_BINDING, name, codec));
			Lithostitched.registerCommonStructureTypes((name, type) -> register(event, Registries.STRUCTURE_TYPE, name, type));
			Lithostitched.registerCommonStructureProcessors((name, type) -> register(event, Registries.STRUCTURE_PROCESSOR, name, type));
			Lithostitched.registerCommonBlockEntityModifiers((name, type) -> register(event, Registries.RULE_BLOCK_ENTITY_MODIFIER, name, type));
			Lithostitched.registerCommonRuleSources((name, codec) -> register(event, Registries.MATERIAL_RULE, name, codec));
			Lithostitched.registerCommonSurfaceConditions((name, codec) -> register(event, Registries.MATERIAL_CONDITION, name, codec));
			
			LithostitchedEnvironmentAttributes.registerEnvironmentAttributes(
				(name, attribute) -> register(event, Registries.ENVIRONMENT_ATTRIBUTE, name, attribute)
			);
		});

		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			event.dataPackRegistry(LithostitchedRegistryKeys.WORLDGEN_MODIFIER, WorldgenModifier.CODEC);
			event.dataPackRegistry(LithostitchedRegistryKeys.SURFACE_RULE, SurfaceRules.RuleSource.CODEC);
			event.dataPackRegistry(LithostitchedRegistryKeys.BANDLANDS, Bandlands.CODEC);
			event.dataPackRegistry(LithostitchedRegistryKeys.TEMPLATE_LIST, TemplateList.CODEC);
			event.dataPackRegistry(LithostitchedRegistryKeys.BIOME_INJECTOR, BiomeInjector.CODEC);
			event.dataPackRegistry(LithostitchedRegistryKeys.FAST_NOISE_CONFIG, FastNoiseConfig.CODEC);
			event.dataPackRegistry(LithostitchedRegistryKeys.REGION, Region.CODEC);
		});

		Lithostitched.registerCommonModifiers((name, codec) -> DEFERRED_MODIFIER_TYPES.register(name, () -> codec));
		registerForgeModifiers((name, codec) -> DEFERRED_MODIFIER_TYPES.register(name, () -> codec));
		DEFERRED_MODIFIER_TYPES.register(bus);

		Lithostitched.registerCommonPlacementConditions((name, codec) -> DEFERRED_PLACEMENT_CONDITION_TYPES.register(name, () -> codec));
		DEFERRED_PLACEMENT_CONDITION_TYPES.register(bus);

		Lithostitched.registerCommonProcessorConditions((name, codec) -> DEFERRED_PROCESSOR_CONDITION_TYPES.register(name, () -> codec));
		DEFERRED_PROCESSOR_CONDITION_TYPES.register(bus);

		Lithostitched.registerCommonBandlandsBandTypes((name, codec) -> DEFERRED_BANDLANDS_BAND_TYPES.register(name, () -> codec));
		DEFERRED_BANDLANDS_BAND_TYPES.register(bus);
		
		Lithostitched.registerCommonBiomeInjectorTypes((name, codec) -> DEFERRED_BIOME_INJECTOR_TYPES.register(name, () -> codec));
		DEFERRED_BIOME_INJECTOR_TYPES.register(bus);
		
		Lithostitched.registerCommonFastNoiseConfigTypes((name, codec) -> DEFERRED_FAST_NOISE_CONFIG_TYPES.register(name, () -> codec));
		DEFERRED_FAST_NOISE_CONFIG_TYPES.register(bus);

		registerForgeBiomeModifiers((name, codec) -> BIOME_MODIFIER_TYPES.register(name, () -> codec));
		BIOME_MODIFIER_TYPES.register(bus);

		registerForgeResourceConditions((name, codec) -> RESOURCE_CONDITION_TYPES.register(name, () -> codec));
		RESOURCE_CONDITION_TYPES.register(bus);
	}

	private static <T> void register(RegisterEvent event, ResourceKey<Registry<T>> registry, String name, T object) {
		event.register(registry, helper -> helper.register(key(registry, name), object));
	}

	public static void registerForgeModifiers(BiConsumer<String, MapCodec<? extends WorldgenModifier>> consumer) {
		consumer.accept("add_biome_spawns", AddBiomeSpawnsModifier.CODEC);
		consumer.accept("add_features", AddFeaturesModifier.CODEC);
		consumer.accept("remove_biome_spawns", RemoveBiomeSpawnsModifier.CODEC);
		consumer.accept("remove_features", RemoveFeaturesModifier.CODEC);
		consumer.accept("replace_climate", ReplaceClimateModifier.CODEC);
		consumer.accept("replace_effects", ReplaceEffectsModifier.CODEC);

		consumer.accept("set_biome_attributes", SetBiomeAttributesModifier.CODEC);
		consumer.accept("set_dimension_attributes", SetDimensionAttributesModifier.CODEC);
		consumer.accept("set_timeline_tracks", SetTimelineTracksModifier.CODEC);
	}

	private static void registerForgeResourceConditions(BiConsumer<String, MapCodec<? extends ICondition>> consumer) {
		consumer.accept("breaks_seed_parity", BreaksSeedParityCondition.CODEC);
	}

	public static void registerForgeBiomeModifiers(BiConsumer<String, MapCodec<? extends BiomeModifier>> consumer) {
		consumer.accept("replace_climate", LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier.CODEC);
		consumer.accept("replace_effects", LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier.CODEC);
	}
}
