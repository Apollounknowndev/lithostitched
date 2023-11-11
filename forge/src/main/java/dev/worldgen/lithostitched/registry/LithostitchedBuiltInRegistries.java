package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.surface.LithostitchedSurfaceRules;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Built-in registries for Lithostitched on Forge.
 */
public final class LithostitchedBuiltInRegistries {
	private static final DeferredRegister<Codec<? extends Modifier>> DEFERRED_MODIFIER_TYPES = DeferredRegister.create(LithostitchedRegistries.MODIFIER_TYPE, LithostitchedCommon.MOD_ID);
	public static final Supplier<IForgeRegistry<Codec<? extends Modifier>>> MODIFIER_TYPE = DEFERRED_MODIFIER_TYPES.makeRegistry(() -> new RegistryBuilder<Codec<? extends Modifier>>().hasTags().disableSync().disableSaving());

	private static final DeferredRegister<Codec<? extends ModifierPredicate>> DEFERRED_MODIFIER_PREDICATES_TYPES = DeferredRegister.create(LithostitchedRegistries.MODIFIER_PREDICATE_TYPE, LithostitchedCommon.MOD_ID);
	public static final Supplier<IForgeRegistry<Codec<? extends ModifierPredicate>>> MODIFIER_PREDICATE_TYPE = DEFERRED_MODIFIER_PREDICATES_TYPES.makeRegistry(() -> new RegistryBuilder<Codec<? extends ModifierPredicate>>().hasTags().disableSync().disableSaving());

	private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "lithostitched");
	public static void init(IEventBus bus) {

		bus.addListener((RegisterEvent event) -> {
			event.register(Registries.MATERIAL_RULE, (helper) -> {
				helper.register("transient_merged", LithostitchedSurfaceRules.TransientMergedRuleSource.CODEC.codec());
			});
		});

		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			event.dataPackRegistry(LithostitchedRegistries.WORLDGEN_MODIFIER, Modifier.CODEC);
		});

		registerForgeBiomeModifiers((name, codec) -> BIOME_MODIFIER_SERIALIZERS.register(name, () -> codec));
		BIOME_MODIFIER_SERIALIZERS.register(bus);

		LithostitchedCommon.registerCommonModifiers((name, codec) -> DEFERRED_MODIFIER_TYPES.register(name, () -> codec));
		registerForgeModifiers((name, codec) -> DEFERRED_MODIFIER_TYPES.register(name, () -> codec));
		DEFERRED_MODIFIER_TYPES.register(bus);

		LithostitchedCommon.registerCommonModifierPredicates((name, codec) -> DEFERRED_MODIFIER_PREDICATES_TYPES.register(name, () -> codec));
		DEFERRED_MODIFIER_PREDICATES_TYPES.register(bus);

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
