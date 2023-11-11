package dev.worldgen.lithostitched.worldgen.modifier;

import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraftforge.common.world.BiomeModifier;

/**
 * An interface for Forge biome modifiers.
 *
 * @author Apollo
 */
public abstract class AbstractBiomeModifier extends Modifier {
    private final BiomeModifier forgeBiomeModifier;
    protected AbstractBiomeModifier(ModifierPredicate predicate, BiomeModifier forgeBiomeModifier) {
        super(predicate, ModifierPhase.NONE);
        this.forgeBiomeModifier = forgeBiomeModifier;
    }

    /**
     * Gets a Forge biome modifier equivalent to inject into the Forge biome modifier system.
     */
    public BiomeModifier forgeBiomeModifier() {
        return this.forgeBiomeModifier;
    }

    @Override
    public void applyModifier() {}
}
