package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.util.DensityFunctionWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record WrapDensityFunctionModifier(int priority, Holder<DensityFunction> targetFunction, Holder<DensityFunction> wrapperFunction) implements PriorityBasedModifier {
    private static final Codec<Holder<DensityFunction>> DF_REFERENCE_CODEC = RegistryFileCodec.create(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC, false);

    public static final MapCodec<WrapDensityFunctionModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_CODEC.forGetter(WrapDensityFunctionModifier::priority),
        DF_REFERENCE_CODEC.fieldOf("target_function").forGetter(WrapDensityFunctionModifier::targetFunction),
        DensityFunction.CODEC.fieldOf("wrapper_function").forGetter(WrapDensityFunctionModifier::wrapperFunction)
    ).apply(instance, WrapDensityFunctionModifier::new));

    @Override
    @SuppressWarnings("unchecked")
    public void applyModifier() {
        if (this.targetFunction instanceof Holder.Reference<DensityFunction> reference) {
            var accessor = ((HolderReferenceAccessor<DensityFunction>)reference);
            accessor.setValue(DensityFunctionWrapper.wrap(this.targetFunction.value(), this.wrapperFunction.value()));
        }
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.MODIFY;
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
