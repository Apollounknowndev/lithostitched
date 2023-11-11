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
public class ReplaceClimateModifier extends Modifier {
    public static final Codec<ReplaceClimateModifier> CODEC = RecordCodecBuilder.create((instance) -> addModifierFields(instance).and(instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceClimateModifier::biomes),
        Biome.ClimateSettings.CODEC.fieldOf("climate").forGetter(ReplaceClimateModifier::climateSettings)
    )).apply(instance, ReplaceClimateModifier::new));
    private final HolderSet<Biome> biomes;
    private final Biome.ClimateSettings climateSettings;
    public ReplaceClimateModifier(ModifierPredicate predicate, HolderSet<Biome> biomes, Biome.ClimateSettings climateSettings) {
        super(predicate, ModifierPhase.MODIFY);
        this.biomes = biomes;
        this.climateSettings = climateSettings;
    }

    public HolderSet<Biome> biomes() {
        return biomes;
    }

    public Biome.ClimateSettings climateSettings() {
        return climateSettings;
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
