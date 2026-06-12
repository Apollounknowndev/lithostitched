package dev.worldgen.lithostitched.api.worldgen.processor;

import dev.worldgen.lithostitched.Lithostitched;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.structures.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public interface LithostitchedProcessorLists {
	ResourceKey<StructureProcessorList> END_CITY = key("end_city");
	ResourceKey<StructureProcessorList> IGLOO = key("igloo");
	ResourceKey<StructureProcessorList> NETHER_FOSSIL = key("nether_fossil");
	ResourceKey<StructureProcessorList> OCEAN_RUIN_COLD = key("ocean_ruin_cold");
	ResourceKey<StructureProcessorList> OCEAN_RUIN_WARM = key("ocean_ruin_warm");
	ResourceKey<StructureProcessorList> RUINED_PORTAL = key("ruined_portal");
	ResourceKey<StructureProcessorList> SHIPWRECK = key("shipwreck");
	ResourceKey<StructureProcessorList> WOODLAND_MANSION = key("woodland_mansion");
	
	ResourceKey<StructureProcessorList> MINESHAFT = key("mineshaft");
	ResourceKey<StructureProcessorList> NETHER_FORTRESS = key("nether_fortress");
	ResourceKey<StructureProcessorList> OCEAN_MONUMENT = key("ocean_monument");
	ResourceKey<StructureProcessorList> DESERT_TEMPLE = key("desert_temple");
	ResourceKey<StructureProcessorList> JUNGLE_TEMPLE = key("jungle_temple");
	ResourceKey<StructureProcessorList> SWAMP_HUT = key("swamp_hut");
	ResourceKey<StructureProcessorList> STRONGHOLD = key("stronghold");
	
	private static ResourceKey<StructureProcessorList> key(String name) {
		return ResourceKey.create(Registries.PROCESSOR_LIST, Lithostitched.id(name));
	}
	
	// Internal method for non-template processors.
	static ResourceKey<StructureProcessorList> pick(StructurePiece piece) {
		if (piece instanceof MineshaftPieces.MineShaftPiece) {
			return MINESHAFT;
		}
		if (piece instanceof NetherFortressPieces.NetherBridgePiece) {
			return NETHER_FORTRESS;
		}
		if (piece instanceof OceanMonumentPieces.OceanMonumentPiece) {
			return OCEAN_MONUMENT;
		}
		if (piece instanceof DesertPyramidPiece) {
			return DESERT_TEMPLE;
		}
		if (piece instanceof JungleTemplePiece) {
			return JUNGLE_TEMPLE;
		}
		if (piece instanceof SwampHutPiece) {
			return SWAMP_HUT;
		}
		if (piece instanceof StrongholdPieces.StrongholdPiece) {
			return STRONGHOLD;
		}
		return null;
	}
}
