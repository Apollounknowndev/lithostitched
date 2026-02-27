package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import net.minecraft.core.RegistryAccess;

/**
 * A {@link WorldgenModifier} implementation that does nothing.
 * <p>Useful for overriding worldgen modifiers from other datapacks/mods</p>
 *
 * @author Apollo
 */
public record NoOpModifier() implements WorldgenModifier {
    public static final MapCodec<NoOpModifier> CODEC = MapCodec.unit(NoOpModifier::new);

    @Override
    public void apply(RegistryAccess registries) {}

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
