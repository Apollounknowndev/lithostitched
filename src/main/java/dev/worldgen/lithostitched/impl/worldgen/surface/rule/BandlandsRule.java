package dev.worldgen.lithostitched.impl.worldgen.surface.rule;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.duck.ContextAccessor;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;

public record BandlandsRule(Holder<Bandlands> options) implements SurfaceRules.RuleSource {
    public static final MapCodec<BandlandsRule> CODEC = RegistryFileCodec.create(LithostitchedRegistries.BANDLANDS, Bandlands.CODEC, false).fieldOf("options").xmap(BandlandsRule::new, BandlandsRule::options);
    public static final KeyDispatchDataCodec<BandlandsRule> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);
    
    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return DATA_CODEC;
    }
    
    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        return (x, y, z) -> this.options.value().getBand(((ContextAccessor)(Object)context).lithostitched$getSystem(), x, y, z);
    }
}