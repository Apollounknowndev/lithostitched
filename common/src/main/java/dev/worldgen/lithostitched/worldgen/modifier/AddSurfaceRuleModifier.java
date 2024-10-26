package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A {@link Modifier} implementation that adds surface rules to given level stems.
 * <p>Surface rule injection is independent of all other modifiers.</p>
 *
 * @author Apollo
 */
public record AddSurfaceRuleModifier(ModifierPredicate predicate, HashSet<ResourceKey<LevelStem>> levels, SurfaceRules.RuleSource surfaceRule) implements Modifier {
    public static final Codec<AddSurfaceRuleModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        ResourceKey.codec(Registries.LEVEL_STEM).listOf().xmap(HashSet::new, ArrayList::new).fieldOf("levels").forGetter(AddSurfaceRuleModifier::levels),
        SurfaceRules.RuleSource.CODEC.fieldOf("surface_rule").forGetter(AddSurfaceRuleModifier::surfaceRule)
    )).apply(instance, AddSurfaceRuleModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.ADD;
    }

    @Override
    public void applyModifier() {}

    @Override
    public Codec<? extends Modifier> codec() {
        return AddSurfaceRuleModifier.CODEC;
    }
}
