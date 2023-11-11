package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.TrueModifierPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The interface used for applying worldgen modifiers.
 *
 * @author Apollo
 */
public abstract class Modifier {
    @SuppressWarnings("unchecked")
    public static final Codec<Modifier> CODEC = ExtraCodecs.lazyInitializedCodec(() -> {
        var modifierRegistry = BuiltInRegistries.REGISTRY.get(LithostitchedRegistries.MODIFIER_TYPE.location());
        if (modifierRegistry == null) throw new NullPointerException("Worldgen modifier registry does not exist yet!");
        return ((Registry<Codec<? extends Modifier>>) modifierRegistry).byNameCodec();
    }).dispatch(Modifier::codec, Function.identity());

    private final ModifierPredicate predicate;
    private final ModifierPhase phase;

    protected Modifier(ModifierPredicate modifierPredicate, ModifierPhase phase) {
        this.predicate = modifierPredicate;
        this.phase = phase;
    }

    public static <P extends Modifier> Products.P1<RecordCodecBuilder.Mu<P>, ModifierPredicate> addModifierFields(RecordCodecBuilder.Instance<P> codec) {
        return codec.group(ModifierPredicate.CODEC.fieldOf("predicate").orElse(TrueModifierPredicate.INSTANCE).forGetter(Modifier::predicate));
    }

    public ModifierPredicate predicate() {
        return this.predicate;
    }
    public ModifierPhase phase() {
        return this.phase;
    }
    public abstract void applyModifier();

    public abstract Codec<? extends Modifier> codec();

    // Apply all worldgen modifiers in the worldgen modifier registry
    public static void applyModifiers(MinecraftServer server) {
        Registry<Modifier> modifiers = server.registryAccess().registryOrThrow(LithostitchedRegistries.WORLDGEN_MODIFIER);
        for (ModifierPhase phase : ModifierPhase.values()) {
            if (phase == ModifierPhase.NONE) continue;
            for (Modifier modifier : modifiers.stream().filter(modifier -> modifier.phase() == phase).collect(Collectors.toSet())) {
                if (modifier.predicate().test()) {
                    modifier.applyModifier();
                }
            }
        }
    }

    public enum ModifierPhase implements StringRepresentable {
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
         * Phase for modifiers that add to worldgen, such as template pool and structure set additions.
         */
        ADD("add"),

        /**
         * Phase for modifiers that remove from worldgen, such as feature and mob spawn removals.
         */
        REMOVE("remove"),

        /**
         * Phase for modifiers that replace/modify parts of worldgen, like climate replacements and placed feature redirections.
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
