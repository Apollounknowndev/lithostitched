package dev.worldgen.lithostitched.worldgen.surface.rule;

import com.google.common.collect.ImmutableList;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record ReferenceRule(HolderSet<SurfaceRules.RuleSource> rules) implements SurfaceRules.RuleSource {
    public static final KeyDispatchDataCodec<ReferenceRule> CODEC = KeyDispatchDataCodec.of(
        RegistryCodecs.homogeneousList(LithostitchedRegistries.SURFACE_RULE).fieldOf("rules").xmap(ReferenceRule::new, ReferenceRule::rules)
    );

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        if (this.rules.size() == 0) return (x, y, z) -> null;
        if (this.rules.size() == 1) return this.rules.get(0).value().apply(context);

        ImmutableList.Builder<SurfaceRules.SurfaceRule> builder = ImmutableList.builder();
        for (SurfaceRules.RuleSource ruleSource : this.rules.stream().map(Holder::value).toList()) {
            builder.add(ruleSource.apply(context));
        }
        return (x, y, z) -> {
            for (SurfaceRules.SurfaceRule surfaceRule : builder.build()) {
                BlockState blockstate = surfaceRule.tryApply(x, y, z);
                if (blockstate != null) {
                    return blockstate;
                }
            }
            return null;
        };
    }
}
