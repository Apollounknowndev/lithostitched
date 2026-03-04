package dev.worldgen.lithostitched.worldgen.modifier;

import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import net.minecraft.core.RegistryAccess;

import java.util.Optional;

/**
 * Deprecated, please use {@link WorldgenModifier} instead.
 */
@Deprecated(forRemoval = true)
public interface Modifier extends WorldgenModifier {
    default Optional<LoadPredicate> predicate() {
        return Optional.empty();
    }
    
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
