package dev.worldgen.lithostitched.impl.worldgen.attribute;

import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface PositionalTimeBasedLayer<Value> extends EnvironmentAttributeLayer<Value> {
	Value applyPositionalTimeBased(Value baseValue, int cacheTickId, Vec3 pos, @Nullable SpatialAttributeInterpolator biomeInterpolator);
}
