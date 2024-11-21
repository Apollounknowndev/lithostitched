package dev.worldgen.lithostitched.worldgen.structure.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

public record AnyOfStructureCondition(List<StructureCondition> conditions) implements StructureCondition {
    public static final MapCodec<AnyOfStructureCondition> CODEC = StructureCondition.BASE_CODEC.listOf().fieldOf("conditions").xmap(AnyOfStructureCondition::new, AnyOfStructureCondition::conditions);

    @Override
    public boolean test(Structure.GenerationContext context, BlockPos pos) {
        for (StructureCondition condition : this.conditions) {
            if (condition.test(context, pos)) return true;
        }
        return false;
    }

    @Override
    public MapCodec<? extends StructureCondition> codec() {
        return CODEC;
    }
}
