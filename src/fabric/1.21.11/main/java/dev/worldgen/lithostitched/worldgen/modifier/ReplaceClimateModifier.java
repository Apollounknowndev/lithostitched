package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.util.BiomeClimate;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

/**
 * A {@link Modifier} implementation that replaces the biome climate settings of {@link Biome} entries.
 *
 * @author Apollo
 */
public record ReplaceClimateModifier(int priority, HolderSet<Biome> biomes, BiomeClimate climateSettings) implements Modifier {
    public static final MapCodec<ReplaceClimateModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_DEFAULT.forGetter(ReplaceClimateModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceClimateModifier::biomes),
        BiomeClimate.CODEC.fieldOf("climate").forGetter(ReplaceClimateModifier::climateSettings)
    ).apply(instance, ReplaceClimateModifier::new));

    @SuppressWarnings("unchecked")
    @Override
    public void applyModifier(RegistryAccess registries) {
        List<Holder<Biome>> biomes = this.biomes().stream().toList();
        Registry<Biome> registry = Lithostitched.registry(registries, Registries.BIOME);
        for (Holder<Biome> entry : biomes) {
            this.applyModifier(entry.value());
            Modifier.resetRegistrationInfo(registry, entry);
        }
    }

    @Override
    public void applyModifier() {}

    public void applyModifier(Biome biome) {
        var originalClimate = ((BiomeAccessor) (Object) biome).getClimateSettings();

        var hasPrecipitation = this.climateSettings.hasPrecipitation().orElse(originalClimate.hasPrecipitation());
        var temperature = this.climateSettings.temperature().orElse(originalClimate.temperature());
        var temperatureModifier = this.climateSettings.temperatureModifier().orElse(originalClimate.temperatureModifier());
        var downfall = this.climateSettings.downfall().orElse(originalClimate.downfall());

        ((BiomeAccessor) (Object) biome).setClimateSettings(new Biome.ClimateSettings(hasPrecipitation, temperature, temperatureModifier, downfall));
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
