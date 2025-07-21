package dev.worldgen.lithostitched.worldgen.modifier;

import net.neoforged.neoforge.common.world.BiomeModifier;

/**
 * An interface for Neoforge biome modifiers.
 *
 * @author Apollo
 */
public abstract class AbstractBiomeModifier implements Modifier {
    private final BiomeModifier neoforgeBiomeModifier;
    protected AbstractBiomeModifier(BiomeModifier neoforgeBiomeModifier) {
        this.neoforgeBiomeModifier = neoforgeBiomeModifier;
    }

    /**
     * Gets a Forge biome modifier equivalent to inject into the Forge biome modifier system.
     */
    public BiomeModifier neoforgeBiomeModifier() {
        return this.neoforgeBiomeModifier;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.NONE;
    }

    @Override
    public void applyModifier() {}
}
