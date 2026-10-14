package dev.worldgen.lithostitched.impl.worldgen.modifier.target.biome;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record HasFeatureTarget<T>(ResourceKey<Registry<T>> key, Holder<PlacedFeature> feature) implements HolderTarget<T> {
	public static final Type TYPE = new Type() {
		@Override
		public <A> DataResult<MapCodec<? extends HolderTarget<A>>> apply(ResourceKey<Registry<A>> key) {
			if (!key.equals(Registries.BIOME)) {
				return DataResult.error(() -> "Cannot use \"biome/has_feature\" target type outside of biomes");
			}
			return DataResult.success(RecordCodecBuilder.<HasFeatureTarget<A>>mapCodec(i -> i.group(
				RegistryFileCodec.create(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC, false).fieldOf("feature").forGetter(HasFeatureTarget::feature)
			).apply(i, feature -> new HasFeatureTarget<>(key, feature))));
		}
	};
	
	@Override
	public Iterable<Holder<T>> getHolders(RegistryAccess registries) {
		if (!this.key.equals(Registries.BIOME)) throw new IllegalStateException("Cannot use \"biome/has_feature\" target type outside of biomes");
		
		return registries.lookupOrThrow(Registries.BIOME).listElements().filter(biome -> this.test(biome.value())).map(t -> (Holder<T>) t).toList();
	}
	
	private boolean test(Biome biome) {
		return biome.getGenerationSettings().hasFeature(this.feature.value());
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
}
