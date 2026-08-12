package dev.worldgen.lithostitched.mixin.common.timeline;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.impl.duck.AttributeLayerBuilderDuck;
import dev.worldgen.lithostitched.impl.duck.BiomeTimelineDuck;
import dev.worldgen.lithostitched.impl.duck.BiomeWeightingDuck;
import dev.worldgen.lithostitched.impl.worldgen.attribute.PositionalTimeBasedLayer;
import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMaps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.LerpFunction;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.timeline.Timeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(EnvironmentAttributeSystem.class)
public class EnvironmentAttributeSystemMixin {
	@WrapOperation(
		method = "bakeLayerSampler",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"
		)
	)
	private <Value> boolean markBiomeTimelinesAsPositionAffected(Stream<Value> stream, Predicate<? super EnvironmentAttributeLayer<Value>> predicate, Operation<Boolean> operation) {
		return operation.call(stream, predicate.or(layer -> layer instanceof PositionalTimeBasedLayer));
	}
	
	@Inject(
		method = "addDynamicLayers",
		at = @At("TAIL")
	)
	private static void addBiomeTimelines(EnvironmentAttributeSystem.Builder builder, Level level, CallbackInfo ci) {
		Registry<Biome> biomes = level.registryAccess().lookupOrThrow(Registries.BIOME);
		BiomeManager biomeManager = level.getBiomeManager();
		
		Stream<EnvironmentAttribute<?>> attributesProvidedByBiomeTimelines = biomes.listElements().flatMap(biome -> {
			Holder<Timeline> timeline = BiomeTimelineDuck.cast(biome.value()).lithostitched$getTimeline();
			if (timeline != null) {
				return timeline.value().attributes().stream();
			}
			return Stream.of();
		}).distinct();
		attributesProvidedByBiomeTimelines.forEach(attribute -> {
			addBiomeTimelineLayerForAttribute(builder, attribute, biomes, biomeManager, level.clockManager());
		});
	}
	
	@Unique
	private static <Value> void addBiomeTimelineLayerForAttribute(EnvironmentAttributeSystem.Builder builder, EnvironmentAttribute<Value> attribute, Registry<Biome> biomes, BiomeManager biomeManager, ClockManager clockManager) {
		Map<Holder<Biome>, EnvironmentAttributeLayer.TimeBased<Value>> trackSamplersByBiome = new HashMap<>();
		for (Holder<Biome> biome : biomes.asHolderIdMap()) {
			Holder<Timeline> timeline = BiomeTimelineDuck.cast(biome.value()).lithostitched$getTimeline();
			if (timeline == null) continue;
			trackSamplersByBiome.put(biome, timeline.value().createTrackSampler(attribute, clockManager));
		}
		
		((AttributeLayerBuilderDuck)builder).addPositionalTimeBasedLayer(
			attribute,
			(baseValue, cacheTickId, pos, biomeWeights) -> {
				if (biomeWeights != null && attribute.isSpatiallyInterpolated()) {
					Reference2DoubleArrayMap<Holder<Biome>> weightsByBiome = ((BiomeWeightingDuck) biomeWeights).lithostitched$getWeights();
					if (weightsByBiome.isEmpty()) {
						return baseValue;
					} else if (weightsByBiome.size() == 1) {
						Holder<Biome> biome = weightsByBiome.keySet().iterator().next();
						EnvironmentAttributeLayer.TimeBased<Value> trackSampler = trackSamplersByBiome.get(biome);
						if (trackSampler != null) {
							return trackSampler.applyTimeBased(baseValue, cacheTickId);
						}
						return baseValue;
					} else {
						LerpFunction<Value> lerp = attribute.type().spatialLerp();
						Value resultValue = null;
						double accumulatedWeight = 0.0;
						for (Reference2DoubleMap.Entry<Holder<Biome>> entry : Reference2DoubleMaps.fastIterable(weightsByBiome)) {
							EnvironmentAttributeLayer.TimeBased<Value> trackSampler = trackSamplersByBiome.get(entry.getKey());
							Value sourceValue;
							if (trackSampler != null) {
								sourceValue = trackSampler.applyTimeBased(baseValue, cacheTickId);
							} else {
								sourceValue = baseValue;
							}
							
							double sourceWeight = entry.getDoubleValue();
							accumulatedWeight += sourceWeight;
							if (resultValue == null) {
								resultValue = sourceValue;
								continue;
							}
							float relativeFraction = (float)(sourceWeight / accumulatedWeight);
							resultValue = lerp.apply(relativeFraction, resultValue, sourceValue);
						}
						return Objects.requireNonNull(resultValue);
					}
				} else {
					Holder<Biome> biome = biomeManager.getNoiseBiomeAtPosition(pos.x, pos.y, pos.z);
					EnvironmentAttributeLayer.TimeBased<Value> trackSampler = trackSamplersByBiome.get(biome);
					if (trackSampler != null) {
						return trackSampler.applyTimeBased(baseValue, cacheTickId);
					}
					return baseValue;
				}
			}
		);
	}
}
