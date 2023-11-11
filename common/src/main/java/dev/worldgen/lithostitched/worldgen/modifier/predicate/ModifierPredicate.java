package dev.worldgen.lithostitched.worldgen.modifier.predicate;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.registry.LithostitchedRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;


public interface ModifierPredicate {
    @SuppressWarnings("unchecked")
    Codec<ModifierPredicate> CODEC = ExtraCodecs.lazyInitializedCodec(() -> {
        var predicateRegistry = BuiltInRegistries.REGISTRY.get(LithostitchedRegistries.MODIFIER_PREDICATE_TYPE.location());
        if (predicateRegistry == null) throw new NullPointerException("Modifier predicate type registry does not exist yet!");
        return ((Registry<Codec<? extends ModifierPredicate>>) predicateRegistry).byNameCodec();
    }).dispatch(ModifierPredicate::codec, Function.identity());

    boolean test();

    Codec<? extends ModifierPredicate> codec();
}

