package dev.worldgen.lithostitched.api.tag;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.impl.Lithostitched;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.BiomeSource;

@SuppressWarnings("unused")
public interface LithostitchedBiomeSourceTags {
	TagKey<MapCodec<? extends BiomeSource>> CANNOT_INJECT_INTO = create("cannot_inject_into");
	
	private static TagKey<MapCodec<? extends BiomeSource>> create(String name) {
		return TagKey.create(Registries.BIOME_SOURCE, Lithostitched.id(name));
	}
}
