package dev.worldgen.lithostitched.resource;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.config.ConfigHandler;
import net.neoforged.neoforge.common.conditions.ICondition;

public record BreaksSeedParityCondition() implements ICondition {
    public static final BreaksSeedParityCondition INSTANCE = new BreaksSeedParityCondition();
    public static MapCodec<BreaksSeedParityCondition> CODEC = MapCodec.unit(INSTANCE);

    public boolean test(ICondition.IContext context) {
        return ConfigHandler.getConfig().breaksSeedParity();
    }

    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
