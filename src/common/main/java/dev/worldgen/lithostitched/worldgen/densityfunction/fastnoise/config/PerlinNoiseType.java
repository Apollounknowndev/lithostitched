package dev.worldgen.lithostitched.worldgen.densityfunction.fastnoise.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.densityfunction.fastnoise.FNL;

public class PerlinNoiseType extends FastNoiseConfig {
    public static final MapCodec<PerlinNoiseType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.FLOAT.fieldOf("frequency").forGetter(FastNoiseConfig::frequency),
        Codec.INT.optionalFieldOf("salt", 0).forGetter(FastNoiseConfig::salt)
    ).apply(instance, PerlinNoiseType::new));

    PerlinNoiseType(float frequency, int salt) {
        super(frequency, salt);
        fnl.SetNoiseType(FNL.NoiseType.Perlin);
    }

    @Override
    public MapCodec<PerlinNoiseType> getCodec() {
        return CODEC;
    }
}
