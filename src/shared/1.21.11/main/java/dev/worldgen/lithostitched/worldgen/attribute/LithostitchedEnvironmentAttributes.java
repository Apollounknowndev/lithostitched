package dev.worldgen.lithostitched.worldgen.attribute;

import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;

import java.util.function.BiConsumer;

public interface LithostitchedEnvironmentAttributes {
	EnvironmentAttribute<Boolean> RESET_MUSIC = EnvironmentAttribute.builder(AttributeTypes.BOOLEAN).syncable().defaultValue(false).build();
	
	static void registerEnvironmentAttributes(BiConsumer<String, EnvironmentAttribute<?>> consumer) {
		consumer.accept("structure/reset_music", RESET_MUSIC);
	}
}
