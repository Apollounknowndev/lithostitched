package dev.worldgen.lithostitched.mixin.common.timeline;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.worldgen.lithostitched.worldgen.attribute.PositionalTimeBasedLayer;
import net.minecraft.world.attribute.*;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EnvironmentAttributeSystem.ValueSampler.class)
public class EnvironmentAttributeSystemValueSamplerMixin<Value> {
	@Shadow
	@Final
	private List<EnvironmentAttributeLayer<Value>> layers;
	
	@Shadow
	private int cacheTickId;
	
	@Definition(id = "layers", field = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$ValueSampler;layers:Ljava/util/List;")
	@Expression("this.layers")
	@ModifyExpressionValue(
		method = {
			"computeValuePositional",
			"computeValueNotPositional"
		},
		at = @At("MIXINEXTRAS:EXPRESSION")
	)
	private List<EnvironmentAttributeLayer<Value>> removeBiomeTimelineLayers(List<EnvironmentAttributeLayer<Value>> layers) {
		return layers.stream().filter(layer -> (!(layer instanceof PositionalTimeBasedLayer<Value>))).toList();
	}
	
	@WrapOperation(
		method = "computeValuePositional",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/attribute/EnvironmentAttribute;sanitizeValue(Ljava/lang/Object;)Ljava/lang/Object;"
		)
	)
	private Value applyBiomeTimelineLayers(EnvironmentAttribute<Value> attribute, Value result, Operation<Value> operation, @Local(ordinal = 0) Vec3 pos, @Local(ordinal = 0) SpatialAttributeInterpolator biomeInterpolator) {
		for (EnvironmentAttributeLayer<Value> layer : this.layers.stream().filter(layer -> (layer instanceof PositionalTimeBasedLayer<Value>)).toList()) {
			result = ((PositionalTimeBasedLayer<Value>)layer).applyPositionalTimeBased(result, this.cacheTickId, pos, biomeInterpolator);
		}
		return operation.call(attribute, result);
	}
}
