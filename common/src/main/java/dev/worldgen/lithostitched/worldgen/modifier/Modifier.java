package dev.worldgen.lithostitched.worldgen.modifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.mixin.common.ChunkGeneratorAccessor;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.dimension.LevelStem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * The interface used for applying worldgen modifiers.
 *
 * @author Apollo
 */
public interface Modifier {
    @SuppressWarnings("unchecked")
    Codec<Modifier> CODEC = Codec.lazyInitialized(() -> {
        var modifierRegistry = BuiltInRegistries.REGISTRY.get(LithostitchedRegistryKeys.MODIFIER_TYPE.location());
        if (modifierRegistry == null) throw new NullPointerException("Worldgen modifier registry does not exist yet!");
        return ((Registry<MapCodec<? extends Modifier>>) modifierRegistry).byNameCodec();
    }).dispatch(Modifier::codec, Function.identity());

    default void applyModifier(RegistryAccess registryAccess) {
        this.applyModifier();
    }

    void applyModifier();

    ModifierPhase getPhase();

    MapCodec<? extends Modifier> codec();

    // Apply all worldgen modifiers in the worldgen modifier registry
    static void applyModifiers(MinecraftServer server) {
        boolean fabricFeaturesModified = false;
        RegistryAccess registries = server.registryAccess();
        HolderLookup.RegistryLookup<Modifier> modifiers = registries.lookupOrThrow(LithostitchedRegistryKeys.WORLDGEN_MODIFIER);


        for (ModifierPhase phase : ModifierPhase.values()) {
            if (phase == ModifierPhase.NONE) continue;
            List<Holder.Reference<Modifier>> phaseModifiers = modifiers.listElements().filter(m -> m.value().getPhase() == phase).toList();
            applyPhaseModifiers(registries, phaseModifiers);

            if (!phaseModifiers.stream().filter(holder -> holder.value().internal$modifiesFabricFeatures()).toList().isEmpty()) {
                fabricFeaturesModified = true;
            }
        }

        if (fabricFeaturesModified) {
            Registry<LevelStem> dimensions = registries.registryOrThrow(Registries.LEVEL_STEM);
            for (LevelStem dimension : dimensions) {
                var accessor = ((ChunkGeneratorAccessor)dimension.generator());
                BiomeSource source = accessor.getBiomeSource();
                accessor.setFeaturesPerStep(
                        Suppliers.memoize(() -> FeatureSorter.buildFeaturesPerStep(List.copyOf(source.possibleBiomes()), biome -> accessor.getGetter().apply(biome).features(), true))
                );
            }
        }
    }

    private static void applyPhaseModifiers(RegistryAccess registries, List<Holder.Reference<Modifier>> phaseModifiers) {
        List<Holder.Reference<PriorityBasedModifier>> priorityBasedModifiers = new ArrayList<>();

        for (Holder.Reference<Modifier> reference : phaseModifiers) {
            if (reference.value() instanceof PriorityBasedModifier) {
                // Yucky cast, but fully safe
                priorityBasedModifiers.add((Holder.Reference<PriorityBasedModifier>)(Object)reference);
            } else {
                LithostitchedCommon.debug("Applying modifier with id: {}", reference.key().location());
                reference.value().applyModifier(registries);
            }
        }

        for (Holder.Reference<PriorityBasedModifier> reference : sortByPriority(priorityBasedModifiers)) {
            LithostitchedCommon.debug("Applying modifier with id: {}", reference.key().location());
            reference.value().applyModifier(registries);
        }
    }

    static List<Holder.Reference<PriorityBasedModifier>> sortByPriority(List<Holder.Reference<PriorityBasedModifier>> modifiers) {
        return modifiers.stream().sorted(Comparator.comparingInt(reference -> reference.value().getPriority())).toList();
    }

    default boolean internal$modifiesFabricFeatures() {
        return false;
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
