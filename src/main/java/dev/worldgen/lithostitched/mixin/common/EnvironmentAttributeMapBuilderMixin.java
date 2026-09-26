package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.impl.duck.AttributeMapDuck;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(EnvironmentAttributeMap.Builder.class)
public class EnvironmentAttributeMapBuilderMixin implements AttributeMapDuck {
	@Shadow @Final protected Map<EnvironmentAttribute<?>, EnvironmentAttributeMap.Entry<?, ?>> entries;
	
	@Override
	public void clear() {
		this.entries.clear();
	}
}
