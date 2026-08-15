package dev.worldgen.lithostitched.duck;

import dev.worldgen.lithostitched.worldgen.attribute.PositionalTimeBasedLayer;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;

public interface AttributeLayerBuilderDuck {
	<Value> EnvironmentAttributeSystem.Builder addPositionalTimeBasedLayer(
		EnvironmentAttribute<Value> attribute,
		PositionalTimeBasedLayer<Value> layer
	);
}
