package dev.worldgen.lithostitched.impl.worldgen.surface.rule;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.SurfaceRules.SurfaceRule;

public record ReferenceRule(HolderSet<RuleSource> rules) implements RuleSource {
    public static final MapCodec<ReferenceRule> CODEC = RegistryCodecs.homogeneousList(LithostitchedRegistries.SURFACE_RULE).fieldOf("rules").xmap(ReferenceRule::new, ReferenceRule::rules);
    public static final KeyDispatchDataCodec<ReferenceRule> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);
    
    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return DATA_CODEC;
    }

    @Override
    public SurfaceRule apply(SurfaceRules.Context context) {
        if (this.rules.size() == 0) return (x, y, z) -> null;
        if (this.rules.size() == 1) return this.rules.get(0).value().apply(context);
        
        SurfaceRule[] sources = this.rules.stream().map(Holder::value).map(source -> source.apply(context)).toArray(SurfaceRule[]::new);
        return (x, y, z) -> {
            for (SurfaceRule surfaceRule : sources) {
                BlockState blockState = surfaceRule.tryApply(x, y, z);
                if (blockState != null) {
                    return blockState;
                }
            }
            return null;
        };
    }
}
