package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.impl.LithostitchedVersion;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public class SinDensityFunction extends TransformerDensityFunction {
    public static final MapCodec<SinDensityFunction> DATA_CODEC = LithostitchedVersion.DF_CODEC.fieldOf("argument").xmap(SinDensityFunction::new, SinDensityFunction::argument);
    public static KeyDispatchDataCodec<SinDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);
    
    public SinDensityFunction(DensityFunction argument) {
        super(argument);
    }
    
    @Override
    public double transform(double value) {
        return Math.sin(value);
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
    public DensityFunction mapAll(Visitor visitor) {
        return new SinDensityFunction(this.argument().mapAll(visitor));
    }
    
    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC_HOLDER;
    }
}
