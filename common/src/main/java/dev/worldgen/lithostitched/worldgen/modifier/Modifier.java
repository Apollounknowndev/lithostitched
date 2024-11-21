package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.TrueModifierPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The interface used for applying worldgen modifiers.
 *
 * @author Apollo
 */
public interface Modifier {
    @SuppressWarnings("unchecked")
    Codec<Modifier> CODEC = ExtraCodecs.lazyInitializedCodec(() -> {
        var modifierRegistry = BuiltInRegistries.REGISTRY.get(LithostitchedRegistryKeys.MODIFIER_TYPE.location());
        if (modifierRegistry == null) throw new NullPointerException("Worldgen modifier registry does not exist yet!");
        return ((Registry<Codec<? extends Modifier>>) modifierRegistry).byNameCodec();
    }).dispatch(Modifier::codec, Function.identity());

    static <P extends Modifier> Products.P1<RecordCodecBuilder.Mu<P>, ModifierPredicate> addModifierFields(RecordCodecBuilder.Instance<P> codec) {
        return codec.group(ModifierPredicate.CODEC.fieldOf("predicate").orElse(TrueModifierPredicate.INSTANCE).forGetter(Modifier::getPredicate));
    }

    ModifierPredicate getPredicate();

    ModifierPhase getPhase();

    default void applyModifier(RegistryAccess registryAccess) {
        this.applyModifier();
    }

    void applyModifier();

    Codec<? extends Modifier> codec();

    // Apply all worldgen modifiers in the worldgen modifier registry
    static void applyModifiers(MinecraftServer server) {
        RegistryAccess registries = server.registryAccess();
        Registry<Modifier> modifiers = registries.registryOrThrow(LithostitchedRegistryKeys.WORLDGEN_MODIFIER);
        for (ModifierPhase phase : ModifierPhase.values()) {
            if (phase == ModifierPhase.NONE) continue;
            List<Modifier> phaseModifiers = modifiers.stream().filter(modifier -> modifier.getPhase() == phase).toList();
            applyPhaseModifiers(registries, phaseModifiers);
        }
    }

    private static void applyPhaseModifiers(RegistryAccess registries, List<Modifier> phaseModifiers) {
        List<PriorityBasedModifier> priorityBasedModifiers = new ArrayList<>();
        for (Modifier modifier : phaseModifiers) {
            if (modifier instanceof PriorityBasedModifier priorityModifier) {
                priorityBasedModifiers.add(priorityModifier);
            } else {
                modifier.applyModifier(registries);
            }
        }
        for (Modifier modifier : sortByPriority(priorityBasedModifiers)) {
            modifier.applyModifier(registries);
        }
    }
    static List<PriorityBasedModifier> sortByPriority(List<PriorityBasedModifier> modifiers) {
        return modifiers.stream().sorted(Comparator.comparingInt(PriorityBasedModifier::getPriority)).toList();
    }

    enum ModifierPhase implements StringRepresentable {
        /**
         * Phase for modifiers to never apply.
         * Useful for modifiers that don't use the regular modifier system for applying modifications, like Forge biome modifiers and the AddSurfaceRule modifier.
         */
        NONE("none"),

        /**
         * Phase for modifiers that need to run before any other steps.
         */
        BEFORE_ALL("before_all"),

        /**
         * Phase for modifiers that replace parts of worldgen such as pool alias bindings.
         */
        REPLACE("replace"),

        /**
         * Phase for modifiers that add to worldgen, such as template pool and structure set additions.
         */
        ADD("add"),

        /**
         * Phase for modifiers that remove from worldgen, such as feature and mob spawn removals.
         */
        REMOVE("remove"),

        /**
         * Phase for modifiers that modify parts of worldgen such as placed feature redirections.
         */
        MODIFY("modify"),

        /**
         * Phase for modifiers that need to run after all other steps.
         */
        AFTER_ALL("after_all");

        private final String name;

        ModifierPhase(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

}
