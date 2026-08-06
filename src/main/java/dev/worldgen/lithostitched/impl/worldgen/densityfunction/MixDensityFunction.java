package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.platform.LithostitchedVersion;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public record MixDensityFunction(DensityFunction input, DensityFunction argument1, DensityFunction argument2) implements DensityFunction {
	public static final MapCodec<MixDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		LithostitchedVersion.DF_CODEC.fieldOf("input").forGetter(MixDensityFunction::input),
		LithostitchedVersion.DF_CODEC.fieldOf("argument1").forGetter(MixDensityFunction::argument1),
		LithostitchedVersion.DF_CODEC.fieldOf("argument2").forGetter(MixDensityFunction::argument2)
	).apply(i, MixDensityFunction::new));
	public static final KeyDispatchDataCodec<MixDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
	
	@Override
	public double compute(FunctionContext context) {
		double input = this.input.compute(context);
		if (input <= 0) {
			return this.argument1.compute(context);
		}
		if (input >= 1) {
			return this.argument2.compute(context);
		}
		double argument1 = this.argument1.compute(context);
		double argument2 = this.argument2.compute(context);
		return argument1 * (1 - input) + argument2 * input;
	}
	
	@Override
	public void fillArray(double[] densities, ContextProvider applier) {
		applier.fillAllDirectly(densities, this);
	}
	
	@Override
	public @NotNull DensityFunction mapChildren(Visitor visitor) {
		return new MixDensityFunction(visitor.apply(input), visitor.apply(argument1), visitor.apply(argument2));
	}
	
	@Override
	public double minValue() {
		return Math.min(argument1.minValue(), argument2.minValue());
	}
	
	@Override
	public double maxValue() {
		return Math.min(argument1.maxValue(), argument2.maxValue());
	}
	
	@Override
	public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
		return CODEC;
	}
}