package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.densityfunction.WrappedMarkerDensityFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

/**
 * {
 *     "type": "lithostitched:wrap_noise_router",
 *     "priority": 600 (optional field),
 *     "target": "continents",
 *     "modifier: {
 *         "type": "minecraft:mul",
 *         "argument1": "lithostitched:input",
 *         "argument2": 2
 *     }
 * }
 */
public record WrapNoiseRouterModifier(int priority, Target target, DensityFunction modifier) implements Modifier {
    private static final ResourceKey<DensityFunction> INPUT_MARKER = LithostitchedCommon.createResourceKey(Registries.DENSITY_FUNCTION, "wrapped_marker");
    public static final MapCodec<WrapNoiseRouterModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("priority", 1000).forGetter(WrapNoiseRouterModifier::priority),
        Target.CODEC.fieldOf("target").forGetter(WrapNoiseRouterModifier::target),
        DensityFunction.DIRECT_CODEC.fieldOf("modifier").forGetter(WrapNoiseRouterModifier::modifier)
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

    public static DensityFunction modifyDensityFunction(Target target, DensityFunction original, Registry<Modifier> registry) {
        List<DensityFunction> orderedFunctions = registry.stream()
            .filter(modifier -> modifier instanceof WrapNoiseRouterModifier routerModifier && routerModifier.target == target)
            .map(modifier -> (WrapNoiseRouterModifier) modifier)
            .sorted(Comparator.comparingInt(WrapNoiseRouterModifier::priority))
            .map(WrapNoiseRouterModifier::modifier)
            .toList();

        if (orderedFunctions.isEmpty()) return original;

        DensityFunction mergedFunction = original;
        for (DensityFunction function : orderedFunctions) {
            final DensityFunction input = mergedFunction;
            mergedFunction = function.mapAll(value -> {
                if (value instanceof DensityFunctions.HolderHolder holderHolder && holderHolder.function().value() instanceof WrappedMarkerDensityFunction)
                    return input;
                return value;
            });
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

        private final String name;
        public static final Codec<Target> CODEC = StringRepresentable.fromEnum(Target::values);

        Target(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
