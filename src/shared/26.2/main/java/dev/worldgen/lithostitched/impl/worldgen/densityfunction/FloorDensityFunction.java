package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.impl.LithostitchedVersion;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public class FloorDensityFunction extends TransformerDensityFunction {
    public static final MapCodec<FloorDensityFunction> DATA_CODEC = LithostitchedVersion.DF_CODEC.fieldOf("argument").xmap(FloorDensityFunction::new, FloorDensityFunction::argument);
    public static KeyDispatchDataCodec<FloorDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);
    
    public FloorDensityFunction(DensityFunction argument) {
        super(argument);
    }
    
    @Override
    public double transform(double value) {
        return Math.floor(value);
    }
    
    @Override
    public DensityFunction mapChildren(Visitor visitor) {
        return new FloorDensityFunction(visitor.apply(this.argument()));
    }
    
    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC_HOLDER;
    }
}
