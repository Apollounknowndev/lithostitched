package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.util.DensityFunctionWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.Optional;

public record WrapDensityFunctionModifier(Optional<LoadPredicate> predicate, int priority, Holder<DensityFunction> targetFunction, Holder<DensityFunction> wrapperFunction) implements WorldgenModifier {
    private static final Codec<Holder<DensityFunction>> DF_REFERENCE_CODEC = RegistryFileCodec.create(Registries.DENSITY_FUNCTION, DensityFunction.DIRECT_CODEC, false);

    public static final MapCodec<WrapDensityFunctionModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PREDICATE_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(WrapDensityFunctionModifier::priority),
        DF_REFERENCE_CODEC.fieldOf("target_function").forGetter(WrapDensityFunctionModifier::targetFunction),
        DensityFunction.CODEC.fieldOf("wrapper_function").forGetter(WrapDensityFunctionModifier::wrapperFunction)
    ).apply(instance, WrapDensityFunctionModifier::new));

    @Override
    @SuppressWarnings("unchecked")
    public void apply(RegistryAccess registries) {
        if (this.targetFunction instanceof Holder.Reference<DensityFunction> reference) {
            var accessor = ((HolderReferenceAccessor<DensityFunction>)reference);
            accessor.setValue(DensityFunctionWrapper.wrap(this.targetFunction.value(), this.wrapperFunction.value()));
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
