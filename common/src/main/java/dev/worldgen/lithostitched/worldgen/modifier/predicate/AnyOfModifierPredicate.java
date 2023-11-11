package dev.worldgen.lithostitched.worldgen.modifier.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record AnyOfModifierPredicate(List<ModifierPredicate> predicates) implements ModifierPredicate {
    public static final Codec<AnyOfModifierPredicate> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        ModifierPredicate.CODEC.listOf().fieldOf("predicates").forGetter(AnyOfModifierPredicate::predicates)
    ).apply(instance, AnyOfModifierPredicate::new));
    @Override
    public boolean test() {
        for (ModifierPredicate predicate : this.predicates()) {
            if (predicate.test()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Codec<? extends ModifierPredicate> codec() {
        return CODEC;
    }
}
