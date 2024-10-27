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
public interface LithostitchedRegistryKeys {
	ResourceKey<Registry<Modifier>> WORLDGEN_MODIFIER = create("worldgen_modifier");
	ResourceKey<Registry<Codec<? extends Modifier>>> MODIFIER_TYPE = create("modifier_type");
	ResourceKey<Registry<Codec<? extends ModifierPredicate>>> MODIFIER_PREDICATE_TYPE = create("modifier_predicate_type");
	ResourceKey<Registry<MapCodec<? extends StructureCondition>>> STRUCTURE_CONDITION_TYPE = create("structure_condition_type");

	private static <T> ResourceKey<Registry<T>> create(String name) {
		return ResourceKey.createRegistryKey(LithostitchedCommon.id(name));
	}
}
