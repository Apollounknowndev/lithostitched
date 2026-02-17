package dev.worldgen.lithostitched.worldgen.biomeinjector.internal;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.mixin.common.MultiNoiseBiomeSourceAccessor;
import dev.worldgen.lithostitched.mixin.common.mnbs.MNBSPLAccessor;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import dev.worldgen.lithostitched.worldgen.biomeinjector.AddPoints;
import dev.worldgen.lithostitched.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.worldgen.biomeinjector.ReplaceFully;
import dev.worldgen.lithostitched.worldgen.biomeinjector.ReplacePartially;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.SinglePointContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class InjectorBiomeSource extends BiomeSource {
	public static final MapCodec<BiomeSource> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		BiomeSource.CODEC.fieldOf("delegate").forGetter(source -> source instanceof InjectorBiomeSource injector ? injector.delegate : source)
	).apply(i, InjectorBiomeSource::new));
	private static final boolean APPLY_FULL_REPLACEMENTS_LATE =
		Lithostitched.isModLoaded("terrablender") ||
		Lithostitched.isModLoaded("biolith") ||
		Lithostitched.isModLoaded("blueprint");
	
	private final BiomeSource delegate;
	private final Map<MapCodec<? extends BiomeInjector>, List<BiomeInjector>> injectorMap = new HashMap<>();
	private List<Holder<Biome>> possibleBiomes = new ArrayList<>();
	
	public InjectorBiomeSource(BiomeSource delegate) {
		this.delegate = delegate;
	}
	
	public void applyInjectors(List<BiomeInjector> injectors, NoiseWiringHelper noiseHelper) {
		this.possibleBiomes = new ArrayList<>();
		
		injectors.forEach(injector -> {
			injector.mapAll(noiseHelper);
			
			List<BiomeInjector> byType = this.injectorMap.getOrDefault(injector.codec(), new ArrayList<>());
			byType.add(injector);
			this.injectorMap.put(injector.codec(), byType);
			
			this.possibleBiomes.addAll(injector.biomes());
		});
		
		if (!(this.delegate instanceof MultiNoiseBiomeSource multiNoise)) {
			Lithostitched.debug("Biome source is not a MultiNoiseBiomeSource instance. add_points and replace_fully injectors will not run.");
			return;
		}
		var accessor = (MultiNoiseBiomeSourceAccessor) multiNoise;
		var left = accessor.getParameters().left();
		var right = accessor.getParameters().right();
		
		var modifiedParameters = new ArrayList<>(left.orElseGet(() -> right.get().value().parameters()).values());
		AddPoints.apply(modifiedParameters, this.injectorMap.getOrDefault(AddPoints.CODEC, new ArrayList<>()));
		if (!APPLY_FULL_REPLACEMENTS_LATE) {
			ReplaceFully.apply(modifiedParameters, this.injectorMap.getOrDefault(ReplaceFully.CODEC, new ArrayList<>()));
		}
		if (left.isPresent()) {
			accessor.setParameters(Either.left(new Climate.ParameterList<>(modifiedParameters)));
		} else {
			((MNBSPLAccessor)right.get().value()).setParameters(new Climate.ParameterList<>(modifiedParameters));
		}
	}
	
	@Override
	protected MapCodec<? extends BiomeSource> codec() {
		return CODEC;
	}
	
	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		return Stream.concat(this.delegate.possibleBiomes().stream(), this.possibleBiomes.stream());
	}
	
	@Override
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
		if (this.injectorMap.isEmpty()) {
			return this.delegate.getNoiseBiome(quartX, quartY, quartZ, sampler);
		}
		
		int blockX = QuartPos.toBlock(quartX);
		int blockY = QuartPos.toBlock(quartY);
		int blockZ = QuartPos.toBlock(quartZ);
		SinglePointContext context = new SinglePointContext(blockX, blockY, blockZ);
		TargetPoint point = sampler.sample(quartX, quartY, quartZ);
		
		Holder<Biome> baseBiome = this.delegate instanceof MultiNoiseBiomeSource multiNoise ?
			multiNoise.getNoiseBiome(point) :
			this.delegate.getNoiseBiome(quartX, quartY, quartZ, sampler);
		
		return applyReplacements(context, point, baseBiome);
	}
	
	public Holder<Biome> applyReplacements(SinglePointContext context, TargetPoint point, Holder<Biome> biome) {
		HashMap<DensityFunction, Double> densities = new HashMap<>();
		if (APPLY_FULL_REPLACEMENTS_LATE) {
			for (BiomeInjector injector : this.injectorMap.getOrDefault(ReplaceFully.CODEC, List.of())) {
				ReplaceFully replaceFully = (ReplaceFully) injector;
				if (replaceFully.biomes().contains(biome)) {
					return replaceFully.replacement();
				}
			}
		}
		
		for (BiomeInjector injector : this.injectorMap.getOrDefault(ReplacePartially.CODEC, List.of())) {
			ReplacePartially replacePartially = (ReplacePartially) injector;
			if (replacePartially.matches(context, point, densities, biome)) {
				return replacePartially.replacement();
			}
		}
		return biome;
	}
}
