package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.util.DensityFunctionWrapper;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class WrapNoiseRouterModifier extends Modifier {
    public static final Codec<WrapNoiseRouterModifier> CODEC = RecordCodecBuilder.create(instance -> addModifierFields(instance).and(instance.group(
        PriorityBasedModifier.PRIORITY_CODEC.forGetter(WrapNoiseRouterModifier::priority),
        Target.CODEC.fieldOf("target").forGetter(WrapNoiseRouterModifier::target),
        DensityFunction.CODEC.fieldOf("wrapper_function").forGetter(WrapNoiseRouterModifier::wrapperFunction)
    )).apply(instance, WrapNoiseRouterModifier::new));
    private final int priority;
    private final Target target;
    private final Holder<DensityFunction> wrapperFunction;

    public WrapNoiseRouterModifier(ModifierPredicate predicate, int priority, Target target, Holder<DensityFunction> wrapperFunction) {
        super(predicate, ModifierPhase.MODIFY);
        this.priority = priority;
        this.target = target;
        this.wrapperFunction = wrapperFunction;
    }

    @Override
    public void applyModifier() {
    }

    @Override
    public Codec<? extends Modifier> codec() {
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

    public int priority() {
        return priority;
    }

    public Target target() {
        return target;
    }

    public Holder<DensityFunction> wrapperFunction() {
        return wrapperFunction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (WrapNoiseRouterModifier) obj;
        return this.priority == that.priority &&
                Objects.equals(this.target, that.target) &&
                Objects.equals(this.wrapperFunction, that.wrapperFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, target, wrapperFunction);
    }

    @Override
    public String toString() {
        return "WrapNoiseRouterModifier[" +
                "priority=" + priority + ", " +
                "target=" + target + ", " +
                "wrapperFunction=" + wrapperFunction + ']';
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
