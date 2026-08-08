package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.LithostitchedVersion;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public record MixDensityFunction(DensityFunction input, DensityFunction argument1, DensityFunction argument2) implements DensityFunction {
	public static final MapCodec<MixDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		LithostitchedCodecs.DF_BASE.fieldOf("input").forGetter(MixDensityFunction::input),
		LithostitchedCodecs.DF_BASE.fieldOf("argument1").forGetter(MixDensityFunction::argument1),
		LithostitchedCodecs.DF_BASE.fieldOf("argument2").forGetter(MixDensityFunction::argument2)
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
	public @NotNull DensityFunction mapAll(Visitor visitor) {
		return new MixDensityFunction(input.mapAll(visitor), argument1.mapAll(visitor), argument2.mapAll(visitor));
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