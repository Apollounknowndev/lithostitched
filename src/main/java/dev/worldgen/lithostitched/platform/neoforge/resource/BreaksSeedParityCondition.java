//? if neoforge {
package dev.worldgen.lithostitched.platform.neoforge.resource;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import net.neoforged.neoforge.common.conditions.ICondition;

public record BreaksSeedParityCondition() implements ICondition {
    public static final BreaksSeedParityCondition INSTANCE = new BreaksSeedParityCondition();
    public static MapCodec<BreaksSeedParityCondition> CODEC = MapCodec.unit(INSTANCE);

    public boolean test(IContext context) {
        return Lithostitched.breaksSeedParity();
    }

    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
//? }