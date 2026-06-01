package dev.worldgen.lithostitched.mixin.common.mnbs;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Climate.ParameterList.class)
public interface ParameterListAccessor<T> {
	@Accessor("values")
	@Mutable
	@Final
	void lithostitched$setValues(List<Pair<Climate.ParameterPoint, T>> values);
	
	@Accessor("index")
	@Mutable
	@Final
	void lithostitched$index(Climate.RTree<T> values);
}
