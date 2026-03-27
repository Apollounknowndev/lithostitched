package dev.worldgen.lithostitched.mixin.common.mnbs;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiNoiseBiomeSourceParameterList.class)
public interface MNBSPLAccessor {
	@Accessor("parameters")
	void setParameters(Climate.ParameterList<Holder<Biome>> parameters);
}
