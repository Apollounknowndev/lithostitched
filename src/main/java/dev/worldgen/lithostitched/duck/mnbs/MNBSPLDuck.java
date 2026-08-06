package dev.worldgen.lithostitched.duck.mnbs;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.Optional;

public interface MNBSPLDuck {
    void lithostitched$setParameters(Climate.ParameterList<Holder<Biome>> parameters);
    void lithostitched$setMigrationBiome(Optional<Holder<Biome>> biome);
    Optional<Holder<Biome>> lithostitched$getMigrationBiome();
}
