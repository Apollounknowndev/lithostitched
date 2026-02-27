package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.BiomeGenerationSettingsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Modifier} implementation that adds placed feature entries to {@link Biome} entries in a generation step.
 *
 * @author Apollo
 */
public record AddFeaturesModifier(int priority, HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements Modifier {
    public static final MapCodec<AddFeaturesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_DEFAULT.forGetter(AddFeaturesModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddFeaturesModifier::biomes),
        PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesModifier::features),
        GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesModifier::step)
    ).apply(instance, AddFeaturesModifier::new));

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
        ((BiomeAccessor) (Object) biome).setGenerationSettings(BiomeGenerationSettingsAccessor.createGenerationSettings(
                ((BiomeGenerationSettingsAccessor) biome.getGenerationSettings()).getCarvers(),
                biomeFeatures
        ));
    }

    @Override
    public boolean shouldRecompileSortedFeatures() {
        return true;
    }

    @Override
    public void applyModifier() {
        List<Holder<Biome>> biomes = this.biomes().stream().toList();
        for (Holder<Biome> entry : biomes.stream().toList()) {
            this.applyModifier(entry.value());
        }
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
