package dev.worldgen.lithostitched.worldgen.modifier;

import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.neoforged.neoforge.common.world.BiomeModifier;

/**
 * An interface for Neoforge biome modifiers.
 *
 * @author Apollo
 */
public abstract class AbstractBiomeModifier extends Modifier {
    private final BiomeModifier neoforgeBiomeModifier;
    protected AbstractBiomeModifier(ModifierPredicate predicate, BiomeModifier neoforgeBiomeModifier) {
        super(predicate, ModifierPhase.NONE);
        this.neoforgeBiomeModifier = neoforgeBiomeModifier;
    }

    /**
     * Gets a Forge biome modifier equivalent to inject into the Forge biome modifier system.
     */
    public BiomeModifier neoforgeBiomeModifier() {
        return this.neoforgeBiomeModifier;
    }

    @Override
    public void applyModifier() {}
}
