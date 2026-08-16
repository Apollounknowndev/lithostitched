package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

//? if neoforge {
import net.neoforged.neoforge.common.world.BiomeModifier;
import dev.worldgen.lithostitched.platform.neoforge.worldgen.LithostitchedNeoforgeBiomeModifiers;
//? }

public record ReplaceEffectsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, BiomeEffects effects) implements WorldgenModifier /*? if neoforge{*/, NeoforgeModifierHolder /*?}*/ {
    public static final MapCodec<ReplaceEffectsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(ReplaceEffectsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsModifier::biomes),
        BiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsModifier::effects)
    ).apply(instance, ReplaceEffectsModifier::new));
    
    //? if neoforge {
    @Override
    public BiomeModifier createNeoforgeModifier() {
        return new LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier(biomes, effects);
    }
    //? }

    @Override
    public void apply(RegistryAccess registries) {
        //? if neoforge
        if (true) return;
        
        Registry<Biome> registry = Lithostitched.registry(registries, Registries.BIOME);
        for (Holder<Biome> entry : this.biomes()) {
            this.applyModifier(entry.value());
            WorldgenModifier.resetRegistrationInfo(registry, entry);
        }
    }
    
    public void applyModifier(Biome biome) {
        BiomeAccessor accessor = (BiomeAccessor) (Object) biome;
        BiomeSpecialEffects originalEffects = accessor.getSpecialEffects();
        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder();
        
        this.applyRequiredEffect(BiomeEffects::fogColor, originalEffects::getFogColor, builder::fogColor);
        this.applyRequiredEffect(BiomeEffects::waterColor, originalEffects::getWaterColor, builder::waterColor);
        this.applyRequiredEffect(BiomeEffects::waterFogColor, originalEffects::getWaterFogColor, builder::waterFogColor);
        this.applyRequiredEffect(BiomeEffects::skyColor, originalEffects::getSkyColor, builder::skyColor);
        
        this.applyOptionalEffect(BiomeEffects::foliageColor, originalEffects::getFoliageColorOverride, builder::foliageColorOverride);
        this.applyOptionalEffect(BiomeEffects::grassColor, originalEffects::getGrassColorOverride, builder::grassColorOverride);
        this.applyRequiredEffect(BiomeEffects::grassColorModifier, originalEffects::getGrassColorModifier, builder::grassColorModifier);
        
        this.applyOptionalEffect(BiomeEffects::ambientParticle, originalEffects::getAmbientParticleSettings, builder::ambientParticle);
        this.applyOptionalEffect(BiomeEffects::ambientSound, originalEffects::getAmbientLoopSoundEvent, builder::ambientLoopSound);
        this.applyOptionalEffect(BiomeEffects::moodSound, originalEffects::getAmbientMoodSettings, builder::ambientMoodSound);
        this.applyOptionalEffect(BiomeEffects::additionsSound, originalEffects::getAmbientAdditionsSettings, builder::ambientAdditionsSound);
        this.applyOptionalEffect(BiomeEffects::music, originalEffects::getBackgroundMusic, builder::backgroundMusic);
        
        accessor.setSpecialEffects(builder.build());
    }
    
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

