package dev.worldgen.lithostitched.mixin.common.timeline;

import dev.worldgen.lithostitched.impl.duck.BiomeWeightingDuck;
import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SpatialAttributeInterpolator.class)
public class SpatialAttributeInterpolatorMixin implements BiomeWeightingDuck {
	@Unique
	private final Reference2DoubleArrayMap<Holder<Biome>> weightsByBiome = new Reference2DoubleArrayMap<>();
	
	@Override
	public void lithostitched$clear() {
		this.weightsByBiome.clear();
	}
	
	@Override
	public void lithostitched$accumulate(double weight, Holder<Biome> biome) {
		this.weightsByBiome.mergeDouble(biome, weight, Double::sum);
	}
	
	@Override
	public Reference2DoubleArrayMap<Holder<Biome>> lithostitched$getWeights() {
		return this.weightsByBiome;
	}
}
