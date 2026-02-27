package dev.worldgen.lithostitched.api.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.function.Function;

/**
 * The root interface of a worldgen modifier type.
 */
public interface WorldgenModifier {
	Codec<WorldgenModifier> CODEC = LithostitchedRegistries.MODIFIER_TYPE.byNameCodec().dispatch(WorldgenModifier::codec, Function.identity());
	Integer DEFAULT_PRIORITY = 1000;
	Integer REMOVAL_PRIORITY = 2000;
	
	MapCodec<Integer> PRIORITY_DEFAULT = Codec.INT.optionalFieldOf("priority", 1000);
	MapCodec<Integer> PRIORITY_REMOVE = Codec.INT.optionalFieldOf("priority", 2000);
	
	void apply(RegistryAccess registries);
	int priority();
	MapCodec<? extends WorldgenModifier> codec();
	
	default boolean shouldRecompileSortedFeatures() {
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
