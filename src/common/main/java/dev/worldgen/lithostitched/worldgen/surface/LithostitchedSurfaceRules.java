package dev.worldgen.lithostitched.worldgen.surface;

import com.google.common.collect.ImmutableList;
import dev.worldgen.lithostitched.duck.ContextAccessor;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.bandlands.Bandlands;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

public class LithostitchedSurfaceRules extends SurfaceRules {
    /**
     * The {@link RuleSource} type responsible for merging new surface rules with original surface rules.
     *
     * @author SmellyModder (Luke Tonon)
     */
    public record TransientMergedRuleSource(List<RuleSource> sequence, RuleSource original) implements RuleSource {
        public static final KeyDispatchDataCodec<RuleSource> CODEC = KeyDispatchDataCodec.of(
            RuleSource.CODEC.xmap(
                source -> source,
                source -> source instanceof TransientMergedRuleSource transientMergedRuleSource ? transientMergedRuleSource.original : source
            ).fieldOf("original_source")
        );

        @Override
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        @Override
        public SurfaceRule apply(Context context) {
            if (this.sequence.size() == 1) {
                return this.sequence.get(0).apply(context);
            } else {
                ImmutableList.Builder<SurfaceRule> builder = ImmutableList.builder();
                for (RuleSource ruleSource : this.sequence) {
                    builder.add(ruleSource.apply(context));
                }
                builder.add(this.original.apply(context));
                return (x, y, z) -> {
                    for (SurfaceRule surfaceRule : builder.build()) {
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

    public record BandlandsRuleSource(Holder<Bandlands> options) implements RuleSource {
        public static final KeyDispatchDataCodec<BandlandsRuleSource> CODEC = KeyDispatchDataCodec.of(
            RegistryFileCodec.create(LithostitchedRegistryKeys.BANDLANDS, Bandlands.CODEC, false).fieldOf("options").xmap(BandlandsRuleSource::new, BandlandsRuleSource::options)
        );

        @Override
        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        @Override
        public SurfaceRule apply(Context context) {
            return (x, y, z) -> options.value().getBand(((ContextAccessor)(Object)context).getSystem(), x, y, z);
        }
    }
}
