package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.api.event.AddWorldgenModifiersEvent;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.network.ApplyStructureAttributesPacket;
import dev.worldgen.lithostitched.resource.BreaksSeedParityCondition;
import dev.worldgen.lithostitched.worldgen.structure.StructureAttributeHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;

import static dev.worldgen.lithostitched.api.worldgen.densityfunction.LithostitchedDensityFunctions.*;

/**
 * Mod class for Lithostitched on Fabric.
 */
public final class LithostitchedFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ConfigHandler.load();
		LithostitchedBuiltInRegistries.init();
		ResourceConditions.register(BreaksSeedParityCondition.TYPE);
		
		PayloadTypeRegistry.playS2C().register(
			ApplyStructureAttributesPacket.TYPE,
			ApplyStructureAttributesPacket.CODEC
		);
		
		ServerTickEvents.START_WORLD_TICK.register(StructureAttributeHandler::tick);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> StructureAttributeHandler.disconnect(handler.getPlayer()));
		
		
		// Flips the offset spline on the z-axis
		AddWorldgenModifiersEvent.EVENT.register((registries, consumer) -> {
			DensityFunction xAxisFlip = shift(
				wrappedMarker(),
				DensityFunctions.mul(
					DensityFunctions.constant(-2),
					axis(Direction.Axis.X)
				),
				DensityFunctions.zero(),
				DensityFunctions.zero()
			);
			consumer.accept(
				Identifier.fromNamespaceAndPath("my_mod", "flip/continents"),
				WorldgenModifier.builder().wrapDensityFunction(registries.getOrThrow(NoiseRouterData.CONTINENTS), xAxisFlip)
			);
			consumer.accept(
				Identifier.fromNamespaceAndPath("my_mod", "flip/erosion"),
				WorldgenModifier.builder().wrapDensityFunction(registries.getOrThrow(NoiseRouterData.EROSION), xAxisFlip)
			);
			consumer.accept(
				Identifier.fromNamespaceAndPath("my_mod", "flip/ridges"),
				WorldgenModifier.builder().wrapDensityFunction(registries.getOrThrow(NoiseRouterData.RIDGES), xAxisFlip)
			);
			consumer.accept(
				Identifier.fromNamespaceAndPath("my_mod", "flip/temperature"),
				WorldgenModifier.builder().wrapNoiseRouter(Level.OVERWORLD, NoiseRouterTarget.TEMPERATURE, xAxisFlip)
			);
			consumer.accept(
				Identifier.fromNamespaceAndPath("my_mod", "flip/vegetation"),
				WorldgenModifier.builder().wrapNoiseRouter(Level.OVERWORLD, NoiseRouterTarget.VEGETATION, xAxisFlip)
			);
		});
	}
}
