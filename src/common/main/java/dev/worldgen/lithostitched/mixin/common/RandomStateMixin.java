package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.duck.SeedAccessor;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomState.class)
public class RandomStateMixin implements SeedAccessor {
	@Unique
	private long seed;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void saveSeed(NoiseGeneratorSettings settings, HolderGetter<NormalNoise.NoiseParameters> registry, long seed, CallbackInfo ci) {
		this.seed = seed;
	}
	
	@Override
	public long getSeed() {
		return seed;
	}
}
