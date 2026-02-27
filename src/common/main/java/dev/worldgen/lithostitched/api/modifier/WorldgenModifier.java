package dev.worldgen.lithostitched.api.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import dev.worldgen.lithostitched.worldgen.NoiseRouterTarget;
import dev.worldgen.lithostitched.worldgen.feature.config.CompositeConfig;
import dev.worldgen.lithostitched.worldgen.modifier.*;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunction;
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
	
	// Builder for built-in modifier types.
	
	static ModifierBuilder builder() {
		return new ModifierBuilder(Optional.empty());
	}
	
	static ModifierBuilder builder(int priority) {
		return new ModifierBuilder(Optional.of(priority));
	}
	
	class ModifierBuilder {
		private final Optional<Integer> priority;
		
		private ModifierBuilder(Optional<Integer> priority) {
			this.priority = priority;
		}
		
		public WorldgenModifier addProcessorListProcessors(Holder<StructureProcessorList> list, StructureProcessor... processors) {
			return new AddProcessorListProcessorsModifier(priority.orElse(DEFAULT_PRIORITY), direct(list), new StructureProcessorList(List.of(processors)));
		}
		
		public WorldgenModifier addProcessorListProcessors(HolderSet<StructureProcessorList> lists, StructureProcessor... processors) {
			return new AddProcessorListProcessorsModifier(priority.orElse(DEFAULT_PRIORITY), lists, new StructureProcessorList(List.of(processors)));
		}
		
		public WorldgenModifier addStructureSetEntries(Holder<StructureSet> set, StructureSelectionEntry... entries) {
			return new AddStructureSetEntriesModifier(priority.orElse(DEFAULT_PRIORITY), direct(set), List.of(entries));
		}
		
		public WorldgenModifier addStructureSetEntries(HolderSet<StructureSet> sets, StructureSelectionEntry... entries) {
			return new AddStructureSetEntriesModifier(priority.orElse(DEFAULT_PRIORITY), sets, List.of(entries));
		}
		
		public WorldgenModifier addSurfaceRule(ResourceKey<LevelStem> dimension, SurfaceRules.RuleSource ruleSource) {
			return new AddSurfaceRuleModifier(priority.orElse(DEFAULT_PRIORITY), List.of(dimension), ruleSource);
		}
		
		public WorldgenModifier addSurfaceRule(List<ResourceKey<LevelStem>> dimensions, SurfaceRules.RuleSource ruleSource) {
			return new AddSurfaceRuleModifier(priority.orElse(DEFAULT_PRIORITY), dimensions, ruleSource);
		}
		
		@SafeVarargs
		public final WorldgenModifier addTemplatePoolElements(Holder<StructureTemplatePool> pool, Pair<StructurePoolElement, Integer>... elements) {
			return new AddTemplatePoolElementsModifier(priority.orElse(DEFAULT_PRIORITY), direct(pool), List.of(elements));
		}
		
		@SafeVarargs
		public final WorldgenModifier addTemplatePoolElements(HolderSet<StructureTemplatePool> pools, Pair<StructurePoolElement, Integer>... elements) {
			return new AddTemplatePoolElementsModifier(priority.orElse(DEFAULT_PRIORITY), pools, List.of(elements));
		}
		
		@SafeVarargs
		public final WorldgenModifier removeStructureSetEntries(Holder<StructureSet> set, Holder<Structure>... holders) {
			return new RemoveStructureSetEntriesModifier(priority.orElse(REMOVAL_PRIORITY), direct(set), List.of(holders));
		}
		
		@SafeVarargs
		public final WorldgenModifier removeStructureSetEntries(HolderSet<StructureSet> sets, Holder<Structure>... holders) {
			return new RemoveStructureSetEntriesModifier(priority.orElse(REMOVAL_PRIORITY), sets, List.of(holders));
		}
		
		public WorldgenModifier setPoolAliases(Holder<Structure> structure, boolean append, PoolAliasBinding... aliases) {
			return new SetPoolAliasesModifier(priority.orElse(DEFAULT_PRIORITY), direct(structure), List.of(aliases), append);
		}
		
		public WorldgenModifier setPoolAliases(HolderSet<Structure> structures, boolean append, PoolAliasBinding... aliases) {
			return new SetPoolAliasesModifier(priority.orElse(DEFAULT_PRIORITY), structures, List.of(aliases), append);
		}
		
		public WorldgenModifier setPoolElementProcessors(Holder<StructureTemplatePool> pool, Holder<StructureProcessorList> list, boolean append) {
			return new SetPoolElementProcessorsModifier(priority.orElse(DEFAULT_PRIORITY), direct(pool), Optional.empty(), list, append);
		}
		
		public WorldgenModifier setPoolElementProcessors(Holder<StructureTemplatePool> pool, Holder<StructureProcessorList> list, boolean append, Identifier... ids) {
			return new SetPoolElementProcessorsModifier(priority.orElse(DEFAULT_PRIORITY), direct(pool), Optional.of(List.of(ids)), list, append);
		}
		
		public WorldgenModifier setPoolElementProcessors(HolderSet<StructureTemplatePool> pools, Holder<StructureProcessorList> list, boolean append) {
			return new SetPoolElementProcessorsModifier(priority.orElse(DEFAULT_PRIORITY), pools, Optional.empty(), list, append);
		}
		
		public WorldgenModifier setPoolElementProcessors(HolderSet<StructureTemplatePool> pools, Holder<StructureProcessorList> list, boolean append, Identifier... ids) {
			return new SetPoolElementProcessorsModifier(priority.orElse(DEFAULT_PRIORITY), pools, Optional.of(List.of(ids)), list, append);
		}
		
		public WorldgenModifier setStructureSpawnCondition(Holder<Structure> structure, PlacementCondition spawnCondition, boolean append) {
			return new SetStructureSpawnConditionModifier(priority.orElse(DEFAULT_PRIORITY), direct(structure), spawnCondition, append);
		}
		
		public WorldgenModifier setStructureSpawnCondition(HolderSet<Structure> structures, PlacementCondition spawnCondition, boolean append) {
			return new SetStructureSpawnConditionModifier(priority.orElse(DEFAULT_PRIORITY), structures, spawnCondition, append);
		}
		
		public WorldgenModifier stackFeatures(Holder<ConfiguredFeature<?, ?>> baseFeatures, Holder<PlacedFeature> stackedFeature, CompositeConfig.Type placementType) {
			return new StackFeatureModifier(priority.orElse(DEFAULT_PRIORITY), direct(baseFeatures), stackedFeature, placementType);
		}
		
		public WorldgenModifier stackFeatures(HolderSet<ConfiguredFeature<?, ?>> baseFeatures, Holder<PlacedFeature> stackedFeature, CompositeConfig.Type placementType) {
			return new StackFeatureModifier(priority.orElse(DEFAULT_PRIORITY), baseFeatures, stackedFeature, placementType);
		}
		
		public WorldgenModifier wrapDensityFunction(Holder<DensityFunction> target, DensityFunction wrapper) {
			return new WrapDensityFunctionModifier(priority.orElse(DEFAULT_PRIORITY), target, Holder.direct(wrapper));
		}
		
		public WorldgenModifier wrapDensityFunction(Holder<DensityFunction> target, Holder<DensityFunction> wrapper) {
			return new WrapDensityFunctionModifier(priority.orElse(DEFAULT_PRIORITY), target, wrapper);
		}
		
		public WorldgenModifier wrapNoiseRouter(ResourceKey<Level> dimension, NoiseRouterTarget target, DensityFunction wrapper) {
			return new WrapNoiseRouterModifier(priority.orElse(DEFAULT_PRIORITY), dimension, target, Holder.direct(wrapper));
		}
		
		public WorldgenModifier wrapNoiseRouter(ResourceKey<Level> dimension, NoiseRouterTarget target, Holder<DensityFunction> wrapper) {
			return new WrapNoiseRouterModifier(priority.orElse(DEFAULT_PRIORITY), dimension, target, wrapper);
		}
	}
}
