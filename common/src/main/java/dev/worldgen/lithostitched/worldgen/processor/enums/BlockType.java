package dev.worldgen.lithostitched.worldgen.processor.enums;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.worldgen.processor.condition.ProcessorCondition;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public enum BlockType implements StringRepresentable {
    INPUT("input", data -> data.relative().state()),
    LOCATION("location", data -> data.absolute().state());

    public static final Codec<BlockType> CODEC = StringRepresentable.fromEnum(BlockType::values);
    private final String name;
    private final Function<ProcessorCondition.Data, BlockState> state;

    BlockType(String name, Function<ProcessorCondition.Data, BlockState> state) {
        this.name = name;
        this.state = state;
    }

    public BlockState state(ProcessorCondition.Data data) {
        return this.state.apply(data);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
