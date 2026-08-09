package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.SimpleContext;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public record ShiftDensityFunction(DensityFunction input, DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ) implements DensityFunction {
	public static final MapCodec<ShiftDensityFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		DensityFunction.CODEC.fieldOf("input").forGetter(ShiftDensityFunction::input),
		DensityFunction.CODEC.fieldOf("shift_x").forGetter(ShiftDensityFunction::shiftX),
		DensityFunction.CODEC.fieldOf("shift_y").forGetter(ShiftDensityFunction::shiftY),
		DensityFunction.CODEC.fieldOf("shift_z").forGetter(ShiftDensityFunction::shiftZ)
	).apply(i, ShiftDensityFunction::new));
	
	@Override
	public float compute(FunctionContext context) {
		return this.input.compute(SimpleContext.of(
			context.blockX() + shiftX.compute(context),
			context.blockY() + shiftY.compute(context),
			context.blockZ() + shiftZ.compute(context)
		));
	}
	
	@Override
	public void fillArray(float[] output, ContextProvider contextProvider) {
		contextProvider.fillAllDirectly(output, this);
	}
	
	@Override
	public @NotNull DensityFunction mapChildren(Visitor visitor) {
		return new ShiftDensityFunction(visitor.apply(input), visitor.apply(shiftX),visitor.apply(shiftY), visitor.apply(shiftZ));
	}
	
	@Override
	public Interval range() {
		return this.input.range();
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