package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor2;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import dev.worldgen.lithostitched.util.weighted.WeightedList;
import dev.worldgen.lithostitched.worldgen.modifier.util.BiomeEffects;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.BiomeSpecialEffects.Builder;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link Modifier} implementation that replaces the biome special effects of {@link Biome} entries.
 *
 * @author Apollo
 */
public record ReplaceEffectsModifier(int priority, HolderSet<Biome> biomes, BiomeEffects specialEffects) implements Modifier {
    public static final MapCodec<ReplaceEffectsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_DEFAULT.forGetter(ReplaceEffectsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsModifier::biomes),
        BiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsModifier::specialEffects)
    ).apply(instance, ReplaceEffectsModifier::new));

    @SuppressWarnings("unchecked")
    @Override
    public void applyModifier(RegistryAccess registryAccess) {
        List<Holder<Biome>> biomes = this.biomes().stream().toList();
        Registry<Biome> registry = registryAccess.lookupOrThrow(Registries.BIOME);
        for (Holder<Biome> entry : biomes.stream().toList()) {
            this.applyModifier(entry.value());
            Modifier.resetRegistrationInfo(registry, entry);
        }
    }

    @Override
    public void applyModifier() {}

    public void applyModifier(Biome biome) {
        BiomeAccessor accessor = (BiomeAccessor) (Object) biome;
        BiomeAccessor2 accessor2 = (BiomeAccessor2) (Object) biome;

        BiomeSpecialEffects effects = accessor.getSpecialEffects();
        Builder effectBuilder = new Builder();
        applyRequiredEffect(BiomeEffects::waterColor, effects::waterColor, effectBuilder::waterColor);
        applyOptionalEffect(BiomeEffects::foliageColor, effects::foliageColorOverride, effectBuilder::foliageColorOverride);
        applyOptionalEffect(BiomeEffects::dryFoliageColor, effects::dryFoliageColorOverride, effectBuilder::dryFoliageColorOverride);
        applyOptionalEffect(BiomeEffects::grassColor, effects::grassColorOverride, effectBuilder::grassColorOverride);
        applyRequiredEffect(BiomeEffects::grassColorModifier, effects::grassColorModifier, effectBuilder::grassColorModifier);
        accessor.setSpecialEffects(effectBuilder.build());

        EnvironmentAttributeMap attributes = biome.getAttributes();
        var attributeBuilder = EnvironmentAttributeMap.builder();
        attributeBuilder.putAll(attributes);
        applyAttribute(attributeBuilder, BiomeEffects::fogColor, EnvironmentAttributes.FOG_COLOR);
        applyAttribute(attributeBuilder, BiomeEffects::waterFogColor, EnvironmentAttributes.WATER_FOG_COLOR);
        applyAttribute(attributeBuilder, BiomeEffects::skyColor, EnvironmentAttributes.SKY_COLOR);
        applyAttribute(attributeBuilder, e -> Optional.of(e.ambientParticle().map(List::of).orElse(List.of())), EnvironmentAttributes.AMBIENT_PARTICLES);
        applyAttribute(attributeBuilder, e -> Optional.of(new AmbientSounds(e.ambientSound(), e.moodSound(), e.additionsSound().map(List::of).orElse(List.of()))), EnvironmentAttributes.AMBIENT_SOUNDS);
        applyAttribute(attributeBuilder, e -> Optional.of(new BackgroundMusic(e.music(), Optional.empty(), Optional.empty())), EnvironmentAttributes.BACKGROUND_MUSIC);
        applyAttribute(attributeBuilder, BiomeEffects::musicVolume, EnvironmentAttributes.MUSIC_VOLUME);
        accessor2.setAttributes(attributeBuilder.build());
    }

    private <T> void applyRequiredEffect(Function<BiomeEffects, Optional<T>> getter, Supplier<T> fallback, Consumer<T> applier) {
        applier.accept(getter.apply(this.specialEffects).orElse(fallback.get()));
    }

    private <T> void applyOptionalEffect(Function<BiomeEffects, Optional<T>> getter, Supplier<Optional<T>> fallback, Consumer<T> applier) {
        T value = getter.apply(this.specialEffects).orElse(fallback.get().orElse(null));
        if (value != null) {
            applier.accept(value);
        }
    }

    private <T> void applyAttribute(EnvironmentAttributeMap.Builder builder, Function<BiomeEffects, Optional<T>> getter, EnvironmentAttribute<T> attribute) {
        Optional<T> value = getter.apply(this.specialEffects);
        value.ifPresent(object -> builder.set(attribute, object));
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}

