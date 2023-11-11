package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.ForgeBiomeModifiers;

/**
 * A {@link Modifier} implementation that adds placed feature entries to {@link Biome} entries in a generation step.
 *
 * @author Apollo
 */
public class AddFeaturesModifier extends AbstractBiomeModifier {
    public static final Codec<AddFeaturesModifier> CODEC = RecordCodecBuilder.create((instance) -> addModifierFields(instance).and(instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddFeaturesModifier::biomes),
        PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesModifier::features),
        GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesModifier::step)
    )).apply(instance, AddFeaturesModifier::new));
    private final HolderSet<Biome> biomes;
    private final HolderSet<PlacedFeature> features;

    private final GenerationStep.Decoration step;

    public AddFeaturesModifier(ModifierPredicate predicate, HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) {
        super(predicate, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes, features, step));
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

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
