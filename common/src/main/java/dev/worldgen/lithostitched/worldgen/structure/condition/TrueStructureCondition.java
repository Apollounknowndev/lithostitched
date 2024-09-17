package dev.worldgen.lithostitched.worldgen.structure.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;

public class TrueStructureCondition implements StructureCondition {
    public static final TrueStructureCondition INSTANCE = new TrueStructureCondition();
    public static final MapCodec<TrueStructureCondition> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public boolean test(Structure.GenerationContext context, BlockPos pos) {
        return true;
    }

    @Override
    public MapCodec<? extends StructureCondition> codec() {
        return CODEC;
    }
}
