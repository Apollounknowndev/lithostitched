package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeClimate;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public record ReplaceClimateModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, BiomeClimate climateSettings) implements WorldgenModifier {
    public static final MapCodec<ReplaceClimateModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(ReplaceClimateModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceClimateModifier::biomes),
        BiomeClimate.CODEC.fieldOf("climate").forGetter(ReplaceClimateModifier::climateSettings)
    ).apply(instance, ReplaceClimateModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        //? if neoforge
        //if (true) return;
        
        Registry<Biome> registry = Lithostitched.registry(registries, Registries.BIOME);
        for (Holder<Biome> entry : this.biomes()) {
            this.applyModifier(entry.value());
            WorldgenModifier.resetRegistrationInfo(registry, entry);
        }
    }

    public void applyModifier(Biome biome) {
        var originalClimate = ((BiomeAccessor) (Object) biome).getClimateSettings();

        var hasPrecipitation = this.climateSettings.hasPrecipitation().orElse(originalClimate.hasPrecipitation());
        var temperature = this.climateSettings.temperature().orElse(originalClimate.temperature());
        var temperatureModifier = this.climateSettings.temperatureModifier().orElse(originalClimate.temperatureModifier());
        var downfall = this.climateSettings.downfall().orElse(originalClimate.downfall());

        ((BiomeAccessor) (Object) biome).setClimateSettings(new Biome.ClimateSettings(hasPrecipitation, temperature, temperatureModifier, downfall));
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
