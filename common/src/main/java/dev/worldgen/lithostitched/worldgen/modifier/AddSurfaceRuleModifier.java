package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

/**
 * A {@link Modifier} implementation that adds surface rules to given level stems.
 * <p>Surface rule injection is independent of all other modifiers.</p>
 *
 * @author Apollo
 */
public record AddSurfaceRuleModifier(List<ResourceKey<LevelStem>> levels, SurfaceRules.RuleSource surfaceRule, int priority) implements Modifier {
    public static final MapCodec<AddSurfaceRuleModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ResourceKey.codec(Registries.LEVEL_STEM).listOf().fieldOf("levels").forGetter(AddSurfaceRuleModifier::levels),
        SurfaceRules.RuleSource.CODEC.fieldOf("surface_rule").forGetter(AddSurfaceRuleModifier::surfaceRule),
        Codec.INT.fieldOf("priority").orElse(0).forGetter(AddSurfaceRuleModifier::priority)
    ).apply(instance, AddSurfaceRuleModifier::new));

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.NONE;
    }

    @Override
    public void applyModifier() {}

    @Override
    public MapCodec<? extends Modifier> codec() {
        return AddSurfaceRuleModifier.CODEC;
    }
}
