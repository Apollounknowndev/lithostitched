package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.util.ExtraCodecs;

public abstract class PriorityBasedModifier extends Modifier {
    public static final MapCodec<Integer> PRIORITY_CODEC = ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("priority", 1000);

    protected PriorityBasedModifier(ModifierPredicate predicate, ModifierPhase phase) {
        super(predicate, phase);
    }

    public abstract int getPriority();
}
