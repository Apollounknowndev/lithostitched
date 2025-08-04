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
 * A {@link Modifier} implementation that removes placed feature entries to {@link Biome} entries in a generation step.
 *
 * @author Apollo
 */
public record RemoveFeaturesModifier(int priority, HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements Modifier {
    public static final MapCodec<RemoveFeaturesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_REMOVE.forGetter(RemoveFeaturesModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveFeaturesModifier::biomes),
        PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(RemoveFeaturesModifier::features),
        GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(RemoveFeaturesModifier::step)
    ).apply(instance, RemoveFeaturesModifier::new));

    public void applyModifier(Biome biome) {
        int index = this.step().ordinal();
        List<HolderSet<PlacedFeature>> biomeFeatures = new ArrayList<>(biome.getGenerationSettings().features());
        List<Holder<PlacedFeature>> stepFeatures = new ArrayList<>(biomeFeatures.get(index).stream().toList());
        for(Holder<PlacedFeature> feature : this.features()) {
            stepFeatures.remove(feature);
        }
        biomeFeatures.set(index, HolderSet.direct(stepFeatures));
        ((BiomeAccessor) (Object) biome).setGenerationSettings(BiomeGenerationSettingsAccessor.createGenerationSettings(
                ((BiomeGenerationSettingsAccessor) biome.getGenerationSettings()).getCarvers(),
                biomeFeatures
        ));
    }

    @Override
    public boolean internal$modifiesFabricFeatures() {
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
