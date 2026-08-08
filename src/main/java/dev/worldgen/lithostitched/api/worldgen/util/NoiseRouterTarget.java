package dev.worldgen.lithostitched.api.worldgen.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;

import java.util.function.Function;

public enum NoiseRouterTarget implements StringRepresentable {
    BARRIER("barrier", NoiseRouter::barrierNoise),
    FLUID_LEVEL_FLOODEDNESS("fluid_level_floodedness", NoiseRouter::fluidLevelFloodednessNoise),
    FLUID_LEVEL_SPREAD("fluid_level_spread", NoiseRouter::fluidLevelSpreadNoise),
    LAVA("lava", NoiseRouter::lavaNoise),
    TEMPERATURE("temperature", NoiseRouter::temperature),
    VEGETATION("vegetation", NoiseRouter::vegetation),
    CONTINENTS("continents", NoiseRouter::continents),
    EROSION("erosion", NoiseRouter::erosion),
    DEPTH("depth", NoiseRouter::depth),
    RIDGES("ridges", NoiseRouter::ridges),
    PRELIMINARY_SURFACE_LEVEL("preliminary_surface_level", NoiseRouter::preliminarySurfaceLevel),
    FINAL_DENSITY("final_density", NoiseRouter::finalDensity),
    VEIN_TOGGLE("vein_toggle", NoiseRouter::veinToggle),
    VEIN_RIDGED("vein_ridged", NoiseRouter::veinRidged),
    VEIN_GAP("vein_gap", NoiseRouter::veinGap);

    public static final Codec<NoiseRouterTarget> CODEC = StringRepresentable.fromEnum(NoiseRouterTarget::values);
    private final String name;
    private final Function<NoiseRouter, DensityFunction> getter;

    NoiseRouterTarget(String name, Function<NoiseRouter, DensityFunction> getter) {
        this.name = name;
        this.getter = getter;
    }

    public DensityFunction getDensityFunction(NoiseRouter router) {
        return this.getter.apply(router);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
