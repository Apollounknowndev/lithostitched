package dev.worldgen.lithostitched.worldgen.biomeinjector.internal;

import com.google.common.base.Suppliers;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.mixin.common.ChunkGeneratorAccessor;
import dev.worldgen.lithostitched.mixin.common.RandomStateAccessor;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import dev.worldgen.lithostitched.worldgen.biomeinjector.BiomeInjector;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.List;
import java.util.Map;

public class BiomeInjectorManager {
	public static void applyBiomeInjectors(MinecraftServer server) {
		RegistryAccess registries = server.registryAccess();
		Registry<BiomeInjector> injectorRegistry = Lithostitched.registry(registries, LithostitchedRegistryKeys.BIOME_INJECTOR);
		if (injectorRegistry.entrySet().isEmpty()) return;
		
		Registry<LevelStem> dimensions = Lithostitched.registry(registries, Registries.LEVEL_STEM);
		long seed = server.getWorldData().worldGenOptions().seed();
		for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensions.entrySet()) {
			var injectors = injectorRegistry.entrySet().stream().map(Map.Entry::getValue).filter(injector -> entry.getKey().equals(injector.dimension())).toList();
			Lithostitched.debug("Applying {} biome injections for dimension {}", injectors.size(), entry.getKey().identifier());
			if (injectors.isEmpty()) continue;
			
			ChunkGenerator generator = entry.getValue().generator();
			if (!(generator instanceof NoiseBasedChunkGenerator noiseGenerator)) continue;
			
			RandomState randomState = RandomState.create(noiseGenerator.generatorSettings().value(), registries.lookupOrThrow(Registries.NOISE), seed);
			NoiseWiringHelper noiseHelper = new NoiseWiringHelper(
				seed,
				noiseGenerator.generatorSettings().value().useLegacyRandomSource(),
				randomState,
				((RandomStateAccessor)(Object)randomState).getRandom()
			);
			
			ChunkGeneratorAccessor accessor = (ChunkGeneratorAccessor) generator;
			InjectorBiomeSource injectorSource = new InjectorBiomeSource(accessor.getBiomeSource());
			injectorSource.applyInjectors(injectors, noiseHelper);
			accessor.setBiomeSource(injectorSource);
			accessor.setFeaturesPerStep(Suppliers.memoize(() ->
				FeatureSorter.buildFeaturesPerStep(List.copyOf(injectorSource.possibleBiomes()), biome -> accessor.getGetter().apply(biome).features(), true)
			));
		}
	}
}
