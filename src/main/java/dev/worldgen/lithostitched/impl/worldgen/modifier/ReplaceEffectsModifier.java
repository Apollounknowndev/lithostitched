package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

//? if neoforge {
/*import net.neoforged.neoforge.common.world.BiomeModifier;
import dev.worldgen.lithostitched.platform.neoforge.worldgen.LithostitchedNeoforgeBiomeModifiers;
*///? }

public record ReplaceEffectsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, BiomeEffects effects) implements WorldgenModifier /*? if neoforge{*//*, NeoforgeModifierHolder *//*?}*/ {
    public static final MapCodec<ReplaceEffectsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(ReplaceEffectsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsModifier::biomes),
        BiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsModifier::effects)
    ).apply(instance, ReplaceEffectsModifier::new));
    
    //? if neoforge {
    /*@Override
    public BiomeModifier createNeoforgeModifier() {
        return new LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier(biomes, effects);
    }
    *///? }

    @Override
    public void apply(RegistryAccess registries) {
        //? if neoforge
        //if (true) return;
        
        Registry<Biome> registry = Lithostitched.registry(registries, Registries.BIOME);
        for (Holder<Biome> entry : this.biomes()) {
            this.applyModifier(entry.value());
            WorldgenModifier.resetRegistrationInfo(registry, entry);
        }
    }
    
    public void applyModifier(Biome biome) {
        BiomeAccessor accessor = (BiomeAccessor) (Object) biome;
        BiomeSpecialEffects originalEffects = accessor.getSpecialEffects();
        BiomeEffects modifierEffects = this.effects();
        
        BiomeSpecialEffects.Builder effectBuilder = new BiomeSpecialEffects.Builder();
        this.applyRequiredEffect(BiomeEffects::waterColor, originalEffects::waterColor, effectBuilder::waterColor);
        this.applyOptionalEffect(BiomeEffects::foliageColor, originalEffects::foliageColorOverride, effectBuilder::foliageColorOverride);
        this.applyOptionalEffect(BiomeEffects::dryFoliageColor, originalEffects::dryFoliageColorOverride, effectBuilder::dryFoliageColorOverride);
        this.applyOptionalEffect(BiomeEffects::grassColor, originalEffects::grassColorOverride, effectBuilder::grassColorOverride);
        this.applyRequiredEffect(BiomeEffects::grassColorModifier, originalEffects::grassColorModifier, effectBuilder::grassColorModifier);
        accessor.setSpecialEffects(effectBuilder.build());
        
        EnvironmentAttributeMap attributes = biome.getAttributes();
        var attributeBuilder = EnvironmentAttributeMap.builder();
        attributeBuilder.putAll(attributes);
        applyAttribute(modifierEffects, attributeBuilder, BiomeEffects::fogColor, EnvironmentAttributes.FOG_COLOR);
        applyAttribute(modifierEffects, attributeBuilder, BiomeEffects::waterFogColor, EnvironmentAttributes.WATER_FOG_COLOR);
        applyAttribute(modifierEffects, attributeBuilder, BiomeEffects::skyColor, EnvironmentAttributes.SKY_COLOR);
        applyAttribute(modifierEffects, attributeBuilder, e -> Optional.of(e.ambientParticle().map(List::of).orElse(List.of())), EnvironmentAttributes.AMBIENT_PARTICLES);
        applyAttribute(modifierEffects, attributeBuilder, e -> Optional.of(new AmbientSounds(e.ambientSound(), e.moodSound(), e.additionsSound().map(List::of).orElse(List.of()))), EnvironmentAttributes.AMBIENT_SOUNDS);
        applyAttribute(modifierEffects, attributeBuilder, e -> Optional.of(new BackgroundMusic(e.music(), Optional.empty(), Optional.empty())), EnvironmentAttributes.BACKGROUND_MUSIC);
        applyAttribute(modifierEffects, attributeBuilder, BiomeEffects::musicVolume, EnvironmentAttributes.MUSIC_VOLUME);
        accessor.setAttributes(attributeBuilder.build());
    }
    
    public static <T> void applyAttribute(BiomeEffects effects, EnvironmentAttributeMap.Builder builder, Function<BiomeEffects, Optional<T>> getter, EnvironmentAttribute<T> attribute) {
        Optional<T> value = getter.apply(effects);
        value.ifPresent(object -> builder.set(attribute, object));
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

