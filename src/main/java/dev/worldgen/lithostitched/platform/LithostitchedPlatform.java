package dev.worldgen.lithostitched.platform;


import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.function.Supplier;

//? if fabric {
/*import com.google.common.base.Suppliers;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.BiomeGenerationSettingsAccessor;
import net.fabricmc.loader.api.FabricLoader;
*///? } else {
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.Lazy;
//? }

public class LithostitchedPlatform {
	public static boolean isModLoaded(String id) {
		//? if fabric
		//return FabricLoader.getInstance().isModLoaded(id);
		//? if neoforge
		return ModList.get().isLoaded(id);
	}
	
	public static void rebuildSettings(Biome biome, List<HolderSet<PlacedFeature>> features) {
		//? if fabric {
		/*((BiomeAccessor) (Object) biome).setGenerationSettings(BiomeGenerationSettingsAccessor.createGenerationSettings(
			((BiomeGenerationSettingsAccessor) biome.getGenerationSettings()).getCarvers(),
			features
		));
		*///? } else {
		throw new IllegalStateException("Cannot call LithostitchedPlatform#rebuildSettings on Neoforge.");
		//? }
	}
	
	public static <T> Supplier<T> memoize(Supplier<T> delegate) {
		//? if fabric {
		/*return Suppliers.memoize(delegate::get);
		 *///? } else {
		return Lazy.of(delegate);
		//? }
		
	}
}
