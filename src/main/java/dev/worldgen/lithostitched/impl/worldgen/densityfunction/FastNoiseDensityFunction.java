package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.fastnoise.FastNoiseConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public record FastNoiseDensityFunction(Holder<FastNoiseConfig> config, double xzScale, double yScale, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) implements DensityFunction {
    public static final MapCodec<FastNoiseDensityFunction> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
        RegistryCodecs.holder(LithostitchedRegistries.FAST_NOISE_CONFIG).fieldOf("config").forGetter(FastNoiseDensityFunction::config),
        Codec.DOUBLE.optionalFieldOf("xz_scale", 1.0).forGetter(FastNoiseDensityFunction::xzScale),
        Codec.DOUBLE.optionalFieldOf("y_scale", 1.0).forGetter(FastNoiseDensityFunction::yScale),
        DensityFunction.CODEC.optionalFieldOf("shift_x", DensityFunctions.zero()).forGetter(FastNoiseDensityFunction::shiftX),
        DensityFunction.CODEC.optionalFieldOf("shift_y", DensityFunctions.zero()).forGetter(FastNoiseDensityFunction::shiftY),
        DensityFunction.CODEC.optionalFieldOf("shift_z", DensityFunctions.zero()).forGetter(FastNoiseDensityFunction::shiftZ)
    ).apply(instance, FastNoiseDensityFunction::new));

    @Override
    public float compute(FunctionContext context) {
        return this.config.value().sample(
            context.blockX() * xzScale + shiftX.compute(context),
            context.blockY() * yScale + shiftY.compute(context),
            context.blockZ() * xzScale + shiftZ.compute(context)
        );
    }
    
    @Override
    public void fillArray(float[] output, ContextProvider contextProvider) {
        contextProvider.fillAllDirectly(output, this);
    }

    @Override
    public DensityFunction mapChildren(Visitor visitor) {
        return new FastNoiseDensityFunction(this.config, this.xzScale, this.yScale, visitor.apply(this.shiftX), visitor.apply(this.shiftY), visitor.apply(this.shiftZ));
    }
    
    @Override
    public Interval range() {
        return Interval.of(-1, 1);
    }
    
    @Override
    public @Axes int domainAxes() {
        return ALL_AXES;
    }
    
    @Override
    public MapCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
