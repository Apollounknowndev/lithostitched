package dev.worldgen.lithostitched.impl.worldgen.modifier.target.structure;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.stream.Stream;

public record FromBiomeTarget<T>(ResourceKey<Registry<T>> key, Holder<Biome> biome) implements HolderTarget<T> {
	public static final Type TYPE = new Type() {
		@Override
		public <A> DataResult<MapCodec<? extends HolderTarget<A>>> apply(ResourceKey<Registry<A>> key) {
			if (!key.equals(Registries.STRUCTURE)) {
				return DataResult.error(() -> "Cannot use \"structure/from_biome\" target type outside of structures");
			}
			return DataResult.success(RecordCodecBuilder.<FromBiomeTarget<A>>mapCodec(i -> i.group(
				RegistryCodecs.holder(Registries.BIOME).fieldOf("biome").forGetter(FromBiomeTarget::biome)
			).apply(i, biome -> new FromBiomeTarget<>(key, biome))));
		}
	};
	
	@Override
	public Stream<Holder<T>> streamHolders(RegistryAccess registries) {
		if (!this.key.equals(Registries.STRUCTURE)) throw new IllegalStateException("Cannot use \"structure/from_biome\" target type outside of structures");
		
		return registries.lookupOrThrow(Registries.STRUCTURE).listElements().filter(structure -> this.test(structure.value())).map(t -> (Holder<T>) t);
	}
	
	private boolean test(Structure structure) {
		return structure.biomes().contains(this.biome);
	}
	
	@Override
	public Type getType() {
		return TYPE;
	}
}
