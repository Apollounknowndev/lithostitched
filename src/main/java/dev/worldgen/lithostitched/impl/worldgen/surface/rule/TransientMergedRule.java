package dev.worldgen.lithostitched.impl.worldgen.surface.rule;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

import java.util.List;

/**
 * The {@link RuleSource} type responsible for merging new surface rules with original surface rules.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record TransientMergedRule(List<RuleSource> rules, RuleSource original) implements RuleSource {
    public static final MapCodec<RuleSource> CODEC = RuleSource.CODEC.xmap(
        source -> source,
        source -> source instanceof TransientMergedRule transientMerged ? transientMerged.original : source
    ).fieldOf("original_source");
    
    @Override
    public MapCodec<? extends RuleSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        if (this.rules.size() == 1) {
            return this.rules.getFirst().apply(context);
        } else {
            ImmutableList.Builder<SurfaceRules.SurfaceRule> builder = ImmutableList.builder();
            for (RuleSource ruleSource : this.rules) {
                builder.add(ruleSource.apply(context));
            }
            builder.add(this.original.apply(context));
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
}