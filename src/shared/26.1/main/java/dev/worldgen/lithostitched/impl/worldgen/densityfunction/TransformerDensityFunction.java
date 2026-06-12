package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import net.minecraft.world.level.levelgen.DensityFunction;


public abstract class TransformerDensityFunction implements DensityFunction {
	private final DensityFunction argument;
	
	public TransformerDensityFunction(DensityFunction argument) {
		this.argument = argument;
	}
    
    public DensityFunction argument() {
        return argument;
    }
    
	public abstract double transform(double value);
	
	@Override
	public double compute(FunctionContext functionContext) {
		return transform(this.argument.compute(functionContext));
	}
	
	@Override
	public void fillArray(double[] densities, ContextProvider context) {
		this.argument.fillArray(densities, context);
		
		for (int i = 0; i < densities.length; i++) {
			densities[i] = transform(densities[i]);
		}
	}
	
	@Override
	public double minValue() {
		return transform(this.argument.minValue());
	}
	
	@Override
	public double maxValue() {
		return transform(this.argument.maxValue());
	}
}
