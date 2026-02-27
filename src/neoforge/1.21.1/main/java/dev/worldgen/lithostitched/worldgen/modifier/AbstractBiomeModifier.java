package dev.worldgen.lithostitched.worldgen.modifier;

import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.common.world.BiomeModifier;

/**
 * An interface for Neoforge biome modifiers.
 *
 * @author Apollo
 */
public abstract class AbstractBiomeModifier implements WorldgenModifier {
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
    public int priority() {
        return 0;
    }

    @Override
    public void apply(RegistryAccess registries) {}
}
