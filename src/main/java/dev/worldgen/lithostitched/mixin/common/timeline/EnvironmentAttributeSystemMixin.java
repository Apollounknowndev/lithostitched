package dev.worldgen.lithostitched.mixin.common.timeline;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.worldgen.lithostitched.impl.duck.AttributeLayerBuilderDuck;
import dev.worldgen.lithostitched.impl.duck.BiomeTimelineDuck;
import dev.worldgen.lithostitched.impl.duck.BiomeWeightingDuck;
import dev.worldgen.lithostitched.worldgen.attribute.PositionalTimeBasedLayer;
import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMaps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.EnvironmentAttributeLayer.TimeBased;
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

import java.util.*;
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
		method = "addDefaultLayers",
		at = @At("TAIL")
	)
	private static void addBiomeTimelines(EnvironmentAttributeSystem.Builder builder, Level level, CallbackInfo ci) {
		Registry<Biome> biomes = level.registryAccess().lookupOrThrow(Registries.BIOME);
		BiomeManager biomeManager = level.getBiomeManager();
		
		Stream<EnvironmentAttribute<?>> attributesProvidedByBiomeTimelines = biomes.listElements().flatMap(biome ->
			BiomeTimelineDuck.cast(biome.value()).lithostitched$getTimelines().stream().flatMap(timeline -> timeline.value().attributes().stream())
		).distinct();
		attributesProvidedByBiomeTimelines.forEach(attribute -> {
			addBiomeTimelineLayerForAttribute(builder, attribute, biomes, biomeManager, level.clockManager());
		});
	}
	
	@Unique
	private static <Value> void addBiomeTimelineLayerForAttribute(EnvironmentAttributeSystem.Builder builder, EnvironmentAttribute<Value> attribute, Registry<Biome> biomes, BiomeManager biomeManager, ClockManager clockManager) {
		Map<Holder<Biome>, List<TimeBased<Value>>> trackSamplersByBiome = new HashMap<>();
		for (Holder<Biome> biome : biomes.asHolderIdMap()) {
			List<TimeBased<Value>> trackSamplers = new ArrayList<>();
			for (Holder<Timeline> timeline : BiomeTimelineDuck.cast(biome.value()).lithostitched$getTimelines()) {
				trackSamplers.add(timeline.value().createTrackSampler(attribute, clockManager));
			}
			trackSamplersByBiome.put(biome, trackSamplers);
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
						Value value = baseValue;
						for (TimeBased<Value> trackSampler : trackSamplersByBiome.getOrDefault(biome, List.of())) {
							value = trackSampler.applyTimeBased(value, cacheTickId);
						}
						return value;
					} else {
						LerpFunction<Value> lerp = attribute.type().spatialLerp();
						Value resultValue = null;
						double accumulatedWeight = 0.0;
						for (Reference2DoubleMap.Entry<Holder<Biome>> entry : Reference2DoubleMaps.fastIterable(weightsByBiome)) {
							Value sourceValue = baseValue;
							for (TimeBased<Value> trackSampler : trackSamplersByBiome.getOrDefault(entry.getKey(), List.of())) {
								sourceValue = trackSampler.applyTimeBased(sourceValue, cacheTickId);
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
					Value value = baseValue;
					for (TimeBased<Value> trackSampler : trackSamplersByBiome.getOrDefault(biome, List.of())) {
						value = trackSampler.applyTimeBased(value, cacheTickId);
					}
					return value;
				}
			}
		);
	}
}
