package dev.worldgen.lithostitched.mixin.client;

import dev.worldgen.lithostitched.duck.StructureAttributesHolder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.worldgen.lithostitched.worldgen.structure.StructureAttributeHandler.STRUCTURE_ATTRIBUTE_LERP;

@Mixin(EnvironmentAttributeSystem.class)
public class EnvironmentAttributeSystemMixin {
	@Inject(
		method = "addDefaultLayers",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/dimension/DimensionType;timelines()Lnet/minecraft/core/HolderSet;"
		)
	)
	private static void applyStructureAttributes(EnvironmentAttributeSystem.Builder builder, Level level, CallbackInfo ci) {
		var attributeRegistry = level.registryAccess().lookupOrThrow(Registries.ENVIRONMENT_ATTRIBUTE);
		for (Holder<EnvironmentAttribute<?>> holder : attributeRegistry.listElements().toList()) {
			if (holder.value().isPositional()) {
				addLayer(builder, level, holder.value());
			}
		}
	}
	
	@Unique
	private static <Value> void addLayer(EnvironmentAttributeSystem.Builder builder, Level level, EnvironmentAttribute<Value> attribute) {
		if (!(level instanceof ClientLevel)) return;
		
		StructureAttributesHolder holder = StructureAttributesHolder.from(level);
		builder.addTimeBasedLayer(attribute, (result, cacheTickId) -> {
			float delta = Math.min(holder.getTicksSinceUpdated(), (float) STRUCTURE_ATTRIBUTE_LERP) / STRUCTURE_ATTRIBUTE_LERP;
			
			var previousAttributeEntry = holder.getPreviousStructureAttributes().get(attribute);
			if (previousAttributeEntry != null) {
				Value updatedValue = previousAttributeEntry.applyModifier(result);
				result = attribute.type().stateChangeLerp().apply(1 - delta, result, updatedValue);
			};
			
			var attributeEntry = holder.getStructureAttributes().get(attribute);
			if (attributeEntry != null) {
				Value updatedValue = attributeEntry.applyModifier(result);
				result = attribute.type().stateChangeLerp().apply(delta, result, updatedValue);
			};
			
			return result;
		});
	}
}
