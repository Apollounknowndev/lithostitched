package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.worldgen.feature.CompositeFeature;
import dev.worldgen.lithostitched.worldgen.feature.config.CompositeConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

public record StackFeatureModifier(int priority, HolderSet<ConfiguredFeature<?, ?>> baseFeatures, Holder<PlacedFeature> stackedFeature, CompositeConfig.Type placementType) implements WorldgenModifier {
    public static final MapCodec<StackFeatureModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_DEFAULT.forGetter(StackFeatureModifier::priority),
        registrySet(Registries.CONFIGURED_FEATURE, "base_features").forGetter(StackFeatureModifier::baseFeatures),
        PlacedFeature.CODEC.fieldOf("stacked_feature").forGetter(StackFeatureModifier::stackedFeature),
        CompositeConfig.Type.CODEC.fieldOf("placement_type").orElse(CompositeConfig.Type.CANCEL_ON_FAILURE).forGetter(StackFeatureModifier::placementType)
    ).apply(instance, StackFeatureModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        this.baseFeatures.stream().forEach(this::applyModifier);
    }

    private void applyModifier(Holder<ConfiguredFeature<?,?>> feature) {
        if (feature instanceof Holder.Reference<ConfiguredFeature<?,?>>) {
            var accessor = ((HolderReferenceAccessor<ConfiguredFeature<?, ?>>)feature);

            accessor.setValue(new ConfiguredFeature<>(CompositeFeature.FEATURE, new CompositeConfig(
                HolderSet.direct(
                    Holder.direct(new PlacedFeature(Holder.direct(feature.value()), List.of())),
                    this.stackedFeature
                ),
                this.placementType
            )));
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
