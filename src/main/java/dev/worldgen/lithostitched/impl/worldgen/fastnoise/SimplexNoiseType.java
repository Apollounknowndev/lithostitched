package dev.worldgen.lithostitched.impl.worldgen.fastnoise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FNL;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FNL.FractalType;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

import java.util.Optional;

public class SimplexNoiseType extends FastNoiseConfig {
    public static final MapCodec<SimplexNoiseType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.FLOAT.fieldOf("frequency").forGetter(FastNoiseConfig::frequency),
        Codec.INT.optionalFieldOf("salt", 0).forGetter(FastNoiseConfig::salt),
        StringRepresentable.fromValues(FractalType::values).optionalFieldOf("fractal_type", FractalType.None).forGetter(s -> s.fractalType),
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("octaves").forGetter(s -> s.octaves),
        Codec.FLOAT.optionalFieldOf("lacunarity").forGetter(s -> s.lacunarity),
        Codec.FLOAT.optionalFieldOf("gain").forGetter(s -> s.gain)
    ).apply(instance, SimplexNoiseType::new));

    private final FractalType fractalType;
    private final Optional<Integer> octaves;
    private final Optional<Float> lacunarity;
    private final Optional<Float> gain;
    
    public SimplexNoiseType(float frequency, int salt, FractalType fractalType, Optional<Integer> octaves, Optional<Float> lacunarity, Optional<Float> gain) {
        super(frequency, salt);
        this.fractalType = fractalType;
        this.octaves = octaves;
        this.lacunarity = lacunarity;
        this.gain = gain;

        fnl.SetNoiseType(FNL.NoiseType.OpenSimplex2S);
        fnl.SetFractalType(fractalType);
        octaves.ifPresent(fnl::SetFractalOctaves);
        lacunarity.ifPresent(fnl::SetFractalLacunarity);
        gain.ifPresent(fnl::SetFractalGain);
    }

    @Override
    public MapCodec<SimplexNoiseType> getCodec() {
        return CODEC;
    }
}
