package dev.worldgen.lithostitched.mixin.client;

import dev.worldgen.lithostitched.impl.duck.StructureAttributesHolder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import org.spongepowered.asm.mixin.Mixin;

import static dev.worldgen.lithostitched.impl.worldgen.structure.StructureAttributeHandler.STRUCTURE_ATTRIBUTE_LERP;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements StructureAttributesHolder {
	private EnvironmentAttributeMap previousAttributes = EnvironmentAttributeMap.EMPTY;
	private EnvironmentAttributeMap attributes = EnvironmentAttributeMap.EMPTY;
	private long ticksSinceUpdated = 0;
	
	@Override
	public EnvironmentAttributeMap getPreviousStructureAttributes() {
		return this.previousAttributes;
	}
	
	@Override
	public EnvironmentAttributeMap getStructureAttributes() {
		return this.attributes;
	}
	
	@Override
	public void updateStructureAttributes(EnvironmentAttributeMap attributes) {
		this.previousAttributes = this.attributes;
		this.attributes = attributes;
		this.ticksSinceUpdated = Math.max(STRUCTURE_ATTRIBUTE_LERP - this.ticksSinceUpdated, 0);
	}
	
	@Override
	public long getTicksSinceUpdated() {
		return this.ticksSinceUpdated;
	}
	
	@Override
	public void incrementTicksSinceUpdated() {
		this.ticksSinceUpdated++;
	}
}
