package dev.worldgen.lithostitched.impl.worldgen.surface.condition.internal;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record TagFilledCondition(HolderSet<SurfaceRules.RuleSource> rules) implements SurfaceRules.ConditionSource {
    public static final MapCodec<TagFilledCondition> CODEC = RegistryCodecs.holderSet(LithostitchedRegistries.SURFACE_RULE).fieldOf("tag").xmap(TagFilledCondition::new, TagFilledCondition::rules);

    @Override
    public MapCodec<? extends SurfaceRules.ConditionSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.Condition apply(SurfaceRules.Context context) {
        return () -> this.rules.size() > 0;
    }
}
