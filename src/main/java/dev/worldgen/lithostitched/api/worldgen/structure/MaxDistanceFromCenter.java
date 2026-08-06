package dev.worldgen.lithostitched.api.worldgen.structure;


import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.function.Function;

public record MaxDistanceFromCenter(int horizontal, int vertical) {
	private static final Codec<Integer> BASE_CODEC = Codec.intRange(1, 128);
	private static final Codec<MaxDistanceFromCenter> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BASE_CODEC.fieldOf("horizontal").forGetter(MaxDistanceFromCenter::horizontal),
		ExtraCodecs.intRange(1, DimensionType.Y_SIZE).optionalFieldOf("vertical", DimensionType.Y_SIZE).forGetter(MaxDistanceFromCenter::vertical)
	).apply(instance, MaxDistanceFromCenter::new));
	
	public static final Codec<MaxDistanceFromCenter> CODEC = Codec.either(FULL_CODEC, BASE_CODEC).xmap(
		either -> either.map(Function.identity(), MaxDistanceFromCenter::new),
		maxDistance -> maxDistance.horizontal == maxDistance.vertical ? Either.right(maxDistance.horizontal) : Either.left(maxDistance)
	);
	
	public MaxDistanceFromCenter(int value) {
		this(value, value);
	}
	
	public static MaxDistanceFromCenter of(int value) {
		return new MaxDistanceFromCenter(value);
	}
	
	public static MaxDistanceFromCenter of(int horizontal, int vertical) {
		return new MaxDistanceFromCenter(horizontal, vertical);
	}
}