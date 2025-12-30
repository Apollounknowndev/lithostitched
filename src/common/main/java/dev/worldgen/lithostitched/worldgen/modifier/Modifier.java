package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.function.Function;

/**
 * The interface used for applying worldgen modifiers.
 *
 * @author Apollo
 */
public interface Modifier {
    @SuppressWarnings("unchecked")
    Codec<Modifier> CODEC = Codec.lazyInitialized(() -> {
        var modifierRegistry = BuiltInRegistries.REGISTRY.getOptional(LithostitchedRegistryKeys.MODIFIER_TYPE.identifier());
        if (modifierRegistry.isEmpty()) throw new NullPointerException("Worldgen modifier registry does not exist yet!");
        return ((Registry<MapCodec<? extends Modifier>>) modifierRegistry.get()).byNameCodec();
    }).dispatch(Modifier::codec, Function.identity());

    MapCodec<Integer> PRIORITY_DEFAULT = Codec.INT.optionalFieldOf("priority", 1000);
    MapCodec<Integer> PRIORITY_REMOVE = Codec.INT.optionalFieldOf("priority", 2000);

    default void applyModifier(RegistryAccess registryAccess) {
        this.applyModifier();
    }

    void applyModifier();

    int priority();

    MapCodec<? extends Modifier> codec();

    default boolean internal$modifiesFabricFeatures() {
        return false;
    }

    static <T> void resetRegistrationInfo(Registry<T> registry, Holder<T> holder) {
        if (holder.unwrapKey().isPresent()) {
            ResourceKey<T> key = holder.unwrapKey().get();
            Optional<RegistrationInfo> knownPackInfo = registry.registrationInfo(key);
            knownPackInfo.ifPresent(registrationInfo -> ((MappedRegistryAccessor<T>)registry).lithostitched$getRegistrationInfos().put(key, new RegistrationInfo(Optional.empty(), registrationInfo.lifecycle())));
        }
    }
}
