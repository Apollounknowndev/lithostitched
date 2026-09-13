package dev.worldgen.lithostitched.api.worldgen.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.NotNull;

public record WeightedSpawnerData(EntityType<?> type, int weight, IntProvider count) {
	public static final Codec<WeightedSpawnerData> CODEC = RecordCodecBuilder.<WeightedSpawnerData>create(i -> i.group(
		BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(WeightedSpawnerData::type),
		Codec.INT.fieldOf("weight").forGetter(WeightedSpawnerData::weight),
		IntProviders.POSITIVE_CODEC.fieldOf("count").forGetter(WeightedSpawnerData::count)
	).apply(i, WeightedSpawnerData::new));
	
	public WeightedSpawnerData {
		type = type.getCategory() == MobCategory.MISC ? EntityTypes.PIG : type;
	}
	
	@NotNull
	@Override
	public String toString() {
		return EntityType.getKey(this.type) + "*(" + this.count + ")";
	}
}