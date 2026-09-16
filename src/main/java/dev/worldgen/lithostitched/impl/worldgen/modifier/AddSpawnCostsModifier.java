package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.SimpleMapCodec;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.WeightedSpawnerData;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MobSpawnSettingsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.attribute.modifier.MobSpawnSettingsModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//? if neoforge {
/*import dev.worldgen.lithostitched.impl.platform.neoforge.worldgen.LithostitchedNeoforgeBiomeModifiers;
import net.neoforged.neoforge.common.world.BiomeModifier;
*///? }

public record AddSpawnCostsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, Map<EntityType<?>, MobSpawnCost> spawnCosts) implements WorldgenModifier /*? if neoforge{*//*, NeoforgeModifierHolder *//*?}*/ {
	public static final SimpleMapCodec<EntityType<?>, MobSpawnCost> SPAWN_COST_CODEC = Codec.simpleMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), MobSpawnCost.CODEC, BuiltInRegistries.ENTITY_TYPE);
	public static final MapCodec<AddSpawnCostsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
		PRIORITY_DEFAULT_CODEC.forGetter(AddSpawnCostsModifier::priority),
		Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddSpawnCostsModifier::biomes),
		SPAWN_COST_CODEC.fieldOf("spawn_costs").forGetter(AddSpawnCostsModifier::spawnCosts)
	).apply(instance, AddSpawnCostsModifier::new));
	
	//? if neoforge {
	/*@Override
	public BiomeModifier createNeoforgeModifier() {
		return new LithostitchedNeoforgeBiomeModifiers.AddSpawnCostsBiomeModifier(this);
	}
	*///? }
	
	@Override
	public void apply(RegistryAccess registries) {
		for (Holder<Biome> entry : this.biomes()) {
			this.applyModifier(registries, entry);
		}
	}
	
	public void applyModifier(RegistryAccess registries, Holder<Biome> biome) {
		EnvironmentAttributeMap.Entry<MobSpawnSettings, ?> spawnEntry = biome.value().getAttributes().get(EnvironmentAttributes.NATURAL_MOB_SPAWNS);
		if (spawnEntry == null) return;
		
		var attributeBuilder = EnvironmentAttributeMap.builder();
		attributeBuilder.putAll(biome.value().getAttributes());
		
		AttributeModifier<MobSpawnSettings, MobSpawnSettings> modifier = (AttributeModifier<MobSpawnSettings, MobSpawnSettings>) spawnEntry.modifier();
		MobSpawnSettings settings = (MobSpawnSettings) spawnEntry.argument();
		var spawnBuilder = new MobSpawnSettings.Builder();
		
		for (MobCategory category : settings.definedCategories()) {
			var mobsInCategory = settings.getMobsInCategory(category);
			if (mobsInCategory == null) continue;
			spawnBuilder.addAllSpawns(category, mobsInCategory);
		}
		spawnBuilder.addAllCosts(settings.allSpawnCosts());
		spawnBuilder.addAllCosts(this.spawnCosts);
		
		attributeBuilder.modify(EnvironmentAttributes.NATURAL_MOB_SPAWNS, modifier, spawnBuilder.build());
		
		((BiomeAccessor)(Object)biome.value()).setAttributes(attributeBuilder.build());
		WorldgenModifier.resetRegistrationInfo(Lithostitched.registry(registries, Registries.BIOME), biome);
	}
	
	@Override
	public MapCodec<? extends WorldgenModifier> codec() {
		return CODEC;
	}
}