package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.platform.LithostitchedVersion;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public class CeilDensityFunction extends TransformerDensityFunction {
    public static final MapCodec<CeilDensityFunction> DATA_CODEC = LithostitchedVersion.DF_CODEC.fieldOf("argument").xmap(CeilDensityFunction::new, CeilDensityFunction::argument);
    public static KeyDispatchDataCodec<CeilDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);
    
    public CeilDensityFunction(DensityFunction argument) {
        super(argument);
    }
    
    @Override
    public double transform(double value) {
        return Math.ceil(value);
    }
    
    @Override
    public DensityFunction mapChildren(Visitor visitor) {
        return new CeilDensityFunction(visitor.apply(this.argument()));
    }
    
    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC_HOLDER;
    }
}
