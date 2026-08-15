package dev.worldgen.lithostitched.mixin.common.timeline;

import dev.worldgen.lithostitched.impl.duck.AttributeLayerBuilderDuck;
import dev.worldgen.lithostitched.worldgen.attribute.PositionalTimeBasedLayer;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnvironmentAttributeSystem.Builder.class)
public abstract class EnvironmentAttributeSystemBuilderMixin implements AttributeLayerBuilderDuck {
	@Shadow
	public abstract <Value> EnvironmentAttributeSystem.Builder addLayer(EnvironmentAttribute<Value> attribute, EnvironmentAttributeLayer<Value> layer);
	
	@Override
	public <Value> EnvironmentAttributeSystem.Builder addPositionalTimeBasedLayer(EnvironmentAttribute<Value> attribute, PositionalTimeBasedLayer<Value> layer) {
		return this.addLayer(attribute, layer);
	}
}
