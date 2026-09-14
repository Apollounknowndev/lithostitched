package dev.worldgen.lithostitched.api.worldgen.attribute;

import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;

public interface LithostitchedEnvironmentAttributes {
	EnvironmentAttribute<Boolean> RESET_MUSIC = EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).syncable().defaultValue(false).build();
}
