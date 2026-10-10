package dev.worldgen.lithostitched.impl.worldgen.processor.internal;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.worldgen.lithostitched.api.worldgen.processor.LithostitchedProcessorLists;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.impl.duck.ReferencePosDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class NonTemplateProcessorHelper {
	
	public static Optional<StructureProcessorList> getProcessor(ReferencePosDuck piece, LevelReader level) {
		var processorKey = LithostitchedProcessorLists.pick((StructurePiece) piece);
		if (processorKey == null) {
			return Optional.empty();
		}
		
		return getProcessor(level, processorKey);
	}
	
	public static Optional<StructureProcessorList> getProcessor(LevelReader level, ResourceKey<StructureProcessorList> key) {
		var processorList = Lithostitched.registry(level.registryAccess(), Registries.PROCESSOR_LIST).getOptional(key);
		if (processorList.isEmpty() || processorList.get().list().isEmpty()) {
			return Optional.empty();
		}
		
		return processorList;
	}
	
	public static BlockState processBlock(ReferencePosDuck piece, BoundingBox boundingBox, LevelReader level, BlockPos pos, BlockState state, StructureProcessorList processors) {
		return processBlock(piece, boundingBox, level, pos, state, BlockPos.ZERO, processors);
	}
	
	public static BlockState processBlock(ReferencePosDuck piece, BoundingBox boundingBox, LevelReader level, BlockPos pos, BlockState state, BlockPos templateRelativePos, StructureProcessorList processors) {
		StructureTemplate.StructureBlockInfo processedInfo = new StructureTemplate.StructureBlockInfo(pos, state, null);
		BlockPos piecePos = new BlockPos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
		
		for (StructureProcessor processor : processors.list()) {
			BlockPos referencePos = piece.lithostitched$getReferencePos() == null ? piecePos : piece.lithostitched$getReferencePos();
			processedInfo = processor.processBlock(level, piecePos, referencePos, templateRelativePos, processedInfo, new StructurePlaceSettings());
			if (processedInfo == null) {
				return null;
			}
		}
		
		return processedInfo.state();
	}
	
	public static BlockState processBlock(BlockPos piecePos, LevelReader level, BlockPos pos, BlockState state, StructureProcessorList processors) {
		StructureTemplate.StructureBlockInfo processedInfo = new StructureTemplate.StructureBlockInfo(pos, state, null);
		
		for (StructureProcessor processor : processors.list()) {
			processedInfo = processor.processBlock(level, piecePos, piecePos, BlockPos.ZERO, processedInfo, new StructurePlaceSettings());
			if (processedInfo == null) {
				return null;
			}
		}
		
		return processedInfo.state();
	}
	
	public static boolean getProcessorAndApply(ReferencePosDuck piece, BoundingBox boundingBox, LevelReader level, BlockPos pos, BlockState state, int flags, Operation<Boolean> operation) {
		return getProcessorAndApply(piece, boundingBox, level, pos, state, flags, operation, 0, 0, 0);
	}
	
	public static boolean getProcessorAndApply(ReferencePosDuck piece, BoundingBox boundingBox, LevelReader level, BlockPos pos, BlockState state, int flags, Operation<Boolean> operation, int x, int y, int z) {
		var processorList = NonTemplateProcessorHelper.getProcessor(piece, level);
		if (processorList.isEmpty()) {
			return operation.call(level, pos, state, flags);
		}
		
		BlockState processedState = NonTemplateProcessorHelper.processBlock(piece, boundingBox, level, pos, state, new BlockPos(x, y, z), processorList.get());
		if (processedState != null) {
			return operation.call(level, pos, processedState, flags);
		}
		return false;
	}
}
