package dev.worldgen.lithostitched.impl.debug;

import dev.worldgen.lithostitched.api.event.AddWorldgenModifiersEvent;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.Lithostitched;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;

public class LithostitchedDebugging {
	public static void init() {
		Lithostitched.LOGGER.error("Lithostitched's debug suite is enabled. Various modifications will be applied to world generation. If you do not want this, turn off the enable_debug_suite option in the config!");
		
		AddWorldgenModifiersEvent.EVENT.register((registries, consumer) -> {
			consumer.accept(id("add_basalt_to_overworld"), WorldgenModifier.builder().addFeatures(
				registries.lookupOrThrow(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD),
				registries.lookupOrThrow(Registries.PLACED_FEATURE).getOrThrow(NetherPlacements.LARGE_BASALT_COLUMNS),
				GenerationStep.Decoration.RAW_GENERATION
			));
		});
	}
	
	private static Identifier id(String name) {
		return Identifier.fromNamespaceAndPath("test", name);
	}
}
