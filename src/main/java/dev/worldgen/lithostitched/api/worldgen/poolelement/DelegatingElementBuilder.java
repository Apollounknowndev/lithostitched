package dev.worldgen.lithostitched.api.worldgen.poolelement;

import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingConfig;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingElementBuilderImpl;
import net.minecraft.resources.Identifier;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public interface DelegatingElementBuilder {
	static DelegatingElementBuilder create(StructurePoolElement delegate) {
		return new DelegatingElementBuilderImpl(delegate);
	}
	
	DelegatingElementBuilder forcedCount(int count);
	DelegatingElementBuilder limitedCount(int count);
	DelegatingElementBuilder allowedDepth(InclusiveRange<Integer> allowedDepth);
	DelegatingElementBuilder terrainAdaptation(TerrainAdjustment adaptation);
	DelegatingElementBuilder condition(PlacementCondition condition);
	DelegatingElementBuilder named(Identifier name);
	DelegatingElementBuilder terrainMatchingHeightmap(Heightmap.Types heightmap);
	DelegatingElementBuilder allowBoundingBoxCollisions();
	DelegatingElementBuilder otherPiecesCanIntersect();
	
	DelegatingConfig build();
}
