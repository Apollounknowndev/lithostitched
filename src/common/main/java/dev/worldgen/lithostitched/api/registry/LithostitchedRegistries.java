package dev.worldgen.lithostitched.api.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.bandlands.band.Band;
import dev.worldgen.lithostitched.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.worldgen.densityfunction.fastnoise.config.FastNoiseConfig;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.processor.condition.ProcessorCondition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.msrandom.multiplatform.annotations.Expect;

/**
 * All of Lithostitched's static registries.
 */
public class LithostitchedRegistries {
	public static final Registry<MapCodec<? extends WorldgenModifier>> MODIFIER_TYPE = create(LithostitchedRegistryKeys.MODIFIER_TYPE);
	public static final Registry<MapCodec<? extends PlacementCondition>> PLACEMENT_CONDITION_TYPE = create(LithostitchedRegistryKeys.PLACEMENT_CONDITION_TYPE);
	public static final Registry<MapCodec<? extends ProcessorCondition>> PROCESSOR_CONDITION_TYPE = create(LithostitchedRegistryKeys.PROCESSOR_CONDITION_TYPE);
	public static final Registry<MapCodec<? extends Band>> BANDLANDS_BAND_TYPE = create(LithostitchedRegistryKeys.BANDLANDS_BAND_TYPE);
	public static final Registry<MapCodec<? extends BiomeInjector>> BIOME_INJECTOR_TYPE = create(LithostitchedRegistryKeys.BIOME_INJECTOR_TYPE);
	public static final Registry<MapCodec<? extends FastNoiseConfig>> FAST_NOISE_CONFIG_TYPE = create(LithostitchedRegistryKeys.FAST_NOISE_CONFIG_TYPE);
	
	@Expect
	public static <T> Registry<T> create(ResourceKey<Registry<T>> key);
	
	/**
	 * Purely for use in Lithostitched, don't call this.
	 */
	public static void init() {
	
	}
}
