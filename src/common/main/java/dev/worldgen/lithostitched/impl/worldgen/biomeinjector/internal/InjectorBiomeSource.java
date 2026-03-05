package dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.worldgen.biomeinjector.BiomeInjector;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.SimpleContext;
import dev.worldgen.lithostitched.api.worldgen.util.DensityFunctionWrapper;
import dev.worldgen.lithostitched.impl.LithostitchedPlatform;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.*;
import dev.worldgen.lithostitched.mixin.common.MultiNoiseBiomeSourceAccessor;
import dev.worldgen.lithostitched.mixin.common.mnbs.MNBSPLAccessor;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.Region;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.region.RegionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class InjectorBiomeSource extends BiomeSource {
	// When encoding, store the delegate
	// When decoding, ignore the Lithostitched source and directly return the delegate
	public static final MapCodec<BiomeSource> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		BiomeSource.CODEC.fieldOf("delegate").forGetter(InjectorBiomeSource::getRootSource)
	).apply(i, Function.identity()));
	
	private static final Supplier<Boolean> APPLY_FULL_REPLACEMENTS_LATE = Suppliers.memoize(() ->
		LithostitchedPlatform.isModLoaded("terrablender") ||
		LithostitchedPlatform.isModLoaded("biolith") ||
		LithostitchedPlatform.isModLoaded("blueprint")
	);
	
	private final BiomeSource directDelegate;
	private final BiomeSource rootDelegate;
	
	private final Map<MapCodec<? extends BiomeInjector>, List<BiomeInjector>> injectorsByType = new HashMap<>();
	private List<Holder<Biome>> possibleBiomes = new ArrayList<>();
	private List<Holder<Biome>> replacedBiomes = new ArrayList<>();
	private RegionManager regionManager;
	
	public InjectorBiomeSource(BiomeSource directDelegate) {
		this.directDelegate = directDelegate;
		this.rootDelegate = getRootSource(directDelegate);
	}
	
	public void applyInjectors(Map<Identifier, BiomeInjector> injectors, Optional<DensityFunction> regionFunction, Map<ResourceKey<Region>, Region> regions, DensityFunctionWrapper noiseHelper) {
		this.possibleBiomes = new ArrayList<>();
		this.regionManager = new RegionManager(regionFunction, regions, noiseHelper);
		
		injectors.values().forEach(injector -> {
			injector.mapAll(noiseHelper);
			
			List<BiomeInjector> byType = this.injectorsByType.getOrDefault(injector.codec(), new ArrayList<>());
			byType.add(injector);
			this.injectorsByType.put(injector.codec(), byType);
			
			this.possibleBiomes.addAll(injector.possibleBiomes());
			if (injector instanceof ReplaceFully replaceFully) {
				this.replacedBiomes.addAll(replaceFully.targets().stream().toList());
			}
		});
		
		for (List<BiomeInjector> byType : this.injectorsByType.values()) {
			byType.sort(Comparator.comparingInt(BiomeInjector::priority));
		}
		
		if (!(this.rootDelegate instanceof MultiNoiseBiomeSource multiNoise)) {
			Lithostitched.debug("Biome source is not a MultiNoiseBiomeSource instance, got {}. add_points and replace_fully injectors will not run.", this.rootDelegate.getClass().getSimpleName());
			return;
		}
		var accessor = (MultiNoiseBiomeSourceAccessor) multiNoise;
		var left = accessor.getParameters().left();
		var right = accessor.getParameters().right();
		
		var modifiedParameters = new ArrayList<>(left.orElseGet(() -> right.get().value().parameters()).values());
		AddPoints.apply(modifiedParameters, this.injectorsByType.getOrDefault(AddPoints.CODEC, new ArrayList<>()));
		if (!APPLY_FULL_REPLACEMENTS_LATE.get()) {
			ReplaceFully.apply(modifiedParameters, this.injectorsByType.getOrDefault(ReplaceFully.CODEC, new ArrayList<>()));
		}
		if (left.isPresent()) {
			accessor.setParameters(Either.left(new Climate.ParameterList<>(modifiedParameters)));
		} else {
			((MNBSPLAccessor)right.get().value()).setParameters(new Climate.ParameterList<>(modifiedParameters));
		}
	}
	
	@Override
	public MapCodec<? extends BiomeSource> codec() {
		return CODEC;
	}
	
	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		return Stream.concat(this.directDelegate.possibleBiomes().stream(), this.possibleBiomes.stream()).filter(
			biome -> !this.replacedBiomes.contains(biome)
		);
	}
	
	@Override
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
		if (this.injectorsByType.isEmpty()) {
			return this.directDelegate.getNoiseBiome(quartX, quartY, quartZ, sampler);
		}
		
		int blockX = QuartPos.toBlock(quartX);
		int blockY = QuartPos.toBlock(quartY);
		int blockZ = QuartPos.toBlock(quartZ);
		SimpleContext context = SimpleContext.of(blockX, blockY, blockZ);
		TargetPoint point = sampler.sample(quartX, quartY, quartZ);
		HashMap<DensityFunction, Double> densities = new HashMap<>();
		ResourceKey<Region> currentRegion = this.regionManager.getRegion(context);
		
		if (this.injectorsByType.containsKey(ForcePlacement.CODEC)) {
			for (BiomeInjector injector : this.injectorsByType.getOrDefault(ForcePlacement.CODEC, List.of())) {
				ForcePlacement forcePlacement = (ForcePlacement) injector;
				if (forcePlacement.matches(context, point, densities, currentRegion)) return forcePlacement.biome();
			}
		}
		
		Holder<Biome> baseBiome = this.directDelegate instanceof MultiNoiseBiomeSource multiNoise ?
			multiNoise.getNoiseBiome(point) :
			this.directDelegate.getNoiseBiome(quartX, quartY, quartZ, sampler);
		
		return applyReplacements(context, point, densities, baseBiome, currentRegion);
	}
	
	public String getRegionLine(BlockPos pos) {
		SimpleContext context = SimpleContext.of(pos);
		Identifier region = this.regionManager.getRegion(context).identifier();
		int rawValue = this.regionManager.getRegionValue(context);
		return String.format("Region: %s (Raw value: %s)", region, rawValue);
	}
	
	public Holder<Biome> applyReplacements(FunctionContext context, TargetPoint point, HashMap<DensityFunction, Double> densities, Holder<Biome> biome, ResourceKey<Region> currentRegion) {
		if (APPLY_FULL_REPLACEMENTS_LATE.get()) {
			if (this.replacedBiomes.contains(biome)) {
				for (BiomeInjector injector : this.injectorsByType.getOrDefault(ReplaceFully.CODEC, List.of())) {
					ReplaceFully replaceFully = (ReplaceFully) injector;
					if (replaceFully.targets().contains(biome)) {
						return replaceFully.replacement();
					}
				}
			}
		}
		
		for (BiomeInjector injector : this.injectorsByType.getOrDefault(ReplacePartially.CODEC, List.of())) {
			ReplacePartially replacePartially = (ReplacePartially) injector;
			if (replacePartially.matches(context, point, densities, biome, currentRegion)) return replacePartially.replacement();
		}
		return biome;
	}
	
	private static BiomeSource getRootSource(BiomeSource currentSource) {
		if (currentSource instanceof InjectorBiomeSource injector) {
			return getRootSource(injector.directDelegate);
		}
		// Blueprint compatibility without Blueprint dependency. Yeah...
		if (instanceOfBlueprintSource(currentSource)) {
			try {
				Field field = currentSource.getClass().getDeclaredField("originalSource");
				field.setAccessible(true);
				BiomeSource delegate = (BiomeSource) field.get(currentSource);
				return getRootSource(delegate);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return currentSource;
	}
	
	private static boolean instanceOfBlueprintSource(BiomeSource source) {
		return source.getClass().getSimpleName().equals("ModdedBiomeSource");
	}
}
