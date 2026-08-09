package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.worldgen.feature.CompositeFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.Optional;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

public record StackFeatureModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Feature> baseFeatures, Holder<PlacedFeature> stackedFeature, CompositeFeature.Type placementType) implements WorldgenModifier {
    public static final MapCodec<StackFeatureModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(StackFeatureModifier::priority),
        registrySet(Registries.FEATURE, "base_features").forGetter(StackFeatureModifier::baseFeatures),
        PlacedFeature.CODEC.fieldOf("stacked_feature").forGetter(StackFeatureModifier::stackedFeature),
        CompositeFeature.Type.CODEC.fieldOf("placement_type").orElse(CompositeFeature.Type.CANCEL_ON_FAILURE).forGetter(StackFeatureModifier::placementType)
    ).apply(instance, StackFeatureModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        this.baseFeatures.stream().forEach(this::applyModifier);
    }

    private void applyModifier(Holder<Feature> feature) {
        if (feature instanceof Holder.Reference<Feature>) {
            var accessor = ((HolderReferenceAccessor<Feature>)feature);

            accessor.setValue(new CompositeFeature(
                HolderSet.direct(
                    Holder.direct(new PlacedFeature(Holder.direct(feature.value()), List.of())),
                    this.stackedFeature
                ),
                this.placementType
            ));
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
