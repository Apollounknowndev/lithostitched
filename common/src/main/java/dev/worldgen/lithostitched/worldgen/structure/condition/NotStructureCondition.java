package dev.worldgen.lithostitched.worldgen.structure.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;

public record NotStructureCondition(StructureCondition condition) implements StructureCondition {
    public static final MapCodec<NotStructureCondition> CODEC = StructureCondition.BASE_CODEC.fieldOf("condition").xmap(NotStructureCondition::new, NotStructureCondition::condition);

    @Override
    public boolean test(Structure.GenerationContext context, BlockPos pos) {
        return !this.condition.test(context, pos);
    }

    @Override
    public MapCodec<? extends StructureCondition> codec() {
        return CODEC;
    }
}
