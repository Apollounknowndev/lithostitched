package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.BiomeGenerationSettingsAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
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
public class AddFeaturesModifier extends Modifier {
    public static final Codec<AddFeaturesModifier> CODEC = RecordCodecBuilder.create((instance) -> addModifierFields(instance).and(instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddFeaturesModifier::biomes),
        PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesModifier::features),
        GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesModifier::step)
    )).apply(instance, AddFeaturesModifier::new));
    private final HolderSet<Biome> biomes;
    private final HolderSet<PlacedFeature> features;
    private final GenerationStep.Decoration step;
    public AddFeaturesModifier(ModifierPredicate predicate, HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) {
        super(predicate, ModifierPhase.ADD);
        this.biomes = biomes;
        this.features = features;
        this.step = step;
    }

    public HolderSet<Biome> biomes() {
        return this.biomes;
    }

    public HolderSet<PlacedFeature> features() {
        return this.features;
    }

    public GenerationStep.Decoration step() {
        return this.step;
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
        ((BiomeAccessor) (Object) biome).setGenerationSettings(BiomeGenerationSettingsAccessor.createGenerationSettings(
                ((BiomeGenerationSettingsAccessor) biome.getGenerationSettings()).getCarvers(),
                biomeFeatures
        ));
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
