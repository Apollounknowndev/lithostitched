package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.processor.condition.ProcessorCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Class containing the resource keys of every registry registered by Lithostitched.
 *
 * @author SmellyModder (Luke Tonon)
 */
public interface LithostitchedRegistryKeys {
	ResourceKey<Registry<Modifier>> WORLDGEN_MODIFIER = create("worldgen_modifier");
	ResourceKey<Registry<MapCodec<? extends Modifier>>> MODIFIER_TYPE = create("modifier_type");
	ResourceKey<Registry<MapCodec<? extends PlacementCondition>>> PLACEMENT_CONDITION_TYPE = create("placement_condition_type");
	ResourceKey<Registry<MapCodec<? extends ProcessorCondition>>> PROCESSOR_CONDITION_TYPE = create("processor_condition_type");

	private static <T> ResourceKey<Registry<T>> create(String name) {
		return ResourceKey.createRegistryKey(LithostitchedCommon.id(name));
	}
}
