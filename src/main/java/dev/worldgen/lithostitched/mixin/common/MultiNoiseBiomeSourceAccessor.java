package dev.worldgen.lithostitched.mixin.common;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiNoiseBiomeSource.class)
public interface MultiNoiseBiomeSourceAccessor {
	@Accessor("parameters")
	Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> getParameters();
	
	@Accessor("parameters")
	void setParameters(Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters);
}
