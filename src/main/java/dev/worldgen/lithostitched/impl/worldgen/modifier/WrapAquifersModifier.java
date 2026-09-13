package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.AquiferTarget;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import dev.worldgen.lithostitched.impl.worldgen.modifier.util.DensityFunctionInjectorHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public record WrapAquifersModifier(Optional<LoadPredicate> predicate, int priority, ResourceKey<Level> dimension, AquiferTarget target, Holder<DensityFunction> wrapperFunction) implements WorldgenModifier {
    public static final MapCodec<WrapAquifersModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(WrapAquifersModifier::priority),
        ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(WrapAquifersModifier::dimension),
        AquiferTarget.CODEC.fieldOf("target").forGetter(WrapAquifersModifier::target),
        LithostitchedCodecs.DF_REFERENCE.fieldOf("wrapper_function").forGetter(WrapAquifersModifier::wrapperFunction)
    ).apply(instance, WrapAquifersModifier::new));

    @Override
    public void apply(RegistryAccess registries) {}

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }

    public static DensityFunction modifyAquiferFunction(AquiferTarget target, DensityFunction wrapped, List<WrapAquifersModifier> modifiers) {
        List<DensityFunction> orderedFunctions = modifiers.stream()
            .filter(modifier -> modifier.target == target)
            .sorted(Comparator.comparingInt(WrapAquifersModifier::priority))
            .map(modifier -> modifier.wrapperFunction().value())
            .toList();

        if (orderedFunctions.isEmpty()) return wrapped;

        DensityFunction mergedFunction = wrapped;
        for (DensityFunction function : orderedFunctions) {
            mergedFunction = DensityFunctionInjectorHelper.wrap(mergedFunction, function);
        }

        return mergedFunction;
    }
}
