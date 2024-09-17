package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.structure.condition.StructureCondition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Class containing the resource keys of every registry registered by Lithostitched.
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LithostitchedRegistryKeys {
	public static final ResourceKey<Registry<Modifier>> WORLDGEN_MODIFIER = createRegistryKey("worldgen_modifier");
	public static final ResourceKey<Registry<Codec<? extends Modifier>>> MODIFIER_TYPE = createRegistryKey("modifier_type");
	public static final ResourceKey<Registry<Codec<? extends ModifierPredicate>>> MODIFIER_PREDICATE_TYPE = createRegistryKey("modifier_predicate_type");
	public static final ResourceKey<Registry<MapCodec<? extends StructureCondition>>> STRUCTURE_CONDITION_TYPE = createRegistryKey("structure_condition_type");

	private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
		return ResourceKey.createRegistryKey(LithostitchedCommon.id(name));
	}
}
