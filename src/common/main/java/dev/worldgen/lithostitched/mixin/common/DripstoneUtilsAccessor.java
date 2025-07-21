package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.levelgen.feature.DripstoneUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DripstoneUtils.class)
public interface DripstoneUtilsAccessor {
    @Invoker("createPointedDripstone")
    static BlockState createPointed(Direction direction, DripstoneThickness thickness) {
        throw new AssertionError("Implemented via mixin");
    };
}
