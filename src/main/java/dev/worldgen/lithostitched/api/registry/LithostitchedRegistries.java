package dev.worldgen.lithostitched.api.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import dev.worldgen.lithostitched.api.worldgen.bandlands.Band;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateList;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.processorcondition.ProcessorCondition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;

public interface LithostitchedRegistries {
	// Dynamic
	ResourceKey<Registry<WorldgenModifier>> WORLDGEN_MODIFIER = create("worldgen_modifier");
	ResourceKey<Registry<SurfaceRules.RuleSource>> SURFACE_RULE = create("surface_rule");
	ResourceKey<Registry<Bandlands>> BANDLANDS = create("bandlands");
	ResourceKey<Registry<TemplateList>> TEMPLATE_LIST = create("template_list");
	ResourceKey<Registry<BiomeInjector>> BIOME_INJECTOR = create("biome_injector");
	ResourceKey<Registry<FastNoiseConfig>> FAST_NOISE_CONFIG = create("fast_noise_config");
	ResourceKey<Registry<Region>> REGION = create("region");
	
	// Static
	ResourceKey<Registry<MapCodec<? extends WorldgenModifier>>> MODIFIER_TYPE = create("modifier_type");
	ResourceKey<Registry<MapCodec<? extends PlacementCondition>>> PLACEMENT_CONDITION_TYPE = create("placement_condition_type");
	ResourceKey<Registry<MapCodec<? extends ProcessorCondition>>> PROCESSOR_CONDITION_TYPE = create("processor_condition_type");
	ResourceKey<Registry<MapCodec<? extends Band>>> BANDLANDS_BAND_TYPE = create("bandlands_band_type");
	ResourceKey<Registry<MapCodec<? extends BiomeInjector>>> BIOME_INJECTOR_TYPE = create("biome_injector_type");
	ResourceKey<Registry<MapCodec<? extends FastNoiseConfig>>> FAST_NOISE_CONFIG_TYPE = create("fast_noise_config_type");
	ResourceKey<Registry<MapCodec<? extends LoadPredicate>>> LOAD_PREDICATE_TYPE = create("load_predicate_type");
	
	private static <T> ResourceKey<Registry<T>> create(String name) {
		return ResourceKey.createRegistryKey(Lithostitched.id(name));
	}
}
