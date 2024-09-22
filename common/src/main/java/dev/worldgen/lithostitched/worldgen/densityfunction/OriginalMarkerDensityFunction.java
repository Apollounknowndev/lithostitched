package dev.worldgen.lithostitched.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public class OriginalMarkerDensityFunction implements MarkerFunction {
    public static final KeyDispatchDataCodec<OriginalMarkerDensityFunction> CODEC = KeyDispatchDataCodec.of(Codec.unit(new OriginalMarkerDensityFunction()));


    @Override
    public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
