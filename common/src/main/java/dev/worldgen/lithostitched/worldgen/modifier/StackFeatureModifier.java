package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.worldgen.feature.CompositeFeature;
import dev.worldgen.lithostitched.worldgen.feature.config.CompositeConfig;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

public record StackFeatureModifier(ModifierPredicate predicate, HolderSet<ConfiguredFeature<?, ?>> baseFeatures, Holder<PlacedFeature> stackedFeature) implements Modifier {
    public static final Codec<StackFeatureModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        registrySet(Registries.CONFIGURED_FEATURE, "base_feature").forGetter(StackFeatureModifier::baseFeatures),
        PlacedFeature.CODEC.fieldOf("stacked_feature").forGetter(StackFeatureModifier::stackedFeature)
    )).apply(instance, StackFeatureModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }

    @Override
    public void applyModifier() {
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
                    CompositeConfig.Type.CANCEL_ON_FAILURE
            )));
        }
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.MODIFY;
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
