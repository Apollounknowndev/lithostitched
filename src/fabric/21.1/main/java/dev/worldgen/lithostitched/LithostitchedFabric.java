package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.resource.BreaksSeedParityCondition;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

/**
 * Mod class for Lithostitched on Fabric.
 */
public final class LithostitchedFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ConfigHandler.load();
		LithostitchedBuiltInRegistries.init();
		ResourceConditions.register(BreaksSeedParityCondition.TYPE);
	}
}
