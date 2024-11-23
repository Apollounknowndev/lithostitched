package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;

public class ScheduleTickProcessor extends StructureProcessor {
    public static final ScheduleTickProcessor INSTANCE = new ScheduleTickProcessor();
    public static final MapCodec<ScheduleTickProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
    public static final StructureProcessorType<ScheduleTickProcessor> TYPE = () -> CODEC;

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo relative, StructureTemplate.StructureBlockInfo absolute, StructurePlaceSettings settings) {
        if (levelReader instanceof WorldGenLevel level) {
            level.scheduleTick(absolute.pos(), absolute.state().getBlock(), 0);

            FluidState fluidState = absolute.state().getFluidState();
            if (!fluidState.isEmpty()) {
                level.scheduleTick(absolute.pos(), fluidState.getType(), 0);
            }
        }
        return absolute;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
