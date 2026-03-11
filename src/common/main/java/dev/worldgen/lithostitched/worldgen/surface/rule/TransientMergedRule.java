package dev.worldgen.lithostitched.worldgen.surface.rule;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

/**
 * The {@link SurfaceRules.RuleSource} type responsible for merging new surface rules with original surface rules.
 *
 * @author SmellyModder (Luke Tonon)
 */
public record TransientMergedRule(List<SurfaceRules.RuleSource> sequence, SurfaceRules.RuleSource original) implements SurfaceRules.RuleSource {
    public static final KeyDispatchDataCodec<SurfaceRules.RuleSource> CODEC = KeyDispatchDataCodec.of(
        SurfaceRules.RuleSource.CODEC.xmap(
            source -> source,
            source -> source instanceof TransientMergedRule transientMerged ? transientMerged.original : source
        ).fieldOf("original_source")
    );

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        if (this.sequence.size() == 1) {
            return this.sequence.getFirst().apply(context);
        } else {
            ImmutableList.Builder<SurfaceRules.SurfaceRule> builder = ImmutableList.builder();
            for (SurfaceRules.RuleSource ruleSource : this.sequence) {
                builder.add(ruleSource.apply(context));
            }
            ImmutableList<SurfaceRules.SurfaceRule> list = builder.add(this.original.apply(context)).build();
            return (x, y, z) -> {
                for (SurfaceRules.SurfaceRule surfaceRule : list) {
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