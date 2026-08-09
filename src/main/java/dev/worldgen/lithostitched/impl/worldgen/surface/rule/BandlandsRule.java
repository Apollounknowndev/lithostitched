package dev.worldgen.lithostitched.impl.worldgen.surface.rule;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.duck.ContextAccessor;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record BandlandsRule(Holder<Bandlands> options) implements SurfaceRules.RuleSource {
    public static final MapCodec<BandlandsRule> CODEC = RegistryCodecs.holder(LithostitchedRegistries.BANDLANDS).fieldOf("options").xmap(BandlandsRule::new, BandlandsRule::options);
    
    @Override
    public MapCodec<? extends SurfaceRules.RuleSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        return (x, y, z) -> this.options.value().getBand(((ContextAccessor)(Object)context).getSystem(), x, y, z);
    }
}