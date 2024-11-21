package dev.worldgen.lithostitched.worldgen.structure.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

public record MultipleOfStructureCondition(List<StructureCondition> conditions, InclusiveRange<Integer> allowedCount) implements StructureCondition {
    public static final MapCodec<MultipleOfStructureCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StructureCondition.BASE_CODEC.listOf().fieldOf("conditions").forGetter(MultipleOfStructureCondition::conditions),
        InclusiveRange.INT.fieldOf("allowed_count").forGetter(MultipleOfStructureCondition::allowedCount)
    ).apply(instance, MultipleOfStructureCondition::new));

    @Override
    public boolean test(Structure.GenerationContext context, BlockPos pos) {

        int count = 0;
        for (StructureCondition condition : this.conditions) {
            if (condition.test(context, pos)) {
                count++;
                if (this.allowedCount.maxInclusive() < count) return false;
            }
        }
        return this.allowedCount.isValueInRange(count);
    }

    @Override
    public MapCodec<? extends StructureCondition> codec() {
        return CODEC;
    }
}
