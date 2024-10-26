package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.TrueModifierPredicate;

/**
 * A {@link Modifier} implementation that does nothing.
 * <p>Useful for overriding worldgen modifiers from other datapacks/mods</p>
 *
 * @author Apollo
 */
public record NoOpModifier(ModifierPredicate predicate) implements Modifier {

    public static final Codec<NoOpModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).apply(instance, NoOpModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.NONE;
    }

    @Override
    public void applyModifier() {}

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
