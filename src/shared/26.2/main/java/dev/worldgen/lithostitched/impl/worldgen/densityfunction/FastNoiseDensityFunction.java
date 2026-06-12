package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import dev.worldgen.lithostitched.impl.LithostitchedVersion;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public record FastNoiseDensityFunction(Holder<FastNoiseConfig> config, double xzScale, double yScale, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) implements DensityFunction {
    public static final MapCodec<FastNoiseDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
        RegistryFileCodec.create(LithostitchedRegistries.FAST_NOISE_CONFIG, FastNoiseConfig.CODEC, false).fieldOf("config").forGetter(FastNoiseDensityFunction::config),
        Codec.DOUBLE.optionalFieldOf("xz_scale", 1.0).forGetter(FastNoiseDensityFunction::xzScale),
        Codec.DOUBLE.optionalFieldOf("y_scale", 1.0).forGetter(FastNoiseDensityFunction::yScale),
        LithostitchedVersion.DF_CODEC.optionalFieldOf("shift_x", DensityFunctions.zero()).forGetter(FastNoiseDensityFunction::shiftX),
        LithostitchedVersion.DF_CODEC.optionalFieldOf("shift_y", DensityFunctions.zero()).forGetter(FastNoiseDensityFunction::shiftY),
        LithostitchedVersion.DF_CODEC.optionalFieldOf("shift_z", DensityFunctions.zero()).forGetter(FastNoiseDensityFunction::shiftZ)
    ).apply(instance, FastNoiseDensityFunction::new));
    public static final KeyDispatchDataCodec<FastNoiseDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

    @Override
    public double compute(FunctionContext context) {
        return config.value().sample(
            context.blockX() * xzScale + shiftX.compute(context),
            context.blockY() * yScale + shiftY.compute(context),
            context.blockZ() * xzScale + shiftZ.compute(context)
        );
    }

    @Override
    public void fillArray(double[] doubles, ContextProvider contextProvider) {
        contextProvider.fillAllDirectly(doubles, this);
    }

    @Override
    public DensityFunction mapChildren(Visitor visitor) {
        return new FastNoiseDensityFunction(this.config, this.xzScale, this.yScale, visitor.apply(this.shiftX), visitor.apply(this.shiftY), visitor.apply(this.shiftZ));
    }

    @Override
    public double minValue() {
        return -1;
    }

    @Override
    public double maxValue() {
        return 1;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
