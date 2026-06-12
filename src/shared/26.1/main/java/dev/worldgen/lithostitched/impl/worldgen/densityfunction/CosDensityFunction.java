package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.impl.LithostitchedVersion;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public class CosDensityFunction extends TransformerDensityFunction {
    public static final MapCodec<CosDensityFunction> DATA_CODEC = LithostitchedVersion.DF_CODEC.fieldOf("argument").xmap(CosDensityFunction::new, CosDensityFunction::argument);
    public static KeyDispatchDataCodec<CosDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);
    
    public CosDensityFunction(DensityFunction argument) {
        super(argument);
    }
    
    @Override
    public double transform(double value) {
        return Math.cos(value);
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
        return new CosDensityFunction(this.argument().mapAll(visitor));
    }
    
    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC_HOLDER;
    }
}
