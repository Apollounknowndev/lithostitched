package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.util.DensityFunctionWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.Objects;

public class WrapDensityFunctionModifier extends PriorityBasedModifier {
    private static final Codec<Holder<DensityFunction>> DF_REFERENCE_CODEC = RegistryFileCodec.create(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC, false);

    public static final Codec<WrapDensityFunctionModifier> CODEC = RecordCodecBuilder.create(instance -> addModifierFields(instance).and(instance.group(
        PRIORITY_CODEC.forGetter(WrapDensityFunctionModifier::priority),
        DF_REFERENCE_CODEC.fieldOf("target_function").forGetter(WrapDensityFunctionModifier::targetFunction),
        DensityFunction.CODEC.fieldOf("wrapper_function").forGetter(WrapDensityFunctionModifier::wrapperFunction)
    )).apply(instance, WrapDensityFunctionModifier::new));
    private final int priority;
    private final Holder<DensityFunction> targetFunction;
    private final Holder<DensityFunction> wrapperFunction;

    public WrapDensityFunctionModifier(ModifierPredicate predicate, int priority, Holder<DensityFunction> targetFunction, Holder<DensityFunction> wrapperFunction) {
        super(predicate, ModifierPhase.MODIFY);
        this.priority = priority;
        this.targetFunction = targetFunction;
        this.wrapperFunction = wrapperFunction;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void applyModifier() {
        if (this.targetFunction instanceof Holder.Reference<DensityFunction> reference) {
            var accessor = ((HolderReferenceAccessor<DensityFunction>) reference);
            accessor.setValue(DensityFunctionWrapper.wrap(this.targetFunction.value(), this.wrapperFunction.value()));
        }
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }

    public int priority() {
        return priority;
    }

    public Holder<DensityFunction> targetFunction() {
        return targetFunction;
    }

    public Holder<DensityFunction> wrapperFunction() {
        return wrapperFunction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (WrapDensityFunctionModifier) obj;
        return this.priority == that.priority &&
                Objects.equals(this.targetFunction, that.targetFunction) &&
                Objects.equals(this.wrapperFunction, that.wrapperFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, targetFunction, wrapperFunction);
    }

    @Override
    public String toString() {
        return "WrapDensityFunctionModifier[" +
                "priority=" + priority + ", " +
                "targetFunction=" + targetFunction + ", " +
                "wrapperFunction=" + wrapperFunction + ']';
    }

}
