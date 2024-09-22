package dev.worldgen.lithostitched.worldgen.modifier;

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
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public record WrapNoiseRouterModifier(int priority, ResourceKey<Level> dimension, Target target, Holder<DensityFunction> wrapperFunction) implements Modifier {
    public static final MapCodec<WrapNoiseRouterModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("priority", 1000).forGetter(WrapNoiseRouterModifier::priority),
        ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(WrapNoiseRouterModifier::dimension),
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

    public static DensityFunction modifyDensityFunction(Target target, DensityFunction wrapped, List<WrapNoiseRouterModifier> modifiers) {
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

    public enum Target implements StringRepresentable {
        BARRIER("barrier"),
        FLUID_LEVEL_FLOODEDNESS("fluid_level_floodedness"),
        FLUID_LEVEL_SPREAD("fluid_level_spread"),
        LAVA("lava"),
        TEMPERATURE("temperature"),
        VEGETATION("vegetation"),
        CONTINENTS("continents"),
        EROSION("erosion"),
        DEPTH("depth"),
        RIDGES("ridges"),
        INITIAL_DENSITY("initial_density_without_jaggedness"),
        FINAL_DENSITY("final_density"),
        VEIN_TOGGLE("vein_toggle"),
        VEIN_RIDGED("vein_ridged"),
        VEIN_GAP("vein_gap");

        public static final Codec<Target> CODEC = StringRepresentable.fromEnum(Target::values);
        private final String name;

        Target(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
