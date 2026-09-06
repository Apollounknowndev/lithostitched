package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SelectDensityFunction(DensityFunction input, DensityFunction fallback, List<Selection> selections, double min, double max) implements DensityFunction {
	public static final MapCodec<SelectDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		LithostitchedCodecs.DF_BASE.fieldOf("input").forGetter(SelectDensityFunction::input),
		LithostitchedCodecs.DF_BASE.fieldOf("fallback").forGetter(SelectDensityFunction::fallback),
		Selection.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("selections").forGetter(SelectDensityFunction::selections)
	).apply(i, SelectDensityFunction::create));
	public static final KeyDispatchDataCodec<SelectDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);
	
	public static SelectDensityFunction create(DensityFunction input, DensityFunction fallback, List<Selection> selections) {
		double min = -Double.MAX_VALUE;
		double max = Double.MAX_VALUE;
		for (Selection selection : selections) {
			min = Math.min(min, selection.function.minValue());
			max = Math.max(max, selection.function.maxValue());
		}
		return new SelectDensityFunction(input, fallback, selections, min, max);
	}
	
	
	@Override
	public double compute(FunctionContext context) {
		double value = input.compute(context);
		for (Selection selection : selections) {
			if (selection.range.isValueInRange(value)) {
				return selection.function.compute(context);
			}
		}
		return fallback.compute(context);
	}
	
	@Override
	public void fillArray(double[] densities, ContextProvider applier) {
		applier.fillAllDirectly(densities, this);
	}
	
	@Override
	public @NotNull DensityFunction mapAll(Visitor visitor) {
		return new SelectDensityFunction(input.mapAll(visitor), fallback.mapAll(visitor), selections.stream().map(selection -> selection.mapAll(visitor)).toList(), min, max);
	}
	
	@Override
	public double minValue() {
		return min;
	}
	
	@Override
	public double maxValue() {
		return max;
	}
	
	@Override
	public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
		return CODEC;
	}
	
	public record Selection(InclusiveRange<Double> range, DensityFunction function) {
		public static final Codec<Selection> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			LithostitchedCodecs.DOUBLE_RANGE.fieldOf("range").forGetter(Selection::range),
			LithostitchedCodecs.DF_BASE.fieldOf("function").forGetter(Selection::function)
		).apply(instance, Selection::new));
		
		public Selection mapAll(Visitor visitor) {
			return new Selection(range, function.mapAll(visitor));
		}
		
		public static Selection create(Pair<InclusiveRange<Double>, DensityFunction> pair) {
			return new Selection(pair.getFirst(), pair.getSecond());
		}
	}
}