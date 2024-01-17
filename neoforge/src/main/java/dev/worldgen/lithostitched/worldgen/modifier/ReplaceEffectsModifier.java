package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.registry.LithostitchedNeoforgeBiomeModifiers;
import dev.worldgen.lithostitched.worldgen.biome.BiomeEffects;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;

/**
 * A {@link Modifier} implementation that replaces the biome special effects of {@link Biome} entries.
 *
 * @author Apollo
 */
public class ReplaceEffectsModifier extends AbstractBiomeModifier {
    public static final Codec<ReplaceEffectsModifier> CODEC = RecordCodecBuilder.create((instance) -> addModifierFields(instance).and(instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceEffectsModifier::biomes),
        BiomeEffects.CODEC.fieldOf("effects").forGetter(ReplaceEffectsModifier::effects)
    )).apply(instance, ReplaceEffectsModifier::new));
    private final HolderSet<Biome> biomes;
    private final BiomeEffects effects;
    public ReplaceEffectsModifier(ModifierPredicate predicate, HolderSet<Biome> biomes, BiomeEffects effects) {
        super(predicate, new LithostitchedNeoforgeBiomeModifiers.ReplaceEffectsBiomeModifier(biomes, effects));
        this.biomes = biomes;
        this.effects = effects;
    }

    public HolderSet<Biome> biomes() {
        return biomes;
    }

    public BiomeEffects effects() {
        return this.effects;
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
