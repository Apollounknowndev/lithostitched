package dev.worldgen.lithostitched.worldgen.modifier;

import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import net.minecraft.core.RegistryAccess;

/**
 * Deprecated, please use {@link WorldgenModifier} instead.
 */
@Deprecated(forRemoval = true)
public interface Modifier extends WorldgenModifier {
    /**
     * All worldgen modifiers should only override {@code apply}, not either {@code applyModifier}.
     */
    default void apply(RegistryAccess registries) {
        this.applyModifier(registries);
    }
    
    @Deprecated
    default void applyModifier(RegistryAccess registryAccess) {
        this.applyModifier();
    }
    
    @Deprecated
    void applyModifier();
}
