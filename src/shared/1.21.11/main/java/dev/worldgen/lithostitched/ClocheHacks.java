package dev.worldgen.lithostitched;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingConfig;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;
import java.util.function.Function;

public class ClocheHacks {
	public static final MapCodec<DelegatingConfig> DELEGATING_CONFIG_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Structure.CODEC.fieldOf("delegate").forGetter(DelegatingConfig::delegate),
		PlacementCondition.CODEC.optionalFieldOf("spawn_condition").forGetter(DelegatingConfig::spawnCondition),
		EnvironmentAttributeMap.CODEC_ONLY_POSITIONAL.optionalFieldOf("attributes", EnvironmentAttributeMap.EMPTY).forGetter(config -> (EnvironmentAttributeMap) config.getAttributes())
	).apply(instance, DelegatingConfig::new));
	
	public static <T> void applyAttribute(BiomeEffects effects, EnvironmentAttributeMap.Builder builder, Function<BiomeEffects, Optional<T>> getter, EnvironmentAttribute<T> attribute) {
		Optional<T> value = getter.apply(effects);
		value.ifPresent(object -> builder.set(attribute, object));
	}
}
