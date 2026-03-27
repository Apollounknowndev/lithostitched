package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.LithostitchedPlatform;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.msrandom.multiplatform.annotations.Expect;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record ReplaceEffectsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, BiomeEffects effects) implements WorldgenModifier {
    public static final MapCodec<ReplaceEffectsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(ReplaceEffectsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsModifier::biomes),
        BiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsModifier::effects)
    ).apply(instance, ReplaceEffectsModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        if (!LithostitchedPlatform.isFabric()) return;
        
        Registry<Biome> registry = Lithostitched.registry(registries, Registries.BIOME);
        for (Holder<Biome> entry : this.biomes()) {
            this.applyModifier(entry.value());
            WorldgenModifier.resetRegistrationInfo(registry, entry);
        }
    }

    @Expect
    public void applyModifier(Biome biome);
    
    public <T> void applyRequiredEffect(Function<BiomeEffects, Optional<T>> getter, Supplier<T> fallback, Consumer<T> applier) {
        applier.accept(getter.apply(this.effects).orElse(fallback.get()));
    }
    
    public <T> void applyOptionalEffect(Function<BiomeEffects, Optional<T>> getter, Supplier<Optional<T>> fallback, Consumer<T> applier) {
        T value = getter.apply(this.effects).orElse(fallback.get().orElse(null));
        if (value != null) {
            applier.accept(value);
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}

