package dev.worldgen.lithostitched.worldgen.biomeinjector;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.NoiseWiringHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class InjectorBiomeSource extends BiomeSource {
	public static final MapCodec<BiomeSource> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		BiomeSource.CODEC.fieldOf("delegate").forGetter(source -> source instanceof InjectorBiomeSource injector ? injector.delegate : source)
	).apply(i, InjectorBiomeSource::new));
	private final BiomeSource delegate;
	private List<BiomeInjector> injectors = new ArrayList<>();
	
	public InjectorBiomeSource(BiomeSource delegate) {
		this.delegate = delegate;
	}
	
	public BiomeSource delegate() {
		return this.delegate;
	}
	
	public void prepareInjectors(List<BiomeInjector> injectors, NoiseWiringHelper noiseHelper) {
		injectors.forEach(injector -> injector.mapAll(noiseHelper));
		this.injectors = injectors;
	}
	
	@Override
	protected MapCodec<? extends BiomeSource> codec() {
		return CODEC;
	}
	
	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		return Stream.concat(this.delegate.possibleBiomes().stream(), this.injectors.stream().map(BiomeInjector::biome));
	}
	
	@Override
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
		if (this.injectors == null) {
			return this.delegate.getNoiseBiome(quartX, quartY, quartZ, sampler);
		}
		
		int blockX = QuartPos.toBlock(quartX);
		int blockY = QuartPos.toBlock(quartY);
		int blockZ = QuartPos.toBlock(quartZ);
		DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(blockX, blockY, blockZ);
		Climate.TargetPoint point = sampler.sample(quartX, quartY, quartZ);
		
		for (BiomeInjector injector : this.injectors) {
			if (injector.matches(context, point)) {
				return injector.biome();
			}
		}
		
		if (this.delegate instanceof MultiNoiseBiomeSource multiNoise) {
			return multiNoise.getNoiseBiome(point);
		}
		return this.delegate.getNoiseBiome(quartX, quartY, quartZ, sampler);
	}
}
