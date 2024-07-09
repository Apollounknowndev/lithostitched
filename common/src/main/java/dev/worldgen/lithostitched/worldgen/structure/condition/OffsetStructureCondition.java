package dev.worldgen.lithostitched.worldgen.structure.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;

public record OffsetStructureCondition(StructureCondition condition, BlockPos offset) implements StructureCondition {
    public static final MapCodec<OffsetStructureCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StructureCondition.CODEC.fieldOf("condition").forGetter(OffsetStructureCondition::condition),
        BlockPos.CODEC.fieldOf("offset").forGetter(OffsetStructureCondition::offset)
    ).apply(instance, OffsetStructureCondition::new));

    @Override
    public boolean test(Structure.GenerationContext context, BlockPos pos) {
        return this.condition.test(context, pos.offset(this.offset));
    }

    @Override
    public MapCodec<? extends StructureCondition> codec() {
        return CODEC;
    }
}
