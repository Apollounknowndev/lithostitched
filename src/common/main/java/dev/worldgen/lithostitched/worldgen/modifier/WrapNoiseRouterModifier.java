package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.worldgen.NoiseRouterTarget;
import dev.worldgen.lithostitched.worldgen.modifier.util.DensityFunctionWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.Comparator;
import java.util.List;

public record WrapNoiseRouterModifier(int priority, ResourceKey<Level> dimension, NoiseRouterTarget target, Holder<DensityFunction> wrapperFunction) implements WorldgenModifier {
    public static final MapCodec<WrapNoiseRouterModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("priority", 1000).forGetter(WrapNoiseRouterModifier::priority),
        ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(WrapNoiseRouterModifier::dimension),
        NoiseRouterTarget.CODEC.fieldOf("target").forGetter(WrapNoiseRouterModifier::target),
        DensityFunction.CODEC.fieldOf("wrapper_function").forGetter(WrapNoiseRouterModifier::wrapperFunction)
    ).apply(instance, WrapNoiseRouterModifier::new));

    @Override
    public void apply(RegistryAccess registries) {}

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }

    public static DensityFunction modifyDensityFunction(NoiseRouterTarget target, DensityFunction wrapped, List<WrapNoiseRouterModifier> modifiers) {
        List<DensityFunction> orderedFunctions = modifiers.stream()
            .filter(modifier -> modifier.target == target)
            .sorted(Comparator.comparingInt(WrapNoiseRouterModifier::priority))
            .map(modifier -> modifier.wrapperFunction().value())
            .toList();

        if (orderedFunctions.isEmpty()) return wrapped;

        DensityFunction mergedFunction = wrapped;
        for (DensityFunction function : orderedFunctions) {
            mergedFunction = DensityFunctionWrapper.wrap(mergedFunction, function);
        }

        return mergedFunction;
    }
}
