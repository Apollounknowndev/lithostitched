package dev.worldgen.lithostitched.worldgen.surface.condition.internal;

import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record TagFilledCondition(HolderSet<SurfaceRules.RuleSource> rules) implements SurfaceRules.ConditionSource {
    public static final KeyDispatchDataCodec<TagFilledCondition> CODEC = KeyDispatchDataCodec.of(
        RegistryCodecs.homogeneousList(LithostitchedRegistries.SURFACE_RULE).fieldOf("tag").xmap(TagFilledCondition::new, TagFilledCondition::rules)
    );

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.Condition apply(SurfaceRules.Context context) {
        return () -> this.rules.size() > 0;
    }
}
