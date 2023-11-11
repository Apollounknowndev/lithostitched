package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Class containing the resource keys of every registry registered by Lithostitched.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LithostitchedRegistries {
	public static final ResourceKey<Registry<Modifier>> WORLDGEN_MODIFIER = createRegistryKey("worldgen_modifier");
	public static final ResourceKey<Registry<Codec<? extends Modifier>>> MODIFIER_TYPE = createRegistryKey("modifier_type");
	public static final ResourceKey<Registry<Codec<? extends ModifierPredicate>>> MODIFIER_PREDICATE_TYPE = createRegistryKey("modifier_predicate_type");

	private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
		return ResourceKey.createRegistryKey(new ResourceLocation(LithostitchedCommon.MOD_ID, name));
	}
}
