package dev.worldgen.lithostitched.worldgen.densityfunction.fastnoise.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.densityfunction.fastnoise.FNL;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Function;

public abstract class FastNoiseConfig {
    public static final Codec<FastNoiseConfig> CODEC = Codec.lazyInitialized(() -> {
        var registry = BuiltInRegistries.REGISTRY.getOptional(LithostitchedRegistryKeys.FAST_NOISE_CONFIG_TYPE.identifier());
        if (registry.isEmpty()) throw new NullPointerException("Worldgen modifier registry does not exist yet!");
        return ((Registry<MapCodec<? extends FastNoiseConfig>>) registry.get()).byNameCodec();
    }).dispatch(FastNoiseConfig::getCodec, Function.identity());

    abstract MapCodec<? extends FastNoiseConfig> getCodec();

    protected final FNL fnl;
    private final float frequency;
    private final int salt;

    protected FastNoiseConfig(float frequency, int salt) {
        this.salt = salt;
        this.fnl = new FNL();
        this.frequency = frequency;

        fnl.SetFrequency(frequency);
        fnl.SetFractalType(FNL.FractalType.None);
    }

    public float frequency() {
        return frequency;
    }

    public int salt() {
        return salt;
    }

    public void bind(long seed) {
        fnl.SetSeed((int) seed + salt);
    }

    public double sample(double x, double y, double z) {
        return fnl.GetNoise(x, y, z);
    }
}
