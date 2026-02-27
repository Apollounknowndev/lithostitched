package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.bandlands.band.Band;
import dev.worldgen.lithostitched.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.worldgen.densityfunction.fastnoise.config.FastNoiseConfig;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.processor.condition.ProcessorCondition;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.WritableRegistry;

/**
 * Built-in registries for Lithostitched on Fabric.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface LithostitchedBuiltInRegistries {
	WritableRegistry<MapCodec<? extends PlacementCondition>> PLACEMENT_CONDITION_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.PLACEMENT_CONDITION_TYPE).buildAndRegister();
	WritableRegistry<MapCodec<? extends ProcessorCondition>> PROCESSOR_CONDITION_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.PROCESSOR_CONDITION_TYPE).buildAndRegister();
	WritableRegistry<MapCodec<? extends Band>> BANDLANDS_BAND_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.BANDLANDS_BAND_TYPE).buildAndRegister();
	WritableRegistry<MapCodec<? extends BiomeInjector>> BIOME_INJECTOR_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.BIOME_INJECTOR_TYPE).buildAndRegister();
	WritableRegistry<MapCodec<? extends FastNoiseConfig>> FAST_NOISE_CONFIG_TYPE = FabricRegistryBuilder.createSimple(LithostitchedRegistryKeys.FAST_NOISE_CONFIG_TYPE).buildAndRegister();

	static void init() {

	}
}
