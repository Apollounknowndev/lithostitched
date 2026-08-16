package dev.worldgen.lithostitched.impl.worldgen.surface.rule;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

import java.util.List;

public record TransientMergedRule(List<RuleSource> rules, RuleSource original) implements RuleSource {
    public static final MapCodec<RuleSource> CODEC = RuleSource.CODEC.xmap(
        source -> source,
        source -> source instanceof TransientMergedRule transientMerged ? transientMerged.original : source
    ).fieldOf("original_source");
    public static final KeyDispatchDataCodec<RuleSource> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);
    
    @Override
    public KeyDispatchDataCodec<? extends RuleSource> codec() {
        return DATA_CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        if (this.rules.size() == 1) {
            return this.rules.getFirst().apply(context);
        } else {
            return SurfaceRules.sequence(this.rules.toArray(new RuleSource[0])).apply(context);
        }
    }
}