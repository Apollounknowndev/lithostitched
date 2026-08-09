package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.DensityFunction;

public record CosDensityFunction(DensityFunction input) implements DensityFunction {
    public static final MapCodec<CosDensityFunction> CODEC = DensityFunction.CODEC.fieldOf("input").xmap(CosDensityFunction::new, CosDensityFunction::input);
    
    @Override
    public float compute(FunctionContext context) {
        return (float) Math.cos(this.input.compute(context));
    }
    
    @Override
    public void fillArray(float[] output, ContextProvider contextProvider) {
        this.input().fillArray(output, contextProvider);
        
        for(int i = 0; i < output.length; ++i) {
            output[i] = (float) Math.cos(output[i]);
        }
    }
    
    @Override
    public DensityFunction mapChildren(Visitor visitor) {
        return new CosDensityFunction(visitor.apply(this.input));
    }
    
    @Override
    public Interval range() {
        return Interval.of(-1, 1);
    }
    
    @Override
    public @Axes int domainAxes() {
        return this.input.domainAxes();
    }
    
    @Override
    public MapCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
