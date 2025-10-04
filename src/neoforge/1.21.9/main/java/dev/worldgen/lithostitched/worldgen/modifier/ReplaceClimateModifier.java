package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.registry.LithostitchedNeoforgeBiomeModifiers;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;

/**
 * A {@link Modifier} implementation that replaces the biome climate settings of {@link Biome} entries.
 *
 * @author Apollo
 */
public class ReplaceClimateModifier extends AbstractBiomeModifier {
    public static final MapCodec<ReplaceClimateModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceClimateModifier::biomes),
        Biome.ClimateSettings.CODEC.fieldOf("climate").forGetter(ReplaceClimateModifier::climateSettings)
    ).apply(instance, ReplaceClimateModifier::new));
    private final HolderSet<Biome> biomes;
    private final Biome.ClimateSettings climateSettings;
    public ReplaceClimateModifier(HolderSet<Biome> biomes, Biome.ClimateSettings climateSettings) {
        super(new LithostitchedNeoforgeBiomeModifiers.ReplaceClimateBiomeModifier(biomes, climateSettings));
        this.biomes = biomes;
        this.climateSettings = climateSettings;
    }

    public HolderSet<Biome> biomes() {
        return biomes;
    }

    public Biome.ClimateSettings climateSettings() {
        return climateSettings;
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
