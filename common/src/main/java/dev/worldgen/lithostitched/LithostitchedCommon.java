package dev.worldgen.lithostitched;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyAll;
import dev.worldgen.lithostitched.worldgen.blockentitymodifier.ApplyRandom;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.*;
import dev.worldgen.lithostitched.worldgen.processor.ApplyRandomStructureProcessor;
import dev.worldgen.lithostitched.worldgen.processor.BlockSwapStructureProcessor;
import dev.worldgen.lithostitched.worldgen.processor.ReferenceStructureProcessor;
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawStructure;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

/**
 * Class containing core fields and methods used commonly by Lithostitched across mod loaders.
 * <p>Undocumented methods can be considered not API.</p>
 *
 * @author SmellyModder (Luke Tonon)
 */
public final class LithostitchedCommon {
	public static final String MOD_ID = "lithostitched";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private LithostitchedCommon() {}

	public static void init() {}

	public static <T> ResourceKey<T> createResourceKey(ResourceKey<? extends Registry<T>> resourceKey, String name) {
		return ResourceKey.create(resourceKey, new ResourceLocation(MOD_ID, name));
	}

	public static void registerCommonModifiers(BiConsumer<String, Codec<? extends Modifier>> consumer) {
		consumer.accept("no_op", NoOpModifier.CODEC);
		consumer.accept("add_structure_set_entries", AddStructureSetEntriesModifier.CODEC);
		consumer.accept("remove_structures_from_structure_set", RemoveStructuresFromStructureSetModifier.CODEC);
		consumer.accept("add_surface_rule", AddSurfaceRuleModifier.CODEC);
		consumer.accept("add_template_pool_elements", AddTemplatePoolElementsModifier.CODEC);
		consumer.accept("redirect_feature", RedirectFeatureModifier.CODEC);
	}

	public static void registerCommonModifierPredicates(BiConsumer<String, Codec<? extends ModifierPredicate>> consumer) {
		consumer.accept("all_of", AllOfModifierPredicate.CODEC);
		consumer.accept("any_of", AnyOfModifierPredicate.CODEC);
		consumer.accept("mod_loaded", ModLoadedModifierPredicate.CODEC);
		consumer.accept("not", NotModifierPredicate.CODEC);
		consumer.accept("true", TrueModifierPredicate.CODEC);
	}

	public static void registerCommonStructureTypes(BiConsumer<String, Codec<? extends Structure>> consumer) {
		consumer.accept("jigsaw", AlternateJigsawStructure.CODEC);
	}

	public static void registerCommonStructureProcessors(BiConsumer<String, Codec<? extends StructureProcessor>> consumer) {
		consumer.accept("reference", ReferenceStructureProcessor.CODEC);
		consumer.accept("apply_random", ApplyRandomStructureProcessor.CODEC);
		consumer.accept("block_swap", BlockSwapStructureProcessor.CODEC);
	}

	public static void registerCommonBlockEntityModifiers(BiConsumer<String, Codec<? extends RuleBlockEntityModifier>> consumer) {
		consumer.accept("apply_random", ApplyRandom.CODEC);
		consumer.accept("apply_all", ApplyAll.CODEC);
	}
}
