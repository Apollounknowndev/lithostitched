package dev.worldgen.lithostitched.mixin.common.bandlands;

import dev.worldgen.lithostitched.duck.SurfaceSystemAccessor;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SurfaceSystem.class)
public class SurfaceSystemMixin implements SurfaceSystemAccessor {
    @Shadow @Final private PositionalRandomFactory noiseRandom;

    @Shadow @Final private NormalNoise clayBandsOffsetNoise;

    @Override
    public NormalNoise getBandOffsetNoise() {
        return this.clayBandsOffsetNoise;
    }

    @Override
    public PositionalRandomFactory getNoiseRandom() {
        return this.noiseRandom;
    }
}