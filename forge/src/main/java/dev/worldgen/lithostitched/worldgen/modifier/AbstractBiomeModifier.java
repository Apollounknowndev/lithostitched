package dev.worldgen.lithostitched.worldgen.modifier;

import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraftforge.common.world.BiomeModifier;

/**
 * An interface for Forge biome modifiers.
 *
 * @author Apollo
 */
public abstract class AbstractBiomeModifier implements Modifier {
    private final ModifierPredicate predicate;
    private final BiomeModifier forgeBiomeModifier;
    protected AbstractBiomeModifier(ModifierPredicate predicate, BiomeModifier forgeBiomeModifier) {
        this.predicate = predicate;
        this.forgeBiomeModifier = forgeBiomeModifier;
    }

    /**
     * Gets a Forge biome modifier equivalent to inject into the Forge biome modifier system.
     */
    public BiomeModifier forgeBiomeModifier() {
        return this.forgeBiomeModifier;
    }

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
}
