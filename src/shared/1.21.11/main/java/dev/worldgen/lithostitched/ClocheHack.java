package dev.worldgen.lithostitched;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingConfig;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ClocheHack {
	public static final MapCodec<DelegatingConfig> DELEGATING_CONFIG_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Structure.CODEC.fieldOf("delegate").forGetter(DelegatingConfig::delegate),
		PlacementCondition.CODEC.optionalFieldOf("spawn_condition").forGetter(DelegatingConfig::spawnCondition),
		EnvironmentAttributeMap.CODEC_ONLY_POSITIONAL.optionalFieldOf("attributes", EnvironmentAttributeMap.EMPTY).forGetter(config -> (EnvironmentAttributeMap) config.getAttributes())
	).apply(instance, DelegatingConfig::new));
}
