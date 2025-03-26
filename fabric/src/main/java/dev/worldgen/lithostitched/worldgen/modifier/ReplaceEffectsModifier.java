package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import dev.worldgen.lithostitched.worldgen.modifier.util.BiomeEffects;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.*;
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
public record ReplaceEffectsModifier(HolderSet<Biome> biomes, BiomeEffects specialEffects) implements Modifier {
    public static final MapCodec<ReplaceEffectsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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

            if (entry.unwrapKey().isPresent()) {
                ResourceKey<Biome> key = entry.unwrapKey().get();
                Optional<RegistrationInfo> knownPackInfo = registry.registrationInfo(key);
                knownPackInfo.ifPresent(registrationInfo -> ((MappedRegistryAccessor<Biome>)registry).lithostitched$getRegistrationInfos().put(key, new RegistrationInfo(Optional.empty(), registrationInfo.lifecycle())));
            }
        }
    }

    @Override
    public void applyModifier() {}

    public void applyModifier(Biome biome) {
        BiomeAccessor accessor = (BiomeAccessor) (Object) biome;
        BiomeSpecialEffects effects = accessor.getSpecialEffects();
        Builder builder = new Builder();

        tryApplyRequired(BiomeEffects::fogColor, effects::getFogColor, builder::fogColor);
        tryApplyRequired(BiomeEffects::waterColor, effects::getWaterColor, builder::waterColor);
        tryApplyRequired(BiomeEffects::waterFogColor, effects::getWaterFogColor, builder::waterFogColor);
        tryApplyRequired(BiomeEffects::skyColor, effects::getSkyColor, builder::skyColor);

        tryApplyOptional(BiomeEffects::foliageColor, effects::getFoliageColorOverride, builder::foliageColorOverride);
        tryApplyOptional(BiomeEffects::dryFoliageColor, effects::getDryFoliageColorOverride, builder::dryFoliageColorOverride);
        tryApplyOptional(BiomeEffects::grassColor, effects::getGrassColorOverride, builder::grassColorOverride);
        tryApplyRequired(BiomeEffects::grassColorModifier, effects::getGrassColorModifier, builder::grassColorModifier);

        tryApplyOptional(BiomeEffects::ambientParticle, effects::getAmbientParticleSettings, builder::ambientParticle);
        tryApplyOptional(BiomeEffects::ambientSound, effects::getAmbientLoopSoundEvent, builder::ambientLoopSound);
        tryApplyOptional(BiomeEffects::moodSound, effects::getAmbientMoodSettings, builder::ambientMoodSound);
        tryApplyOptional(BiomeEffects::additionsSound, effects::getAmbientAdditionsSettings, builder::ambientAdditionsSound);
        tryApplyOptional(BiomeEffects::music, effects::getBackgroundMusic, builder::backgroundMusic);
        tryApplyRequired(BiomeEffects::musicVolume, effects::getBackgroundMusicVolume, builder::backgroundMusicVolume);

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
    public ModifierPhase getPhase() {
        return ModifierPhase.MODIFY;
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}

