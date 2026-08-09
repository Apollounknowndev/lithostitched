package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.impl.duck.SeedAccessor;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(RandomState.class)
public class RandomStateMixin implements SeedAccessor {
	@Unique
	private long seed;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void saveSeed(
		HolderGetter<NormalNoise> noises, long seed, boolean useLegacyRandom, BlockState defaultBlock, 
		int seaLevel, NoiseRouter noiseRouter, List<SpawnTargetPoint> spawnTarget, 
		Optional<Aquifer.Config> aquifers, List<OreVeinifier> oreVeins, CallbackInfo ci
	) {
		this.seed = seed;
	}
	
	@Override
	public long getSeed() {
		return seed;
	}
}
