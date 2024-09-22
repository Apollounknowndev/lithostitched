package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.ExtraCodecs;

public interface PriorityBasedModifier extends Modifier {
    MapCodec<Integer> PRIORITY_CODEC = ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("priority", 1000);

    int getPriority();
}
