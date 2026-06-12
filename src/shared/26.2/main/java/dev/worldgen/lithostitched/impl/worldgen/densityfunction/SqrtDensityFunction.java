package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.impl.LithostitchedVersion;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public class SqrtDensityFunction extends TransformerDensityFunction {
    public static final MapCodec<SqrtDensityFunction> DATA_CODEC = LithostitchedVersion.DF_CODEC.fieldOf("argument").xmap(SqrtDensityFunction::new, SqrtDensityFunction::argument);
    public static KeyDispatchDataCodec<SqrtDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);
    
    public SqrtDensityFunction(DensityFunction argument) {
        super(argument);
    }
    
    @Override
    public double transform(double value) {
        if (value == 0) return 0;
        if (value > 0) return Math.sqrt(value);
        return -Math.sqrt(-value);
    }
    
    @Override
    public DensityFunction mapChildren(Visitor visitor) {
        return new SqrtDensityFunction(visitor.apply(this.argument()));
    }
    
    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC_HOLDER;
    }
}
