package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Accessor("biomeSource")
    BiomeSource getBiomeSource();

    @Accessor("generationSettingsGetter")
    Function<Holder<Biome>, BiomeGenerationSettings> getGetter();

    @Accessor("featuresPerStep")
    @Mutable
    void setFeaturesPerStep(Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep);
}
