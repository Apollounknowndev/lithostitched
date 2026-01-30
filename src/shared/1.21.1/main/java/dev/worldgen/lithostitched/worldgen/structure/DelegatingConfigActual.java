package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.msrandom.multiplatform.annotations.Actual;

public class DelegatingConfigActual {
	@Actual
	public static MapCodec<DelegatingConfig> getCodec() {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			Structure.CODEC.fieldOf("delegate").forGetter(DelegatingConfig::delegate),
			PlacementCondition.CODEC.optionalFieldOf("spawn_condition").forGetter(DelegatingConfig::spawnCondition)
		).apply(instance, DelegatingConfig::new));
	}
}
