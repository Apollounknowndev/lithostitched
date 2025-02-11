package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.util.BiomeEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
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
public record ReplaceEffectsModifier(ModifierPredicate predicate, HolderSet<Biome> biomes, BiomeEffects specialEffects) implements Modifier {

    public static final Codec<ReplaceEffectsModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsModifier::biomes),
        BiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsModifier::specialEffects)
    )).apply(instance, ReplaceEffectsModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.MODIFY;
    }

    public void applyModifier(Biome biome) {
        BiomeAccessor accessor = (BiomeAccessor) (Object) biome;
        BiomeSpecialEffects effects = accessor.getSpecialEffects();
        Builder builder = new Builder();

        tryApplyRequired(BiomeEffects::fogColor, effects::getFogColor, builder::fogColor);
        tryApplyRequired(BiomeEffects::waterColor, effects::getWaterColor, builder::waterColor);
        tryApplyRequired(BiomeEffects::waterFogColor, effects::getWaterFogColor, builder::waterFogColor);
        tryApplyRequired(BiomeEffects::skyColor, effects::getSkyColor, builder::skyColor);

        tryApplyOptional(BiomeEffects::foliageColor, effects::getFoliageColorOverride, builder::foliageColorOverride);
        tryApplyOptional(BiomeEffects::grassColor, effects::getGrassColorOverride, builder::grassColorOverride);
        tryApplyRequired(BiomeEffects::grassColorModifier, effects::getGrassColorModifier, builder::grassColorModifier);

        tryApplyOptional(BiomeEffects::ambientParticle, effects::getAmbientParticleSettings, builder::ambientParticle);
        tryApplyOptional(BiomeEffects::ambientSound, effects::getAmbientLoopSoundEvent, builder::ambientLoopSound);
        tryApplyOptional(BiomeEffects::moodSound, effects::getAmbientMoodSettings, builder::ambientMoodSound);
        tryApplyOptional(BiomeEffects::additionsSound, effects::getAmbientAdditionsSettings, builder::ambientAdditionsSound);
        tryApplyOptional(BiomeEffects::music, effects::getBackgroundMusic, builder::backgroundMusic);

        accessor.setSpecialEffects(builder.build());
    }

    private <T> void tryApplyRequired(Function<BiomeEffects, Optional<T>> getter, Supplier<T> fallback, Consumer<T> applier) {
        applier.accept(getter.apply(this.specialEffects).orElse(fallback.get()));
    }

    private <T> void tryApplyOptional(Function<BiomeEffects, Optional<T>> getter, Supplier<Optional<T>> fallback, Consumer<T> applier) {
        T value = getter.apply(this.specialEffects).orElse(fallback.get().orElse(null));
        if (value != null) {
            applier.accept(value);
        }
    }

    @Override
    public void applyModifier() {
        List<Holder<Biome>> biomes = this.biomes().stream().toList();
        for (Holder<Biome> entry : biomes.stream().toList()) {
            this.applyModifier(entry.value());
        }
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}

