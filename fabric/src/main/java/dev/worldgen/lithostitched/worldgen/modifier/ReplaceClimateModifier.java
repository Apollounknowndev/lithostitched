package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

/**
 * A {@link Modifier} implementation that replaces the biome climate settings of {@link Biome} entries.
 *
 * @author Apollo
 */
public record ReplaceClimateModifier(ModifierPredicate predicate, HolderSet<Biome> biomes, Biome.ClimateSettings climateSettings) implements Modifier {
    public static final Codec<ReplaceClimateModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceClimateModifier::biomes),
        Biome.ClimateSettings.CODEC.fieldOf("climate").forGetter(ReplaceClimateModifier::climateSettings)
    )).apply(instance, ReplaceClimateModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.MODIFY;
    }

    public void applyModifier(Biome biome) {
        ((BiomeAccessor) (Object) biome).setClimateSettings(this.climateSettings());
    }

    @Override
    public void applyModifier() {
        List<Holder<Biome>> biomes = this.biomes().stream().toList();
        for (Holder<Biome> entry : biomes.stream().toList()) {
            this.applyModifier(entry.value());
        }
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
