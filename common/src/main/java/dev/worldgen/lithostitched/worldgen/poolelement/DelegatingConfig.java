package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

import java.util.Optional;

/**
 * Config for the delegating pool element type.
 * @param delegate The pool element that all functionality is delegated to.
 * @param allowedDepth The depth values that this element may spawn in.
 * @param forcedCount The number of instances of this element to "force" to place (attempt to place in every allowed position before testing other pieces). Mutually exclusive with maxCount.
 * @param maxCount The maximum number of instances of this element that can be placed. Mutually exclusive with forcedCount.
 */
public record DelegatingConfig(StructurePoolElement delegate, Optional<PlacementCondition> placementCondition, Optional<InclusiveRange<Integer>> allowedDepth, Optional<Integer> forcedCount, Optional<Integer> maxCount, Optional<TerrainAdjustment> overrideTerrainAdaption) {
    public static final MapCodec<DelegatingConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StructurePoolElement.CODEC.fieldOf("delegate").forGetter(DelegatingConfig::delegate),
        PlacementCondition.CODEC.optionalFieldOf("condition").forGetter(DelegatingConfig::placementCondition),
        InclusiveRange.INT.optionalFieldOf("allowed_depth").forGetter(DelegatingConfig::allowedDepth),
        ExtraCodecs.POSITIVE_INT.optionalFieldOf("forced_count").forGetter(DelegatingConfig::forcedCount),
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_count").forGetter(DelegatingConfig::maxCount),
        TerrainAdjustment.CODEC.optionalFieldOf("override_terrain_adaption").forGetter(DelegatingConfig::overrideTerrainAdaption)
    ).apply(instance, DelegatingConfig::new));

    public DelegatingConfig(StructurePoolElement delegate, Optional<PlacementCondition> placementCondition, Optional<InclusiveRange<Integer>> allowedDepth, Optional<Integer> forcedCount, Optional<Integer> maxCount, Optional<TerrainAdjustment> overrideTerrainAdaption) {
        if (forcedCount.isPresent() && maxCount.isPresent()) {
            throw new IllegalArgumentException("min_count and max_count cannot both be present.");
        } else {
            this.delegate = delegate;
            this.placementCondition = placementCondition;
            this.allowedDepth = allowedDepth;
            this.forcedCount = forcedCount;
            this.maxCount = maxCount;
            this.overrideTerrainAdaption = overrideTerrainAdaption;
        }
    }

    public boolean isPlacementValid(Structure.GenerationContext context, BlockPos pos, int depth, int count) {
        boolean validDepth = allowedDepth.map(range -> range.isValueInRange(depth)).orElse(true);
        boolean validCount = this.forcedCount.map(forced -> count < forced).orElse(true) && this.maxCount.map(max -> count < max).orElse(true);
        boolean validCondition = this.placementCondition.map(condition -> condition.test(context, pos)).orElse(true);

        return validDepth && validCount && validCondition;
    }
}
