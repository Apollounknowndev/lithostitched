package dev.worldgen.lithostitched.api.worldgen.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public record BiomeClimate(Optional<Boolean> hasPrecipitation, Optional<Float> temperature, Optional<Biome.TemperatureModifier> temperatureModifier, Optional<Float> downfall) {
    public static final MapCodec<BiomeClimate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("has_precipitation").forGetter(BiomeClimate::hasPrecipitation),
        Codec.FLOAT.optionalFieldOf("temperature").forGetter(BiomeClimate::temperature),
        Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier").forGetter(BiomeClimate::temperatureModifier),
        Codec.FLOAT.optionalFieldOf("downfall").forGetter(BiomeClimate::downfall)
    ).apply(instance, BiomeClimate::new));
}