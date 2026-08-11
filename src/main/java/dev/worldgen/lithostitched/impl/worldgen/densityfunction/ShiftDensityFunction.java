package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.SimpleContext;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public record ShiftDensityFunction(DensityFunction input, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) implements DensityFunction {
	public static final MapCodec<ShiftDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		LithostitchedCodecs.DF_BASE.fieldOf("input").forGetter(ShiftDensityFunction::input),
		LithostitchedCodecs.DF_BASE.fieldOf("shift_x").forGetter(ShiftDensityFunction::shiftX),
		LithostitchedCodecs.DF_BASE.fieldOf("shift_y").forGetter(ShiftDensityFunction::shiftY),
		LithostitchedCodecs.DF_BASE.fieldOf("shift_z").forGetter(ShiftDensityFunction::shiftZ)
	).apply(i, ShiftDensityFunction::new));
	public static final KeyDispatchDataCodec<ShiftDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
	
	@Override
	public double compute(FunctionContext context) {
		return input.compute(SimpleContext.of(
			context.blockX() + shiftX.compute(context),
			context.blockY() + shiftY.compute(context),
			context.blockZ() + shiftZ.compute(context)
		));
	}
	
	@Override
	public void fillArray(double[] densities, ContextProvider applier) {
		applier.fillAllDirectly(densities, this);
	}
	
	@Override
	public @NotNull DensityFunction mapAll(Visitor visitor) {
		return new ShiftDensityFunction(input.mapAll(visitor), shiftX.mapAll(visitor), shiftY.mapAll(visitor), shiftZ.mapAll(visitor));
	}
	
	@Override
	public double minValue() {
		return input.minValue();
	}
	
	@Override
	public double maxValue() {
		return input.maxValue();
	}
	
	@Override
	public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
		return CODEC;
	}
}