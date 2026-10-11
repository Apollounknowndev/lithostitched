package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.cellular.ReturnType;
import dev.worldgen.lithostitched.impl.LithostitchedInternalHooks;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.util.*;
import net.minecraft.world.level.levelgen.densityfunction.*;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import java.util.Optional;

public record CellularDensityFunction(Optional<CellCondition> condition, GridDimensions gridDimensions, float jitter, ReturnType returnType, int salt) implements DensityFunction {
	public static final MapCodec<CellularDensityFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		CellCondition.CODEC.optionalFieldOf("condition").forGetter(CellularDensityFunction::condition),
		GridDimensions.CODEC.fieldOf("grid_dimensions").forGetter(CellularDensityFunction::gridDimensions),
		Codec.floatRange(0f, 1f).fieldOf("jitter").forGetter(CellularDensityFunction::jitter),
		ReturnType.CODEC.fieldOf("return_type").forGetter(CellularDensityFunction::returnType),
		Codec.INT.fieldOf("salt").forGetter(CellularDensityFunction::salt)
	).apply(i, CellularDensityFunction::new));
	
	private static boolean isCellPositionInvalid(CellCondition.Sampler condition, SamplerContext context, BlockPos pos) {
		return !condition.range.isValueInRange(condition.function.sampleValue(context, pos.getX(), pos.getY(), pos.getZ()));
	}
	
	@Override
	public DensitySampler compileSampler(CompileContext context) {
		if (this.gridDimensions.is2d()) {
			return new XZSampler(
				this.condition.map(cc -> cc.compileSampler(context)),
				this.gridDimensions.horizontal,
				this.jitter,
				this.returnType,
				this.salt
			);
		}
		return new XYZSampler(
			this.condition.map(cc -> cc.compileSampler(context)),
			this.gridDimensions,
			this.jitter,
			this.returnType,
			this.salt
		);
	}
	
	@Override
	public DensityFunction rewriteChildren(DfRewriteRule rule) {
		Optional<CellCondition> condition = this.condition.map(c -> c.rewriteChildren(rule));
		return condition == this.condition ? this : new CellularDensityFunction(condition, this.gridDimensions, this.jitter, this.returnType, this.salt);
	}
	
	@Override
	public Interval range() {
		// TODO: Fix
		return Interval.INFINITE;
	}
	
	@Override
	public @Axes int domainAxes() {
		return this.gridDimensions.is2d() ? 5 : ALL_AXES;
	}
	
	@Override
	public MapCodec<? extends DensityFunction> codec() {
		return CODEC;
	}
	
	public record GridDimensions(int horizontal, int vertical) {
		public static final Codec<GridDimensions> CODEC = Codec.withAlternative(
			RecordCodecBuilder.create(i -> i.group(
				ExtraCodecs.POSITIVE_INT.fieldOf("horizontal").forGetter(GridDimensions::horizontal),
				ExtraCodecs.NON_NEGATIVE_INT.fieldOf("vertical").orElse(0).forGetter(GridDimensions::vertical)
			).apply(i, GridDimensions::new)),
			ExtraCodecs.POSITIVE_INT,
			size -> new GridDimensions(size, size)
		);
		
		public boolean is2d() {
			return this.vertical == 0;
		}
	}
	
	public record CellCondition(DensityFunction function, InclusiveRange<Float> range, float fallbackValue) {
		public static final Codec<CellCondition> CODEC = RecordCodecBuilder.create(i -> i.group(
			DensityFunction.CODEC.fieldOf("function").forGetter(CellCondition::function),
			LithostitchedCodecs.FLOAT_RANGE.fieldOf("range").forGetter(CellCondition::range),
			Codec.FLOAT.fieldOf("fallback_value").forGetter(CellCondition::fallbackValue)
		).apply(i, CellCondition::new));
		
		public CellCondition rewriteChildren(DfRewriteRule rule) {
			DensityFunction rewrittenFunction = rule.rewrite(function);
			return rewrittenFunction == function ? this : new CellCondition(rewrittenFunction, range, fallbackValue);
		}
		
		public Sampler compileSampler(CompileContext context) {
			return new Sampler(this.function.compileSampler(context), this.range, this.fallbackValue);
		}
		
		public record Sampler(DensitySampler function, InclusiveRange<Float> range, float fallbackValue) {}
	}
	public record XZSampler(Optional<CellCondition.Sampler> condition, int gridWidth, float jitter, ReturnType returnType, int salt) implements DensitySampler {
		@Override
		public void sampleVolume(SamplerContext context, DensityBuffer outputBuffer, DensityVolume volume) {
			int index = 0;
			
			for (int z = 0; z < volume.sizeZ(); z++) {
				int blockZ = volume.blockZ(z);
				
				for (int x = 0; x < volume.sizeX(); x++) {
					int blockX = volume.blockX(x);
					
					for (int y = 0; y < volume.sizeY(); y++) {
						int blockY = volume.blockY(y);
						outputBuffer.set(index++, this.sampleValue(context, blockX, blockY, blockZ));
					}
				}
			}
		}
		
		/*@Override
		public void sampleVolume(SamplerContext context, DensityBuffer outputBuffer, DensityVolume volume) {
			int minGridX = Math.floorDiv(volume.minBlockX(), this.gridWidth);
			int maxGridX = Math.floorDiv(volume.maxBlockX(), this.gridWidth);
			int gridSpanX = maxGridX - minGridX + 1;
			
			int minGridZ = Math.floorDiv(volume.minBlockZ(), this.gridWidth);
			int maxGridZ = Math.floorDiv(volume.maxBlockZ(), this.gridWidth);
			int gridSpanZ = maxGridZ - minGridZ + 1;
			
			WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
			
			int[][] targetXs = new int[gridSpanX + 2][gridSpanZ + 2];
			int[][] targetZs = new int[gridSpanX + 2][gridSpanZ + 2];
			float[][] targetValues = new float[gridSpanX + 2][gridSpanZ + 2];
			
			for (int xo = -1; xo <= gridSpanX; xo++) {
				for (int zo = -1; zo <= gridSpanZ; zo++) {
					random.setLargeFeatureWithSalt(
						LithostitchedInternalHooks.WORLD_SEED,
						minGridX + xo,
						minGridZ + xo,
						this.salt
					);
					
					targetXs[xo + 1][zo + 1] = (minGridX + xo) * this.gridWidth + random.nextIntBetweenInclusive(0, (int) (this.gridWidth * this.jitter));
					targetZs[xo + 1][zo + 1] = (minGridZ + zo) * this.gridWidth + random.nextIntBetweenInclusive(0, (int) (this.gridWidth * this.jitter));
					targetValues[xo + 1][zo + 1] = random.nextFloat();
				}
			}
			
			for (int z = 0; z < volume.sizeZ(); z++) {
				int blockZ = volume.blockZ(z);
				int gridIndexZ = toGridPos(blockZ) - minGridZ;
				
				for (int x = 0; x < volume.sizeX(); x++) {
					int blockX = volume.blockX(x);
					int gridIndexX = toGridPos(blockX) - minGridX;
					
					BlockPos pos1 = null;
					float distance1 = Float.MAX_VALUE;
					BlockPos pos2 = null;
					float distance2 = Float.MAX_VALUE;
					float nearestCellValue = Float.MAX_VALUE;
					
					for (int xo = 0; xo <= 2; xo++) {
						for (int zo = 0; zo <= 2; zo++) {
							int targetX = targetXs[gridIndexX + xo][gridIndexZ + zo];
							int targetZ = targetZs[gridIndexX + xo][gridIndexZ + zo];
							float targetValue = targetValues[gridIndexX + xo][gridIndexZ + zo];
							
							int dx = targetX - blockX;
							int dz = targetZ - blockZ;
							float distance = Mth.length(dx, dz);
							
							if (distance < distance2) {
								if (distance < distance1) {
									distance2 = distance1;
									pos2 = pos1;
									
									distance1 = distance;
									pos1 = new BlockPos(targetX, 0, targetZ);
									
									nearestCellValue = targetValue;
								} else {
									distance2 = distance;
									pos2 = new BlockPos(targetX, 0, targetZ);
								}
							}
						}
					}
					
					
					float returnValue = switch (this.returnType) {
						case CELL_VALUE -> nearestCellValue;
						case DISTANCE_1 -> distance1;
						case DISTANCE_2 -> distance2;
						case DISTANCE_2_ADD -> (distance2 + distance1) * 0.5f;
						case DISTANCE_2_SUB -> distance2 - distance1;
						case DISTANCE_2_MUL -> distance2 * distance1 * 0.5f;
						case DISTANCE_2_DIV -> distance1 / distance2;
					};
					
					if (this.condition.isPresent() && this.returnType != ReturnType.CELL_VALUE) {
						CellCondition.Sampler condition = this.condition.get();
						
						if (this.returnType != ReturnType.DISTANCE_2 && isCellPositionInvalid(condition, context, pos1)) {
							returnValue = condition.fallbackValue;
						}
						
						if (this.returnType != ReturnType.DISTANCE_1 && isCellPositionInvalid(condition, context, pos2)) {
							returnValue = condition.fallbackValue;
						}
					}
					
					int index = volume.indexUnchecked(x, 0, z);
					outputBuffer.setRange(index, volume.sizeY(), returnValue);
				}
			}
		}*/
		
		@Override
		public float sampleValue(SamplerContext context, int blockX, int blockY, int blockZ) {
			int spacedGridX = Math.floorDiv(blockX, this.gridWidth);
			int spacedGridZ = Math.floorDiv(blockZ, this.gridWidth);
			
			BlockPos pos1 = null;
			float distance1 = Float.MAX_VALUE;
			
			BlockPos pos2 = null;
			float distance2 = Float.MAX_VALUE;
			
			float nearestCellValue = Float.MAX_VALUE;
			
			WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
			for (int xo = -1; xo <= 1; xo++) {
				for (int zo = -1; zo <= 1; zo++) {
					random.setLargeFeatureWithSalt(
						LithostitchedInternalHooks.WORLD_SEED,
						spacedGridX + xo,
						spacedGridZ + zo,
						this.salt
					);
					
					int targetX = (spacedGridX + xo) * this.gridWidth + random.nextIntBetweenInclusive(0, (int) (this.gridWidth * this.jitter));
					int targetZ = (spacedGridZ + zo) * this.gridWidth + random.nextIntBetweenInclusive(0, (int) (this.gridWidth * this.jitter));
					float cellValue = random.nextFloat();
					
					int dx = targetX - blockX;
					int dz = targetZ - blockZ;
					float distance = Mth.length(dx, dz);
					
					if (distance < distance2) {
						if (distance < distance1) {
							distance2 = distance1;
							pos2 = pos1;
							
							distance1 = distance;
							pos1 = new BlockPos(targetX, 0, targetZ);
							
							nearestCellValue = cellValue;
						} else {
							distance2 = distance;
							pos2 = new BlockPos(targetX, 0, targetZ);
						}
					}
				}
			}
			
			float returnValue = switch (this.returnType) {
				case CELL_VALUE -> nearestCellValue;
				case DISTANCE_1 -> distance1;
				case DISTANCE_2 -> distance2;
				case DISTANCE_2_ADD -> (distance2 + distance1) * 0.5f;
				case DISTANCE_2_SUB -> distance2 - distance1;
				case DISTANCE_2_MUL -> distance2 * distance1 * 0.5f;
				case DISTANCE_2_DIV -> distance1 / distance2;
			};
			
			if (this.condition.isPresent() && this.returnType != ReturnType.CELL_VALUE) {
				CellCondition.Sampler condition = this.condition.get();
				
				if (this.returnType != ReturnType.DISTANCE_2 && isCellPositionInvalid(condition, context, pos1)) {
					returnValue = condition.fallbackValue;
				}
				
				if (this.returnType != ReturnType.DISTANCE_1 && isCellPositionInvalid(condition, context, pos2)) {
					returnValue = condition.fallbackValue;
				}
			}
			
			return returnValue;
		}
		
		public int toGridPos(int pos) {
			return Math.floorDiv(pos, this.gridWidth);
		}
	}
	
	
	public record XYZSampler(Optional<CellCondition.Sampler> condition, GridDimensions gridSize, float jitter, ReturnType returnType, int salt) implements DensitySampler {
		@Override
		public void sampleVolume(SamplerContext context, DensityBuffer outputBuffer, DensityVolume volume) {
			int index = 0;
			
			for (int z = 0; z < volume.sizeZ(); z++) {
				int blockZ = volume.blockZ(z);
				
				for (int x = 0; x < volume.sizeX(); x++) {
					int blockX = volume.blockX(x);
					
					for (int y = 0; y < volume.sizeY(); y++) {
						int blockY = volume.blockY(y);
						outputBuffer.set(index++, this.sampleValue(context, blockX, blockY, blockZ));
					}
				}
			}
		}
		
		@Override
		public float sampleValue(SamplerContext context, int blockX, int blockY, int blockZ) {
			int spacedGridX = Math.floorDiv(blockX, this.gridSize.horizontal);
			int spacedGridY = Math.floorDiv(blockY, this.gridSize.vertical);
			int spacedGridZ = Math.floorDiv(blockZ, this.gridSize.horizontal);
			
			BlockPos pos1 = null;
			float distance1 = Float.MAX_VALUE;
			
			BlockPos pos2 = null;
			float distance2 = Float.MAX_VALUE;
			
			float nearestCellValue = Float.MAX_VALUE;
			
			WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
			for (int xo = -1; xo <= 1; xo++) {
				for (int yo = -1; yo <= 1; yo++) {
					for (int zo = -1; zo <= 1; zo++) {
						random.setSeed(
							(spacedGridX + xo) * 341873128712L +
							(spacedGridY + yo) * 297845856431L +
							(spacedGridZ + zo) * 132897987541L +
							LithostitchedInternalHooks.WORLD_SEED +
							this.salt
						);
						
						int targetX = (spacedGridX + xo) * this.gridSize.horizontal + random.nextIntBetweenInclusive(0, (int) (this.gridSize.horizontal * this.jitter));
						int targetY = (spacedGridY + yo) * this.gridSize.vertical + random.nextIntBetweenInclusive(0, (int) (this.gridSize.vertical * this.jitter));
						int targetZ = (spacedGridZ + zo) * this.gridSize.horizontal + random.nextIntBetweenInclusive(0, (int) (this.gridSize.horizontal * this.jitter));
						float cellValue = random.nextFloat();
						
						int dx = targetX - blockX;
						int dy = targetY - blockY;
						int dz = targetZ - blockZ;
						float distance = Mth.length(dx, dy, dz);
						
						if (distance < distance2) {
							if (distance < distance1) {
								distance2 = distance1;
								pos2 = pos1;
								
								distance1 = distance;
								pos1 = new BlockPos(targetX, targetY, targetZ);
								
								nearestCellValue = cellValue;
							} else {
								distance2 = distance;
								pos2 = new BlockPos(targetX, targetY, targetZ);
							}
						}
					}
				}
			}
			
			float returnValue = switch (this.returnType) {
				case CELL_VALUE -> nearestCellValue;
				case DISTANCE_1 -> distance1;
				case DISTANCE_2 -> distance2;
				case DISTANCE_2_ADD -> (distance2 + distance1) * 0.5f;
				case DISTANCE_2_SUB -> distance2 - distance1;
				case DISTANCE_2_MUL -> distance2 * distance1 * 0.5f;
				case DISTANCE_2_DIV -> distance1 / distance2;
			};
			
			if (this.condition.isPresent() && this.returnType != ReturnType.CELL_VALUE) {
				CellCondition.Sampler condition = this.condition.get();
				
				if (this.returnType != ReturnType.DISTANCE_2 && isCellPositionInvalid(condition, context, pos1)) {
					returnValue = condition.fallbackValue;
				}
				
				if (this.returnType != ReturnType.DISTANCE_1 && isCellPositionInvalid(condition, context, pos2)) {
					returnValue = condition.fallbackValue;
				}
			}
			
			return returnValue;
		}
	}
}
