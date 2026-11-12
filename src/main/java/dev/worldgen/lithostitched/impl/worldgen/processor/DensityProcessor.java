package dev.worldgen.lithostitched.impl.worldgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.worldgen.feature.DensityFeature;
import dev.worldgen.lithostitched.impl.worldgen.util.DensitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.densityfunction.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.Nullable;

public record DensityProcessor(DensityFunction densityFunction, DensitySelector<StructureProcessor> selector) implements StructureProcessor {
	public static final MapCodec<DensityProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("density_function").forGetter(DensityProcessor::densityFunction),
		DensitySelector.codec(StructureProcessorType.SINGLE_CODEC).fieldOf("selector").forGetter(DensityProcessor::selector)
	).apply(instance, DensityProcessor::new));
	
	@Override
	public StructureTemplate.@Nullable StructureBlockInfo processBlock(LevelReader levelReader, BlockPos targetPosition, BlockPos referencePos, BlockPos templateRelativePos, StructureTemplate.StructureBlockInfo processedBlockInfo, StructurePlaceSettings settings) {
		if (!(levelReader instanceof WorldGenLevel level)) return processedBlockInfo;
		
		BlockPos absolute = processedBlockInfo.pos();
		
		DensitySampler sampler = level.getLevel().getChunkSource().randomState().getSampler(this.densityFunction);
		SamplerContext context = SamplerContext.builder()
			.setUserFields(getContextMap(absolute, templateRelativePos))
			.build();
		
		float density = sampler.sampleValue(context, absolute.getX(), absolute.getY(), absolute.getZ());
		return this.selector.pick(density).processBlock(levelReader, targetPosition, referencePos, templateRelativePos, processedBlockInfo, settings);
	}
	
	private ContextMap getContextMap(BlockPos absolute, BlockPos relative) {
		return ContextMap.builder()
			.set(DensityFeature.ABSOLUTE_POS, absolute)
			.set(DensityFeature.RELATIVE_POS, relative)
			.build();
	}
	
	@Override
	public MapCodec<? extends StructureProcessor> codec() {
		return CODEC;
	}
}
