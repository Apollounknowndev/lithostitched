package dev.worldgen.lithostitched.worldgen.densityfunction;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

import dev.worldgen.lithostitched.worldgen.modifier.WrapNoiseRouterModifier.Target;

public record ReferenceMarkerDensityFunction(Target target) implements MarkerFunction {
    public static final KeyDispatchDataCodec<ReferenceMarkerDensityFunction> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(instance -> instance.group(
            Target.CODEC.fieldOf("target").forGetter(ReferenceMarkerDensityFunction::target)
    ).apply(instance, ReferenceMarkerDensityFunction::new)));

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
