package dev.worldgen.lithostitched.worldgen.attribute;

import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface PositionalTimeBasedLayer<Value> extends EnvironmentAttributeLayer.Positional<Value> {
	Value applyPositionalTimeBased(Value baseValue, int cacheTickId, Vec3 pos, @Nullable SpatialAttributeInterpolator biomeInterpolator);
	
	default Value applyPositional(Value baseValue, Vec3 pos, @Nullable SpatialAttributeInterpolator biomeInterpolator) {
		throw new IllegalStateException();
	}
}
