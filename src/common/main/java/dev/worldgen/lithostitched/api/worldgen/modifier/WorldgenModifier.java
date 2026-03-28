package dev.worldgen.lithostitched.api.worldgen.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedBuiltInRegistries;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.util.InjectionType;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeClimate;
import dev.worldgen.lithostitched.api.worldgen.util.BiomeEffects;
import dev.worldgen.lithostitched.api.worldgen.util.WeightedSpawnerData;
import dev.worldgen.lithostitched.impl.worldgen.modifier.*;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import dev.worldgen.lithostitched.api.worldgen.util.NoiseRouterTarget;
import dev.worldgen.lithostitched.worldgen.feature.config.CompositeConfig;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.core.HolderSet.direct;

/**
 * The root interface of a worldgen modifier.
 * <p>
 * Worldgen modifiers are applied right before levels are created and worldgen can begin, but after the server starting events on both loaders.
 *
 */
public interface WorldgenModifier {
	Codec<WorldgenModifier> CODEC = LithostitchedBuiltInRegistries.MODIFIER_TYPE.byNameCodec().dispatch(WorldgenModifier::codec, Function.identity());
	Integer DEFAULT_PRIORITY = 1000;
	Integer REMOVAL_PRIORITY = 2000;
	
	MapCodec<Integer> PRIORITY_DEFAULT_CODEC = Codec.INT.optionalFieldOf("priority", 1000);
	MapCodec<Integer> PRIORITY_REMOVE_CODEC = Codec.INT.optionalFieldOf("priority", 2000);
	
	Optional<LoadPredicate> predicate();
	int priority();
	void apply(RegistryAccess registries);
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
	
	// Builder for built-in modifier types.
	
	static ModifierBuilder builder() {
		return new ModifierBuilder(Optional.empty());
	}
	
	static ModifierBuilder builder(LoadPredicate predicate) {
		return new ModifierBuilder(Optional.of(predicate));
	}
	
	@SuppressWarnings("unused")
	class ModifierBuilder {
		private final Optional<LoadPredicate> predicate;
		private Optional<Integer> priority = Optional.empty();
		
		private ModifierBuilder(Optional<LoadPredicate> predicate) {
			this.predicate = predicate;
		}
		
		public ModifierBuilder priority(int priority) {
			this.priority = Optional.of(priority);
			return this;
		}
		
		public WorldgenModifier addBiomeSpawns(Holder<Biome> biome, WeightedSpawnerData... spawns) {
			return new AddBiomeSpawnsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(biome), List.of(spawns));
		}
		
		public WorldgenModifier addBiomeSpawns(HolderSet<Biome> biomes, WeightedSpawnerData... spawns) {
			return new AddBiomeSpawnsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), biomes, List.of(spawns));
		}
		
		public WorldgenModifier addFeatures(Holder<Biome> biome, Holder<PlacedFeature> feature, GenerationStep.Decoration step) {
			return new AddFeaturesModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(biome), direct(feature), step);
		}
		
		public WorldgenModifier addFeatures(HolderSet<Biome> biomes, Holder<PlacedFeature> feature, GenerationStep.Decoration step) {
			return new AddFeaturesModifier(predicate, priority.orElse(DEFAULT_PRIORITY), biomes, direct(feature), step);
		}
		
		public WorldgenModifier addFeatures(Holder<Biome> biome, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) {
			return new AddFeaturesModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(biome), features, step);
		}
		
		public WorldgenModifier addFeatures(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) {
			return new AddFeaturesModifier(predicate, priority.orElse(DEFAULT_PRIORITY), biomes, features, step);
		}
		
		public WorldgenModifier addProcessorListProcessors(Holder<StructureProcessorList> list, StructureProcessor... processors) {
			return new AddProcessorListProcessorsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(list), new StructureProcessorList(List.of(processors)));
		}
		
		public WorldgenModifier addProcessorListProcessors(HolderSet<StructureProcessorList> lists, StructureProcessor... processors) {
			return new AddProcessorListProcessorsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), lists, new StructureProcessorList(List.of(processors)));
		}
		
		public WorldgenModifier addStructureSetEntries(Holder<StructureSet> set, StructureSelectionEntry... entries) {
			return new AddStructureSetEntriesModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(set), List.of(entries));
		}
		
		public WorldgenModifier addStructureSetEntries(HolderSet<StructureSet> sets, StructureSelectionEntry... entries) {
			return new AddStructureSetEntriesModifier(predicate, priority.orElse(DEFAULT_PRIORITY), sets, List.of(entries));
		}
		
		@SafeVarargs
		public final WorldgenModifier addTemplatePoolElements(Holder<StructureTemplatePool> pool, Pair<StructurePoolElement, Integer>... elements) {
			return new AddTemplatePoolElementsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(pool), List.of(elements));
		}
		
		@SafeVarargs
		public final WorldgenModifier addTemplatePoolElements(HolderSet<StructureTemplatePool> pools, Pair<StructurePoolElement, Integer>... elements) {
			return new AddTemplatePoolElementsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), pools, List.of(elements));
		}
		
		public WorldgenModifier addSurfaceRule(ResourceKey<LevelStem> dimension, InjectionType injectionType, SurfaceRules.RuleSource ruleSource) {
			return new AddSurfaceRuleModifier(predicate, priority.orElse(DEFAULT_PRIORITY), List.of(dimension), injectionType, ruleSource);
		}
		
		public WorldgenModifier addSurfaceRule(List<ResourceKey<LevelStem>> dimensions, InjectionType injectionType, SurfaceRules.RuleSource ruleSource) {
			return new AddSurfaceRuleModifier(predicate, priority.orElse(DEFAULT_PRIORITY), dimensions, injectionType, ruleSource);
		}
		
		public WorldgenModifier noop() {
			return new NoOpModifier();
		}
		
		public WorldgenModifier removeBiomeSpawns(Holder<Biome> biome, Holder<EntityType<?>> mob) {
			return new RemoveBiomeSpawnsModifier(predicate, priority.orElse(REMOVAL_PRIORITY), direct(biome), direct(mob));
		}
		
		public WorldgenModifier removeBiomeSpawns(HolderSet<Biome> biomes, Holder<EntityType<?>> mob) {
			return new RemoveBiomeSpawnsModifier(predicate, priority.orElse(REMOVAL_PRIORITY), biomes, direct(mob));
		}
		
		public WorldgenModifier removeBiomeSpawns(Holder<Biome> biome, HolderSet<EntityType<?>> mobs) {
			return new RemoveBiomeSpawnsModifier(predicate, priority.orElse(REMOVAL_PRIORITY), direct(biome), mobs);
		}
		
		public WorldgenModifier removeBiomeSpawns(HolderSet<Biome> biomes, HolderSet<EntityType<?>> mobs) {
			return new RemoveBiomeSpawnsModifier(predicate, priority.orElse(REMOVAL_PRIORITY), biomes, mobs);
		}
		
		public WorldgenModifier removeFeatures(Holder<Biome> biome, Holder<PlacedFeature> feature, GenerationStep.Decoration step) {
			return new RemoveFeaturesModifier(predicate, priority.orElse(REMOVAL_PRIORITY), direct(biome), direct(feature), step);
		}
		
		public WorldgenModifier removeFeatures(HolderSet<Biome> biomes, Holder<PlacedFeature> feature, GenerationStep.Decoration step) {
			return new RemoveFeaturesModifier(predicate, priority.orElse(REMOVAL_PRIORITY), biomes, direct(feature), step);
		}
		
		public WorldgenModifier removeFeatures(Holder<Biome> biome, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) {
			return new RemoveFeaturesModifier(predicate, priority.orElse(REMOVAL_PRIORITY), direct(biome), features, step);
		}
		
		public WorldgenModifier removeFeatures(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) {
			return new RemoveFeaturesModifier(predicate, priority.orElse(REMOVAL_PRIORITY), biomes, features, step);
		}
		
		@SafeVarargs
		public final WorldgenModifier removeStructureSetEntries(Holder<StructureSet> set, Holder<Structure>... holders) {
			return new RemoveStructureSetEntriesModifier(predicate, priority.orElse(REMOVAL_PRIORITY), direct(set), List.of(holders));
		}
		
		@SafeVarargs
		public final WorldgenModifier removeStructureSetEntries(HolderSet<StructureSet> sets, Holder<Structure>... holders) {
			return new RemoveStructureSetEntriesModifier(predicate, priority.orElse(REMOVAL_PRIORITY), sets, List.of(holders));
		}
		
		public WorldgenModifier replaceClimate(Holder<Biome> biome, BiomeClimate climate) {
			return new ReplaceClimateModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(biome), climate);
		}
		
		public WorldgenModifier replaceClimate(HolderSet<Biome> biomes, BiomeClimate climate) {
			return new ReplaceClimateModifier(predicate, priority.orElse(DEFAULT_PRIORITY), biomes, climate);
		}
		
		public WorldgenModifier replaceEffects(Holder<Biome> biome, BiomeEffects effects) {
			return new ReplaceEffectsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(biome), effects);
		}
		
		public WorldgenModifier replaceEffects(HolderSet<Biome> biomes, BiomeEffects effects) {
			return new ReplaceEffectsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), biomes, effects);
		}
		
		public WorldgenModifier setPoolAliases(Holder<Structure> structure, boolean append, PoolAliasBinding... aliases) {
			return new SetPoolAliasesModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(structure), List.of(aliases), append);
		}
		
		public WorldgenModifier setPoolAliases(HolderSet<Structure> structures, boolean append, PoolAliasBinding... aliases) {
			return new SetPoolAliasesModifier(predicate, priority.orElse(DEFAULT_PRIORITY), structures, List.of(aliases), append);
		}
		
		public WorldgenModifier setPoolElementProcessors(Holder<StructureTemplatePool> pool, Holder<StructureProcessorList> list, boolean append) {
			return new SetPoolElementProcessorsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(pool), Optional.empty(), list, append);
		}
		
		public WorldgenModifier setPoolElementProcessors(Holder<StructureTemplatePool> pool, Holder<StructureProcessorList> list, boolean append, Identifier... ids) {
			return new SetPoolElementProcessorsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(pool), Optional.of(List.of(ids)), list, append);
		}
		
		public WorldgenModifier setPoolElementProcessors(HolderSet<StructureTemplatePool> pools, Holder<StructureProcessorList> list, boolean append) {
			return new SetPoolElementProcessorsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), pools, Optional.empty(), list, append);
		}
		
		public WorldgenModifier setPoolElementProcessors(HolderSet<StructureTemplatePool> pools, Holder<StructureProcessorList> list, boolean append, Identifier... ids) {
			return new SetPoolElementProcessorsModifier(predicate, priority.orElse(DEFAULT_PRIORITY), pools, Optional.of(List.of(ids)), list, append);
		}
		
		public WorldgenModifier setStructureSpawnCondition(Holder<Structure> structure, PlacementCondition spawnCondition, boolean append) {
			return new SetStructureSpawnConditionModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(structure), spawnCondition, append);
		}
		
		public WorldgenModifier setStructureSpawnCondition(HolderSet<Structure> structures, PlacementCondition spawnCondition, boolean append) {
			return new SetStructureSpawnConditionModifier(predicate, priority.orElse(DEFAULT_PRIORITY), structures, spawnCondition, append);
		}
		
		public WorldgenModifier stackFeatures(Holder<ConfiguredFeature<?, ?>> baseFeatures, Holder<PlacedFeature> stackedFeature, CompositeConfig.Type placementType) {
			return new StackFeatureModifier(predicate, priority.orElse(DEFAULT_PRIORITY), direct(baseFeatures), stackedFeature, placementType);
		}
		
		public WorldgenModifier stackFeatures(HolderSet<ConfiguredFeature<?, ?>> baseFeatures, Holder<PlacedFeature> stackedFeature, CompositeConfig.Type placementType) {
			return new StackFeatureModifier(predicate, priority.orElse(DEFAULT_PRIORITY), baseFeatures, stackedFeature, placementType);
		}
		
		public WorldgenModifier wrapDensityFunction(Holder<DensityFunction> target, DensityFunction wrapper) {
			return WrapDensityFunctionModifier.create(predicate, priority.orElse(DEFAULT_PRIORITY), target, Holder.direct(wrapper));
		}
		
		public WorldgenModifier wrapDensityFunction(Holder<DensityFunction> target, Holder<DensityFunction> wrapper) {
			return WrapDensityFunctionModifier.create(predicate, priority.orElse(DEFAULT_PRIORITY), target, wrapper);
		}
		
		public WorldgenModifier wrapNoiseRouter(ResourceKey<Level> dimension, NoiseRouterTarget target, DensityFunction wrapper) {
			return new WrapNoiseRouterModifier(predicate, priority.orElse(DEFAULT_PRIORITY), dimension, target, Holder.direct(wrapper));
		}
		
		public WorldgenModifier wrapNoiseRouter(ResourceKey<Level> dimension, NoiseRouterTarget target, Holder<DensityFunction> wrapper) {
			return new WrapNoiseRouterModifier(predicate, priority.orElse(DEFAULT_PRIORITY), dimension, target, wrapper);
		}
	}
}
