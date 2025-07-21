package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;

/**
 * A {@link Modifier} implementation that does nothing.
 * <p>Useful for overriding worldgen modifiers from other datapacks/mods</p>
 *
 * @author Apollo
 */
public record NoOpModifier() implements Modifier {

    public static final MapCodec<NoOpModifier> CODEC = MapCodec.unit(NoOpModifier::new);

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.NONE;
    }

    @Override
    public void applyModifier() {}

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
