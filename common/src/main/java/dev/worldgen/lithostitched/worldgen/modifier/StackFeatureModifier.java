package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.worldgen.feature.CompositeFeature;
import dev.worldgen.lithostitched.worldgen.feature.config.CompositeConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public record StackFeatureModifier(Holder<ConfiguredFeature<?, ?>> baseFeature, Holder<PlacedFeature> stackedFeature) implements Modifier {
    public static final MapCodec<StackFeatureModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ConfiguredFeature.CODEC.fieldOf("base_feature").forGetter(StackFeatureModifier::baseFeature),
        PlacedFeature.CODEC.fieldOf("stacked_feature").forGetter(StackFeatureModifier::stackedFeature)
    ).apply(instance, StackFeatureModifier::new));

    @Override
    public void applyModifier() {
        if (this.baseFeature instanceof Holder.Reference<ConfiguredFeature<?,?>>) {
            var accessor = ((HolderReferenceAccessor<ConfiguredFeature<?, ?>>)this.baseFeature);

            accessor.setValue(new ConfiguredFeature<>(CompositeFeature.FEATURE, new CompositeConfig(
                HolderSet.direct(
                    Holder.direct(new PlacedFeature(Holder.direct(this.baseFeature.value()), List.of())),
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
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
