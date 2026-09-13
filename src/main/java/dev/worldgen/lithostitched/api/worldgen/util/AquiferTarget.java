package dev.worldgen.lithostitched.api.worldgen.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.Aquifer.Config;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

import java.util.function.Function;

public enum AquiferTarget implements StringRepresentable {
    BARRIER("barrier", Config::barrierNoise),
    FLUID_LEVEL_FLOODEDNESS("fluid_level_floodedness", Config::fluidLevelFloodednessNoise),
    FLUID_LEVEL_SPREAD("fluid_level_spread", Config::fluidLevelSpreadNoise),
    LAVA("lava", Config::lavaNoise),
    EXCLUSION("exclusion", Config::exclusion),
    SURFACE_LEVEL("surface_level", Config::surfaceLevel);

    public static final Codec<AquiferTarget> CODEC = StringRepresentable.fromEnum(AquiferTarget::values);
    private final String name;
    private final Function<Config, DensityFunction> getter;

    AquiferTarget(String name, Function<Config, DensityFunction> getter) {
        this.name = name;
        this.getter = getter;
    }

    public DensityFunction getDensityFunction(Config config) {
        return this.getter.apply(config);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
