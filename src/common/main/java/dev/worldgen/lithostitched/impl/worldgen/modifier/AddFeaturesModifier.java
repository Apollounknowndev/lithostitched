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

public record AddFeaturesModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements WorldgenModifier {
    public static final MapCodec<AddFeaturesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PREDICATE_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(AddFeaturesModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddFeaturesModifier::biomes),
        PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesModifier::features),
        GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesModifier::step)
    ).apply(instance, AddFeaturesModifier::new));
    
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
        if (biomeFeatures.size() <= index) {
            for (int i = biomeFeatures.size(); i <= index; i++) {
                biomeFeatures.add(HolderSet.direct());
            }
        }
        List<Holder<PlacedFeature>> stepFeatures = new ArrayList<>(biomeFeatures.get(index).stream().toList());
        for(Holder<PlacedFeature> feature : this.features()) {
            stepFeatures.add(feature);
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
