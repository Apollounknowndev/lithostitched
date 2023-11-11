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
public class AddSurfaceRuleModifier extends Modifier {
    public static final Codec<AddSurfaceRuleModifier> CODEC = RecordCodecBuilder.create(instance -> addModifierFields(instance).and(instance.group(
        ResourceKey.codec(Registries.LEVEL_STEM).listOf().xmap(HashSet::new, ArrayList::new).fieldOf("levels").forGetter(AddSurfaceRuleModifier::levels),
        SurfaceRules.RuleSource.CODEC.fieldOf("surface_rule").forGetter(AddSurfaceRuleModifier::surfaceRule)
    )).apply(instance, AddSurfaceRuleModifier::new));
    private final HashSet<ResourceKey<LevelStem>> levels;
    private final SurfaceRules.RuleSource surfaceRule;
    public AddSurfaceRuleModifier(ModifierPredicate predicate, HashSet<ResourceKey<LevelStem>> levels, SurfaceRules.RuleSource surfaceRule) {
        super(predicate, ModifierPhase.NONE);
        this.levels = levels;
        this.surfaceRule = surfaceRule;
    }
    public HashSet<ResourceKey<LevelStem>> levels() {
        return this.levels;
    }

    public SurfaceRules.RuleSource surfaceRule() {
        return this.surfaceRule;
    }

    @Override
    public void applyModifier() {}

    @Override
    public Codec<? extends Modifier> codec() {
        return AddSurfaceRuleModifier.CODEC;
    }
}
