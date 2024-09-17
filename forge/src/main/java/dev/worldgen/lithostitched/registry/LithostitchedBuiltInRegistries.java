package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.structure.condition.StructureCondition;
import dev.worldgen.lithostitched.worldgen.surface.LithostitchedSurfaceRules;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static dev.worldgen.lithostitched.LithostitchedCommon.createResourceKey;

/**
 * Built-in registries for Lithostitched on Forge.
 */
public final class LithostitchedBuiltInRegistries {
	private static final DeferredRegister<Codec<? extends Modifier>> DEFERRED_MODIFIER_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.MODIFIER_TYPE, LithostitchedCommon.MOD_ID);
	public static final Supplier<IForgeRegistry<Codec<? extends Modifier>>> MODIFIER_TYPE = DEFERRED_MODIFIER_TYPES.makeRegistry(() -> new RegistryBuilder<Codec<? extends Modifier>>().hasTags().disableSync().disableSaving());

	private static final DeferredRegister<Codec<? extends ModifierPredicate>> DEFERRED_MODIFIER_PREDICATES_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.MODIFIER_PREDICATE_TYPE, LithostitchedCommon.MOD_ID);
	public static final Supplier<IForgeRegistry<Codec<? extends ModifierPredicate>>> MODIFIER_PREDICATE_TYPE = DEFERRED_MODIFIER_PREDICATES_TYPES.makeRegistry(() -> new RegistryBuilder<Codec<? extends ModifierPredicate>>().hasTags().disableSync().disableSaving());

	private static final DeferredRegister<MapCodec<? extends StructureCondition>> DEFERRED_STRUCTURE_CONDITION_TYPES = DeferredRegister.create(LithostitchedRegistryKeys.STRUCTURE_CONDITION_TYPE, LithostitchedCommon.MOD_ID);
	public static final Supplier<IForgeRegistry<MapCodec<? extends StructureCondition>>> STRUCTURE_CONDITION_TYPE = DEFERRED_STRUCTURE_CONDITION_TYPES.makeRegistry(() -> new RegistryBuilder<MapCodec<? extends StructureCondition>>().hasTags().disableSync().disableSaving());

	private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "lithostitched");

	public static void init(IEventBus bus) {

		bus.addListener((RegisterEvent event) -> {
			event.register(Registries.MATERIAL_RULE, helper -> helper.register("transient_merged", LithostitchedSurfaceRules.TransientMergedRuleSource.CODEC.codec()));

			LithostitchedCommon.registerCommonFeatureTypes((name, feature) -> register(event, Registries.FEATURE, name, feature));
			LithostitchedCommon.registerCommonPoolElementTypes((name, type) -> register(event, Registries.STRUCTURE_POOL_ELEMENT, name, type));
			LithostitchedCommon.registerCommonStructureTypes((name, type) -> register(event, Registries.STRUCTURE_TYPE, name, type));
			LithostitchedCommon.registerCommonStructureProcessors((name, type) -> register(event, Registries.STRUCTURE_PROCESSOR, name, type));
			LithostitchedCommon.registerCommonRuleTests((name, type) -> register(event, Registries.RULE_TEST, name, type));
			LithostitchedCommon.registerCommonBlockEntityModifiers((name, type) -> register(event, Registries.RULE_BLOCK_ENTITY_MODIFIER, name, type));
		});

		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			event.dataPackRegistry(LithostitchedRegistryKeys.WORLDGEN_MODIFIER, Modifier.CODEC);
		});

		registerForgeBiomeModifiers((name, codec) -> BIOME_MODIFIER_SERIALIZERS.register(name, () -> codec));
		BIOME_MODIFIER_SERIALIZERS.register(bus);

		LithostitchedCommon.registerCommonModifiers((name, codec) -> DEFERRED_MODIFIER_TYPES.register(name, () -> codec));
		registerForgeModifiers((name, codec) -> DEFERRED_MODIFIER_TYPES.register(name, () -> codec));
		DEFERRED_MODIFIER_TYPES.register(bus);

		LithostitchedCommon.registerCommonModifierPredicates((name, codec) -> DEFERRED_MODIFIER_PREDICATES_TYPES.register(name, () -> codec));
		DEFERRED_MODIFIER_PREDICATES_TYPES.register(bus);

		LithostitchedCommon.registerCommonStructureConditions((name, codec) -> DEFERRED_STRUCTURE_CONDITION_TYPES.register(name, () -> codec));
		DEFERRED_STRUCTURE_CONDITION_TYPES.register(bus);
	}

	private static <T> void register(RegisterEvent event, ResourceKey<Registry<T>> registry, String name, T object) {
		event.register(registry, helper -> helper.register(createResourceKey(registry, name), object));
	}

	public static void registerForgeModifiers(BiConsumer<String, Codec<? extends Modifier>> consumer) {
		consumer.accept("add_biome_spawns", AddBiomeSpawnsModifier.CODEC);
		consumer.accept("add_features", AddFeaturesModifier.CODEC);
		consumer.accept("remove_biome_spawns", RemoveBiomeSpawnsModifier.CODEC);
		consumer.accept("remove_features", RemoveFeaturesModifier.CODEC);
		consumer.accept("replace_climate", ReplaceClimateModifier.CODEC);
		consumer.accept("replace_effects", ReplaceEffectsModifier.CODEC);
	}

	public static void registerForgeBiomeModifiers(BiConsumer<String, Codec<? extends BiomeModifier>> consumer) {
		consumer.accept("replace_climate", LithostitchedForgeBiomeModifiers.ReplaceClimateBiomeModifier.CODEC);
		consumer.accept("replace_effects", LithostitchedForgeBiomeModifiers.ReplaceEffectsBiomeModifier.CODEC);
	}
}
