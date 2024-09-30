package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.util.DensityFunctionWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public record WrapNoiseRouterModifier(int priority, Either<ResourceKey<Level>, ResourceKey<NoiseGeneratorSettings>> dimension, Target target, Holder<DensityFunction> wrapperFunction) implements Modifier {
    public static final MapCodec<WrapNoiseRouterModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("priority", 1000).forGetter(WrapNoiseRouterModifier::priority),
        Codec.mapEither(ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension"), ResourceKey.codec(Registries.NOISE_SETTINGS).fieldOf("noise_settings")).forGetter(WrapNoiseRouterModifier::dimension),
        Target.CODEC.fieldOf("target").forGetter(WrapNoiseRouterModifier::target),
        DensityFunction.CODEC.fieldOf("wrapper_function").forGetter(WrapNoiseRouterModifier::wrapperFunction)
    ).apply(instance, WrapNoiseRouterModifier::new));

    @Override
    public void applyModifier() {}

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.NONE;
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }

    public static DensityFunction modifyDensityFunction(NoiseRouter router, Target target, DensityFunction wrapped, List<WrapNoiseRouterModifier> modifiers) {
        List<DensityFunction> orderedFunctions = modifiers.stream()
            .filter(modifier -> modifier.target == target)
            .sorted(Comparator.comparingInt(WrapNoiseRouterModifier::priority))
            .map(modifier -> modifier.wrapperFunction().value())
            .toList();

        if (orderedFunctions.isEmpty()) return wrapped;

        DensityFunction mergedFunction = wrapped;
        for (DensityFunction function : orderedFunctions) {
            mergedFunction = DensityFunctionWrapper.wrap(mergedFunction, function, router);
        }

        return mergedFunction;
    }

    public enum Target implements StringRepresentable {
        BARRIER("barrier", NoiseRouter::barrierNoise),
        FLUID_LEVEL_FLOODEDNESS("fluid_level_floodedness", NoiseRouter::fluidLevelFloodednessNoise),
        FLUID_LEVEL_SPREAD("fluid_level_spread", NoiseRouter::fluidLevelSpreadNoise),
        LAVA("lava", NoiseRouter::lavaNoise),
        TEMPERATURE("temperature", NoiseRouter::temperature),
        VEGETATION("vegetation", NoiseRouter::vegetation),
        CONTINENTS("continents", NoiseRouter::continents),
        EROSION("erosion", NoiseRouter::erosion),
        DEPTH("depth", NoiseRouter::depth),
        RIDGES("ridges", NoiseRouter::ridges),
        INITIAL_DENSITY("initial_density_without_jaggedness", NoiseRouter::initialDensityWithoutJaggedness),
        FINAL_DENSITY("final_density", NoiseRouter::finalDensity),
        VEIN_TOGGLE("vein_toggle", NoiseRouter::veinToggle),
        VEIN_RIDGED("vein_ridged", NoiseRouter::veinRidged),
        VEIN_GAP("vein_gap", NoiseRouter::veinGap);

        public static final Codec<Target> CODEC = StringRepresentable.fromEnum(Target::values);
        private final String name;
        private final Function<NoiseRouter, DensityFunction> getter;

        Target(String name, Function<NoiseRouter, DensityFunction> getter) {
            this.name = name;
            this.getter = getter;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public DensityFunction get(NoiseRouter router) {
            return this.getter.apply(router);
        }
    }
}
