package dev.worldgen.lithostitched.worldgen.surface.technical;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public class LithostitchedTechnicalRules extends SurfaceRules {
    /**
     * The {@link RuleSource} type responsible for merging new surface rules with original surface rules.
     *
     * @author SmellyModder (Luke Tonon)
     */
    public record TransientMergedRuleSource(List<RuleSource> sequence, RuleSource original) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<SurfaceRules.RuleSource> CODEC = KeyDispatchDataCodec.of(
            RuleSource.CODEC.xmap(
                source -> source,
                source -> source instanceof TransientMergedRuleSource transientMergedRuleSource ? transientMergedRuleSource.original : source
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
                builder.add(this.original.apply(context));
                return new SequenceRule(builder.build());
            }
        }
    }
}
