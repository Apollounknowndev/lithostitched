package dev.worldgen.lithostitched.impl.worldgen.modifier.target.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public record ClimateTarget<T>(ResourceKey<Registry<T>> key, Optional<InclusiveRange<Float>> temperature, Optional<InclusiveRange<Float>> downfall, Optional<Boolean> hasPrecipitation, Optional<Biome.TemperatureModifier> temperatureModifier) implements HolderTarget<T> {
	public static final Type TYPE = new Type() {
		@Override
		public <A> DataResult<MapCodec<? extends HolderTarget<A>>> apply(ResourceKey<Registry<A>> key) {
			if (!key.equals(Registries.BIOME)) {
				return DataResult.error(() -> "Cannot use \"biome/climate\" target type outside of biomes");
			}
			return DataResult.success(RecordCodecBuilder.<ClimateTarget<A>>mapCodec(i -> i.group(
				LithostitchedCodecs.FLOAT_RANGE.optionalFieldOf("temperature").forGetter(ClimateTarget::temperature),
				LithostitchedCodecs.FLOAT_RANGE.optionalFieldOf("downfall").forGetter(ClimateTarget::downfall),
				Codec.BOOL.optionalFieldOf("has_precipitation").forGetter(ClimateTarget::hasPrecipitation),
				Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier").forGetter(ClimateTarget::temperatureModifier)
			).apply(i, (a, b, c, d) -> new ClimateTarget<>(key, a, b, c, d))));
		}
	};
	
	@Override
	public Iterable<Holder<T>> getHolders(RegistryAccess registries) {
		if (!this.key.equals(Registries.BIOME)) throw new IllegalStateException("Cannot use \"biome/climate\" target type outside of biomes");
		
		return registries.lookupOrThrow(Registries.BIOME).listElements().filter(biome -> this.test(biome.value())).map(t -> (Holder<T>) t).toList();
	}
	
	private boolean test(Biome biome) {
		var climate = ((BiomeAccessor) (Object) biome).getClimateSettings();
		
		if (!this.temperature.map(range -> range.isValueInRange(climate.temperature())).orElse(true)) return false;
		if (!this.downfall.map(range -> range.isValueInRange(climate.downfall())).orElse(true)) return false;
		if (!this.hasPrecipitation.map(bool -> bool == climate.hasPrecipitation()).orElse(true)) return false;
		return this.temperatureModifier.map(modifier -> modifier == climate.temperatureModifier()).orElse(true);
	}
	
	@Override
	public Type getType() {
		return null;
	}
}
