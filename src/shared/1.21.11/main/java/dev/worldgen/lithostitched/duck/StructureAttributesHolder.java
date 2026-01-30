package dev.worldgen.lithostitched.duck;

import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.Level;

public interface StructureAttributesHolder {
	EnvironmentAttributeMap getPreviousStructureAttributes();
	EnvironmentAttributeMap getStructureAttributes();
	void updateStructureAttributes(EnvironmentAttributeMap attributes);
	
	long getTicksSinceUpdated();
	void incrementTicksSinceUpdated();
	
	static StructureAttributesHolder from(Level level) {
		return (StructureAttributesHolder) level;
	}
}
