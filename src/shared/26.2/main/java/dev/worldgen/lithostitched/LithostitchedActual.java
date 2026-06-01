package dev.worldgen.lithostitched;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.material.Fluid;
import net.msrandom.multiplatform.annotations.Actual;

public class LithostitchedActual {
    @Actual
    public static <T> Registry<T> registry(RegistryAccess registries, ResourceKey<? extends Registry<T>> key) {
        return registries.lookupOrThrow(key);
    }

    @Actual
    public static void scheduleTick(Level level, BlockPos pos, Block block, int flags) {
        level.scheduleTick(pos, block, flags);
    }

    @Actual
    public static void scheduleTick(Level level, BlockPos pos, Fluid fluid, int flags) {
        level.scheduleTick(pos, fluid, flags);
    }

    @Actual
    public static String getString(CompoundTag tag, String name) {
        return tag.getStringOr(name, "");
    }

    @Actual
    public static DensityFunction getInitialDensity(NoiseRouter router) {
        return router.preliminarySurfaceLevel();
    }

    @Actual
    public static String getInitialDensityName() {
        return "preliminary_surface_level";
    }
}
