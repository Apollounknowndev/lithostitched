package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.material.Fluid;
import net.msrandom.multiplatform.annotations.Expect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing core fields and methods used commonly by Lithostitched across mod loaders.
 * <p>Undocumented methods can be considered not API.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class Lithostitched {
	public static final String MOD_ID = "lithostitched";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> resourceKey, String name) {
		return ResourceKey.create(resourceKey, id(name));
	}

	public static Identifier id(String name) {
		return Identifier.fromNamespaceAndPath(MOD_ID, name);
	}

	@Expect
	public static <T> Registry<T> registry(RegistryAccess registries, ResourceKey<? extends Registry<T>> key);

	@Expect
	public static void scheduleTick(Level level, BlockPos pos, Block block, int flags);

	@Expect
	public static void scheduleTick(Level level, BlockPos pos, Fluid fluid, int flags);

	@Expect
	public static String getString(CompoundTag tag, String name);

	@Expect
	public static DensityFunction getInitialDensity(NoiseRouter router);

	@Expect
	public static String getInitialDensityName();

	public static void debug(String message, Object... arguments) {
		if (ConfigHandler.getConfig().logDebugMessages()) {
			Lithostitched.LOGGER.warn(message, arguments);
		}
	}
}
