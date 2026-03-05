package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public class SqrtDensityFunction extends TransformerDensityFunction {
    public static final MapCodec<SqrtDensityFunction> DATA_CODEC =  DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument").xmap(SqrtDensityFunction::new, SqrtDensityFunction::argument);
    public static KeyDispatchDataCodec<SqrtDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);
    
    public SqrtDensityFunction(DensityFunction argument) {
        super(argument);
    }
    
    @Override
    public double transform(double value) {
        return value > 0 ? Math.sqrt(value) : 0;
    }
    
    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return new SqrtDensityFunction(this.argument().mapAll(visitor));
    }
    
    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC_HOLDER;
    }
}
