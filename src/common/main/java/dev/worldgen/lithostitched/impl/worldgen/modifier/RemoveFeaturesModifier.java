package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.LithostitchedPlatform;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record RemoveFeaturesModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements WorldgenModifier {
    public static final MapCodec<RemoveFeaturesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PREDICATE_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_REMOVE_CODEC.forGetter(RemoveFeaturesModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveFeaturesModifier::biomes),
        PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(RemoveFeaturesModifier::features),
        GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(RemoveFeaturesModifier::step)
    ).apply(instance, RemoveFeaturesModifier::new));
    
    @Override
    public void apply(RegistryAccess registries) {
        if (!LithostitchedPlatform.isFabric()) return;
        
        for (Holder<Biome> entry : this.biomes()) {
            this.applyModifier(entry.value());
        }
    }
    
    public void applyModifier(Biome biome) {
        int index = this.step().ordinal();
        List<HolderSet<PlacedFeature>> biomeFeatures = new ArrayList<>(biome.getGenerationSettings().features());
        List<Holder<PlacedFeature>> stepFeatures = new ArrayList<>(biomeFeatures.get(index).stream().toList());
        for(Holder<PlacedFeature> feature : this.features()) {
            stepFeatures.remove(feature);
        }
        biomeFeatures.set(index, HolderSet.direct(stepFeatures));
        LithostitchedPlatform.rebuildSettings(biome, biomeFeatures);
    }

    @Override
    public boolean shouldRecompileSortedFeatures() {
        return true;
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
