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

public class RedirectFeatureModifier extends Modifier {
    public static final Codec<RedirectFeatureModifier> CODEC = RecordCodecBuilder.create(instance -> addModifierFields(instance).and(instance.group(
        ResourceLocation.CODEC.fieldOf("placed_feature").forGetter(RedirectFeatureModifier::rawPlacedFeatureLocation),
        ConfiguredFeature.CODEC.fieldOf("redirect_to").forGetter(RedirectFeatureModifier::redirectTo),
        RegistryOps.retrieveGetter(Registries.PLACED_FEATURE)
    )).apply(instance, RedirectFeatureModifier::new));
    private final ResourceKey<PlacedFeature> EMPTY_PLACED_FEATURE = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(LithostitchedCommon.MOD_ID, "empty"));
    private final ResourceLocation rawPlacedFeatureLocation;

    private final Holder<PlacedFeature> placedFeature;
    private final Holder<ConfiguredFeature<?, ?>> redirectTo;

    public RedirectFeatureModifier(ModifierPredicate predicate, ResourceLocation rawPlacedFeatureLocation, Holder<ConfiguredFeature<?, ?>> redirectTo, HolderGetter<PlacedFeature> getter) {
        super(predicate, ModifierPhase.MODIFY);
        this.rawPlacedFeatureLocation = rawPlacedFeatureLocation;
        var placedFeatureEntry = getter.get(predicate.test() ? ResourceKey.create(Registries.PLACED_FEATURE, rawPlacedFeatureLocation) : EMPTY_PLACED_FEATURE);
        this.placedFeature = placedFeatureEntry.get();
        this.redirectTo = redirectTo;
    }
    public ResourceLocation rawPlacedFeatureLocation() {
        return this.rawPlacedFeatureLocation;
    }

    public Holder<PlacedFeature> placedFeature() {
        return this.placedFeature;
    }

    public Holder<ConfiguredFeature<?, ?>> redirectTo() {
        return this.redirectTo;
    }

    @Override
    public void applyModifier() {
        if (this.placedFeature.is(EMPTY_PLACED_FEATURE)) return;
        ((PlacedFeatureAccessor)(Object)this.placedFeature().value()).setFeature(this.redirectTo());
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
