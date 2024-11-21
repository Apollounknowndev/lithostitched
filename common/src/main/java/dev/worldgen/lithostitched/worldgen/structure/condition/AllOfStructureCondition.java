package dev.worldgen.lithostitched.worldgen.structure.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public record AllOfStructureCondition(List<StructureCondition> conditions) implements StructureCondition {
    public static final MapCodec<AllOfStructureCondition> CODEC = StructureCondition.BASE_CODEC.listOf().fieldOf("conditions").xmap(AllOfStructureCondition::new, AllOfStructureCondition::conditions);

    public AllOfStructureCondition(List<StructureCondition> conditions) {
        this.conditions = new ArrayList<>(conditions);
    }

    public void appendCondition(StructureCondition condition) {
        this.conditions.add(condition);
    }

    @Override
    public boolean test(Structure.GenerationContext context, BlockPos pos) {
        for (StructureCondition condition : this.conditions) {
            if (!condition.test(context, pos)) return false;
        }
        return true;
    }

    @Override
    public MapCodec<? extends StructureCondition> codec() {
        return CODEC;
    }
}
