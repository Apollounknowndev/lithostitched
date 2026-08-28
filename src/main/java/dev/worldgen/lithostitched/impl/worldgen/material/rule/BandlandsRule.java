package dev.worldgen.lithostitched.impl.worldgen.material.rule;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.duck.MaterialSystemAccessor;
import dev.worldgen.lithostitched.impl.worldgen.bandlands.Bandlands;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.MaterialSystem;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;

public record BandlandsRule(Holder<Bandlands> options) implements MaterialRule {
    public static final MapCodec<BandlandsRule> CODEC = RegistryCodecs.holder(LithostitchedRegistries.BANDLANDS).fieldOf("options").xmap(BandlandsRule::new, BandlandsRule::options);
    
    @Override
    public RuleEvaluator compile(MaterialRuleContext context) {
        MaterialSystem system = ((MaterialSystemAccessor)(Object)context).getSystem();
        return (x, y, z) -> this.options.value().getBand(system, x, y, z);
    }
    
    @Override
    public MapCodec<? extends MaterialRule> codec() {
        return CODEC;
    }
}