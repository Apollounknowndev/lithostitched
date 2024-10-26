package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.mixin.common.PlacedFeatureAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record RedirectFeatureModifier(ModifierPredicate predicate, Holder<PlacedFeature> placedFeature, Holder<ConfiguredFeature<?, ?>> redirectTo) implements Modifier {
    public static final Codec<RedirectFeatureModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        PlacedFeature.CODEC.fieldOf("placed_feature").forGetter(RedirectFeatureModifier::placedFeature),
        ConfiguredFeature.CODEC.fieldOf("redirect_to").forGetter(RedirectFeatureModifier::redirectTo)
    )).apply(instance, RedirectFeatureModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.MODIFY;
    }

    @Override
    public void applyModifier() {
        ((PlacedFeatureAccessor)(Object)this.placedFeature().value()).setFeature(this.redirectTo());
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
