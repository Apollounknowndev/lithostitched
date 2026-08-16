package dev.worldgen.lithostitched.mixin.common.villager;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.tag.LithostitchedBiomeTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(VillagerType.class)
public class VillagerTypeMixin {
	@Unique
	private static final Map<VillagerType, TagKey<Biome>> TYPE_TO_TAG_CACHE = new HashMap<>();
	
	@Unique
	private static final ResourceKey<VillagerType> UNKNOWN_TYPE = Lithostitched.key(Registries.VILLAGER_TYPE, "unknown");
	
	@ModifyReturnValue(
		method = "byBiome",
		at = @At("RETURN")
	)
	private static VillagerType useBiomeTags(VillagerType originalType, @Local(argsOnly = true, ordinal = 0) Holder<Biome> biome) {
		for (VillagerType type : BuiltInRegistries.VILLAGER_TYPE) {
			TagKey<Biome> biomeTag = TYPE_TO_TAG_CACHE.computeIfAbsent(type, __ -> LithostitchedBiomeTags.createVillagerTypeTag(BuiltInRegistries.VILLAGER_TYPE.getResourceKey(type).orElse(UNKNOWN_TYPE)));
			if (biome.is(biomeTag)) {
				return type;
			}
		}
		return originalType;
	}
}
