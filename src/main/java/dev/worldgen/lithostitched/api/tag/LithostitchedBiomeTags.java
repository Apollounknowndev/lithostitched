package dev.worldgen.lithostitched.api.tag;

import dev.worldgen.lithostitched.Lithostitched;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

@SuppressWarnings("unused")
public interface LithostitchedBiomeTags {
	TagKey<Biome> HAS_VILLAGER_TYPE_DESERT = create("has_villager_type/desert");
	TagKey<Biome> HAS_VILLAGER_TYPE_JUNGLE = create("has_villager_type/jungle");
	TagKey<Biome> HAS_VILLAGER_TYPE_PLAINS = create("has_villager_type/plains");
	TagKey<Biome> HAS_VILLAGER_TYPE_SAVANNA = create("has_villager_type/savanna");
	TagKey<Biome> HAS_VILLAGER_TYPE_SNOWY = create("has_villager_type/snowy");
	TagKey<Biome> HAS_VILLAGER_TYPE_SWAMP = create("has_villager_type/swamp");
	TagKey<Biome> HAS_VILLAGER_TYPE_TAIGA = create("has_villager_type/taiga");
	
	private static TagKey<Biome> create(String name) {
		return TagKey.create(Registries.BIOME, Lithostitched.id(name));
	}
	
	static TagKey<Biome> createVillagerTypeTag(ResourceKey<?> holder) {
		return TagKey.create(Registries.BIOME, Lithostitched.vanillaToLithostitched(holder.location().withPrefix("has_villager_type/")));
	}
}
