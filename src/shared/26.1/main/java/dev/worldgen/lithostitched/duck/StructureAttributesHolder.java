package dev.worldgen.lithostitched.duck;

import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.Level;

import static dev.worldgen.lithostitched.worldgen.attribute.LithostitchedEnvironmentAttributes.RESET_MUSIC;
import static dev.worldgen.lithostitched.worldgen.structure.StructureAttributeHandler.STRUCTURE_ATTRIBUTE_LERP;

public interface StructureAttributesHolder {
	EnvironmentAttributeMap getPreviousStructureAttributes();
	EnvironmentAttributeMap getStructureAttributes();
	void updateStructureAttributes(EnvironmentAttributeMap attributes);
	
	long getTicksSinceUpdated();
	void incrementTicksSinceUpdated();
	
	default boolean shouldFadeMusic() {
		boolean resetMusic = this.getPreviousStructureAttributes().contains(RESET_MUSIC) || this.getStructureAttributes().contains(RESET_MUSIC);
		return resetMusic && this.getTicksSinceUpdated() <= STRUCTURE_ATTRIBUTE_LERP;
	}
	
	static StructureAttributesHolder from(Level level) {
		return (StructureAttributesHolder) level;
	}
}
