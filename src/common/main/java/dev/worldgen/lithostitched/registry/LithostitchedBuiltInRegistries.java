package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.worldgen.bandlands.Band;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.processorcondition.ProcessorCondition;
import net.minecraft.core.WritableRegistry;

/**
 * Deprecated, please use the API module version of this class with the same name instead.
 */
@Deprecated(forRemoval = true)
public class LithostitchedBuiltInRegistries extends dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries {
	public static final WritableRegistry<MapCodec<? extends WorldgenModifier>> MODIFIER_TYPE = (WritableRegistry<MapCodec<? extends WorldgenModifier>>) dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries.MODIFIER_TYPE;
	public static final WritableRegistry<MapCodec<? extends PlacementCondition>> PLACEMENT_CONDITION_TYPE = (WritableRegistry<MapCodec<? extends PlacementCondition>>) dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries.PLACEMENT_CONDITION_TYPE;
	public static final WritableRegistry<MapCodec<? extends ProcessorCondition>> PROCESSOR_CONDITION_TYPE = (WritableRegistry<MapCodec<? extends ProcessorCondition>>) dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries.PROCESSOR_CONDITION_TYPE;
	public static final WritableRegistry<MapCodec<? extends Band>> BANDLANDS_BAND_TYPE = (WritableRegistry<MapCodec<? extends Band>>) dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries.BANDLANDS_BAND_TYPE;
}
