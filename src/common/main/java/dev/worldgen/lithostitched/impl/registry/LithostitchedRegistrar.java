package dev.worldgen.lithostitched.impl.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.msrandom.multiplatform.annotations.Expect;

import java.util.Map;

public class LithostitchedRegistrar {
	@Expect
	public static <T> void registerRegistry(ResourceKey<Registry<T>> key, Codec<T> codec);
	
	@Expect
	public static <T> void register(ResourceKey<Registry<T>> key, Map<String, T> entries);
}
