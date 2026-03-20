package dev.worldgen.lithostitched.impl;

import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.registry.LithostitchedRegistrar;
import dev.worldgen.lithostitched.worldgen.attribute.LithostitchedEnvironmentAttributes;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetBiomeAttributesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetDimensionAttributesModifier;
import dev.worldgen.lithostitched.worldgen.modifier.attribute.SetTimelineTracksModifier;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackType;
import net.msrandom.multiplatform.annotations.Actual;

import java.util.Map;

public class LithostitchedVersionActual {
	@Actual
	public static int getPackFormat() {
		return SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).major();
	}
	
	@Actual
	public static void initVersionRegistrations() {
		LithostitchedRegistrar.register(LithostitchedRegistries.MODIFIER_TYPE, Map.ofEntries(
			Map.entry("set_biome_attributes", SetBiomeAttributesModifier.CODEC),
			Map.entry("set_dimension_attributes", SetDimensionAttributesModifier.CODEC),
			Map.entry("set_timeline_tracks", SetTimelineTracksModifier.CODEC)
		));
		LithostitchedRegistrar.register(Registries.ENVIRONMENT_ATTRIBUTE, Map.ofEntries(
			Map.entry("structure/reset_music", LithostitchedEnvironmentAttributes.RESET_MUSIC)
		));
	}
}
