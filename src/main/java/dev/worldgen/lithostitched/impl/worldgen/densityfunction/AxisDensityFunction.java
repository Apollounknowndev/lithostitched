package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record AxisDensityFunction(Axis axis) implements DensityFunction {
	public static final MapCodec<AxisDensityFunction> DATA_CODEC = Axis.CODEC.fieldOf("axis").xmap(AxisDensityFunction::new, AxisDensityFunction::axis);
	public static KeyDispatchDataCodec<AxisDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);
	
	@Override
	public double compute(FunctionContext context) {
		return switch (axis) {
			case X -> context.blockX();
			case Y -> context.blockY();
			case Z -> context.blockZ();
		};
	}
	
	@Override
	public void fillArray(double[] densities, ContextProvider context) {
		context.fillAllDirectly(densities, this);
	}
	
	@Override
	public DensityFunction mapChildren(Visitor visitor) {
		return this;
	}
	
	@Override
	public double minValue() {
		return this.axis == Axis.Y ? -4064 : Double.MIN_VALUE;
	}
	
	@Override
	public double maxValue() {
		return this.axis == Axis.Y ? 4064 : Double.MAX_VALUE;
	}
	
	@Override
	public KeyDispatchDataCodec<? extends DensityFunction> codec() {
		return CODEC_HOLDER;
	}
}
