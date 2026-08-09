package dev.worldgen.lithostitched.api.worldgen.structure;

import com.mojang.datafixers.util.Either;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.impl.worldgen.structure.AlternateJigsawConfig;
import dev.worldgen.lithostitched.impl.worldgen.structure.AlternateJigsawStructure;
import dev.worldgen.lithostitched.impl.worldgen.structure.DelegatingConfig;
import dev.worldgen.lithostitched.impl.worldgen.structure.DelegatingStructure;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;

public interface LithostitchedStructures {
	static Structure jigsaw(Structure.StructureSettings settings, Holder<StructureTemplatePool> startPool, IntProvider size, HeightProvider startHeight, int maxDistanceFromCenter, Optional<Heightmap.Types> projectStartToHeightmap) {
		return new AlternateJigsawStructure(settings, new AlternateJigsawConfig(
			startPool,
			Optional.empty(),
			size,
			false,
			startHeight,
			false,
			projectStartToHeightmap.map(Either::right),
			new MaxDistanceFromCenter(maxDistanceFromCenter),
			List.of(),
			DimensionPadding.ZERO,
			LiquidSettings.APPLY_WATERLOGGING
		));
	}
	
	static Structure jigsaw(Structure.StructureSettings settings, Holder<StructureTemplatePool> startPool, Optional<Identifier> startJigsawName, IntProvider size, boolean fixedRotation, HeightProvider startHeight, boolean useExpansionHack, Optional<Either<SurfaceSnap, Heightmap.Types>> startProjection, MaxDistanceFromCenter maxDistanceFromCenter, List<PoolAliasBinding> poolAliases, DimensionPadding dimensionPadding, LiquidSettings liquidSettings) {
		return new AlternateJigsawStructure(settings, new AlternateJigsawConfig(
			startPool, 
			startJigsawName, 
			size, 
			fixedRotation, 
			startHeight, 
			useExpansionHack, 
			startProjection, 
			maxDistanceFromCenter, 
			poolAliases, 
			dimensionPadding, 
			liquidSettings
		));
	}
	
	static Structure delegating(Structure delegate, PlacementCondition spawnCondition) {
		return new DelegatingStructure(new DelegatingConfig(Holder.direct(delegate), Optional.of(spawnCondition)));
	}
}
