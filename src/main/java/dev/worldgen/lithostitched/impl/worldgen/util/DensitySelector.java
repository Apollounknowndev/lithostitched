package dev.worldgen.lithostitched.impl.worldgen.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import net.minecraft.util.InclusiveRange;

import java.util.List;

public record DensitySelector<T>(List<Entry<T>> entries, T fallback) {
	public static <T> Codec<DensitySelector<T>> codec(Codec<T> valueCodec) {
		return RecordCodecBuilder.<DensitySelector<T>>create(i -> i.group(
			Entry.codec(valueCodec).listOf().fieldOf("entries").forGetter(DensitySelector::entries),
			valueCodec.fieldOf("fallback").forGetter(DensitySelector::fallback)
		).apply(i, DensitySelector::new)).validate(list -> {
			int entries = list.entries().size();
			for (int a = 0; a < entries; a++) {
				for (int b = 0; b < entries; b++) {
					if (a >= b) continue;
					var rangeA = list.entries().get(a).acceptableDensity();
					var rangeB = list.entries().get(b).acceptableDensity();
					if (rangeA.minInclusive() < rangeB.maxInclusive() && rangeB.minInclusive() < rangeA.maxInclusive()) {
						return DataResult.error(() -> "Density ranges cannot overlap: " + rangeA + " and " + rangeB);
					}
				}
			}
			return DataResult.success(list);
		});
	}
	
	public T pick(float density) {
		for (var entry : this.entries()) {
			if (entry.acceptableDensity().isValueInRange(density)) {
				return entry.value();
			}
		}
		return this.fallback();
	}
	
	public record Entry<T>(InclusiveRange<Float> acceptableDensity, T value) {
		public static <T> Codec<Entry<T>> codec(Codec<T> valueCodec) {
			return RecordCodecBuilder.create(i -> i.group(
				LithostitchedCodecs.FLOAT_RANGE.fieldOf("acceptable_density").forGetter(Entry::acceptableDensity),
				valueCodec.fieldOf("value").forGetter(Entry::value)
			).apply(i, Entry::new));
		}
	}
}
