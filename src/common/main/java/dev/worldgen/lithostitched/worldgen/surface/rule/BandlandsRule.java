package dev.worldgen.lithostitched.worldgen.surface.rule;

import dev.worldgen.lithostitched.duck.ContextAccessor;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.bandlands.Bandlands;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record BandlandsRule(Holder<Bandlands> options) implements SurfaceRules.RuleSource {
    public static final KeyDispatchDataCodec<BandlandsRule> CODEC = KeyDispatchDataCodec.of(
        RegistryFileCodec.create(LithostitchedRegistryKeys.BANDLANDS, Bandlands.CODEC, false).fieldOf("options").xmap(BandlandsRule::new, BandlandsRule::options)
    );

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        return (x, y, z) -> this.options.value().getBand(((ContextAccessor)(Object)context).getSystem(), x, y, z);
    }
}