package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.SimpleMapCodec;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record SetSpawnCostsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, Map<EntityType<?>, MobSpawnCost> spawnCosts, boolean append) implements WorldgenModifier {
	public static final SimpleMapCodec<EntityType<?>, MobSpawnCost> SPAWN_COST_CODEC = Codec.simpleMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.MobSpawnCost.CODEC, BuiltInRegistries.ENTITY_TYPE);
	public static final MapCodec<SetSpawnCostsModifier> CODEC = RecordCodecBuilder.<SetSpawnCostsModifier>mapCodec(instance -> instance.group(
		LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
		PRIORITY_DEFAULT_CODEC.forGetter(SetSpawnCostsModifier::priority),
		Biome.LIST_CODEC.fieldOf("biomes").forGetter(SetSpawnCostsModifier::biomes),
		SPAWN_COST_CODEC.fieldOf("spawn_costs").forGetter(SetSpawnCostsModifier::spawnCosts),
		Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetSpawnCostsModifier::append)
	).apply(instance, SetSpawnCostsModifier::new));
	
	
	@Override
	public void apply(RegistryAccess registries) {
		//? if neoforge
		//if (true) return;
		
		for (Holder<Biome> holder : this.biomes()) {
			Biome biome = holder.value();
			MobSpawnSettingsAccessor accessor = (MobSpawnSettingsAccessor) biome.getMobSettings();
			
			Map<EntityType<?>, MobSpawnCost> spawnCosts = new HashMap<>();
			if (this.append) {
				spawnCosts.putAll(accessor.lithostitched$getSpawnCosts());
			}
			spawnCosts.putAll(this.spawnCosts);
			
			accessor.lithostitched$setSpawnCosts(spawnCosts);
		}
	}
	
	@Override
	public MapCodec<? extends WorldgenModifier> codec() {
		return null;
	}
}
