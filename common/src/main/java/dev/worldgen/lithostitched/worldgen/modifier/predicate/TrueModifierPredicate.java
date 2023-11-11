package dev.worldgen.lithostitched.worldgen.modifier.predicate;

import com.mojang.serialization.Codec;

public class TrueModifierPredicate implements ModifierPredicate {
    public static final TrueModifierPredicate INSTANCE = new TrueModifierPredicate();
    public static final Codec<TrueModifierPredicate> CODEC = Codec.unit(INSTANCE);
    @Override
    public boolean test() {
        return true;
    }

    @Override
    public Codec<? extends ModifierPredicate> codec() {
        return CODEC;
    }
}
