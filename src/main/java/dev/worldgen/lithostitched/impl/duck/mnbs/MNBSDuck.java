package dev.worldgen.lithostitched.impl.duck.mnbs;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

public interface MNBSDuck {
    Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> lithostitched$getEntries();
    void lithostitched$setEntries(Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> entries);
}
