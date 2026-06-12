package dev.worldgen.lithostitched.impl.worldgen.processor;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;

public class ScheduleTickProcessor implements StructureProcessor {
    public static final ScheduleTickProcessor INSTANCE = new ScheduleTickProcessor();
    public static final MapCodec<ScheduleTickProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pivot, BlockPos relative, StructureTemplate.StructureBlockInfo absolute, StructurePlaceSettings settings) {
        if (levelReader instanceof WorldGenLevel level) {
            Lithostitched.scheduleTick(level.getLevel(), absolute.pos(), absolute.state().getBlock(), 0);

            FluidState fluidState = absolute.state().getFluidState();
            if (!fluidState.isEmpty()) {
                Lithostitched.scheduleTick(level.getLevel(), absolute.pos(), fluidState.getType(), 0);
            }
        }
        return absolute;
    }
    
    @Override
    public MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }
}
