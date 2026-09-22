package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.densityfunction.SimpleContext;
import dev.worldgen.lithostitched.impl.LithostitchedInternalHooks;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.util.*;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import java.util.Optional;

public record CellularDensityFunction(Optional<CellCondition> condition, GridDimensions gridDimensions, float jitter, ReturnType returnType, int salt) implements DensityFunction {
	public static final MapCodec<CellularDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
		CellCondition.CODEC.optionalFieldOf("condition").forGetter(CellularDensityFunction::condition),
		GridDimensions.CODEC.fieldOf("grid_dimensions").forGetter(CellularDensityFunction::gridDimensions),
		Codec.floatRange(0f, 1f).fieldOf("jitter").forGetter(CellularDensityFunction::jitter),
		ReturnType.CODEC.fieldOf("return_type").forGetter(CellularDensityFunction::returnType),
		Codec.INT.fieldOf("salt").forGetter(CellularDensityFunction::salt)
	).apply(i, CellularDensityFunction::new));
	public static KeyDispatchDataCodec<CellularDensityFunction> CODEC_HOLDER = KeyDispatchDataCodec.of(DATA_CODEC);
	
	@Override
	public double compute(FunctionContext context) {
		int x = context.blockX();
		int y = context.blockY();
		int z = context.blockZ();
		
		return this.gridDimensions.is2d() ? this.sample2D(x, z) : this.sample3D(x, y, z);
	}
	
	private boolean isCellPositionInvalid(CellCondition cellCondition, BlockPos pos) {
		return !cellCondition.range.isValueInRange(cellCondition.function.compute(SimpleContext.of(pos)));
	}
	
	@Override
	public void fillArray(double[] output, ContextProvider contextProvider) {
		contextProvider.fillAllDirectly(output, this);
	}
	
	@Override
	public DensityFunction mapChildren(Visitor visitor) {
		return new CellularDensityFunction(this.condition.map(c -> c.mapChildren(visitor)), this.gridDimensions, this.jitter, this.returnType, this.salt);
	}
	
	@Override
	public double minValue() {
		return Math.min(this.condition.map(CellCondition::fallbackValue).orElse(0d), 0);
	}
	
	@Override
	public double maxValue() {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public KeyDispatchDataCodec<? extends DensityFunction> codec() {
		return CODEC_HOLDER;
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
	
	public record CellCondition(DensityFunction function, InclusiveRange<Double> range, double fallbackValue) {
		public static final Codec<CellCondition> CODEC = RecordCodecBuilder.create(i -> i.group(
			LithostitchedCodecs.DF_BASE.fieldOf("function").forGetter(CellCondition::function),
			LithostitchedCodecs.DOUBLE_RANGE.fieldOf("range").forGetter(CellCondition::range),
			Codec.DOUBLE.fieldOf("fallback_value").forGetter(CellCondition::fallbackValue)
		).apply(i, CellCondition::new));
		
		public CellCondition mapChildren(Visitor visitor) {
			return new CellCondition(visitor.apply(function), range, fallbackValue);
		}
	}
	
	public enum ReturnType implements StringRepresentable {
		CELL_VALUE("cell_value"),
		DISTANCE_1("distance_1"),
		DISTANCE_2("distance_2"),
		DISTANCE_2_ADD("distance_2_add"),
		DISTANCE_2_SUB("distance_2_sub"),
		DISTANCE_2_MUL("distance_2_mul"),
		DISTANCE_2_DIV("distance_2_div");
		
		public static final Codec<ReturnType> CODEC = StringRepresentable.fromValues(ReturnType::values);
		private final String name;
		
		ReturnType(String name) {
			this.name = name;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
	
	public double sample2D(int blockX, int blockZ) {
		int spacedGridX = Math.floorDiv(blockX, this.gridDimensions.horizontal);
		int spacedGridZ = Math.floorDiv(blockZ, this.gridDimensions.horizontal);
		
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
				
				int targetX = (spacedGridX + xo) * this.gridDimensions.horizontal + random.nextIntBetweenInclusive(0, (int) (this.gridDimensions.horizontal * this.jitter));
				int targetZ = (spacedGridZ + zo) * this.gridDimensions.horizontal + random.nextIntBetweenInclusive(0, (int) (this.gridDimensions.horizontal * this.jitter));
				float cellValue = random.nextFloat();
				
				int dx = targetX - blockX;
				int dz = targetZ - blockZ;
				float distance = (float) Mth.length(dx, dz);
				
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
		
		double returnValue = switch (this.returnType) {
			case CELL_VALUE -> nearestCellValue;
			case DISTANCE_1 -> distance1;
			case DISTANCE_2 -> distance2;
			case DISTANCE_2_ADD -> (distance2 + distance1) * 0.5;
			case DISTANCE_2_SUB -> distance2 - distance1;
			case DISTANCE_2_MUL -> distance2 * distance1 * 0.5;
			case DISTANCE_2_DIV -> distance1 / distance2;
		};
		
		if (this.condition.isPresent()) {
			CellCondition cellCondition = this.condition.get();
			
			if (this.returnType != ReturnType.DISTANCE_2 && isCellPositionInvalid(cellCondition, pos1)) {
				returnValue = cellCondition.fallbackValue;
			}
			
			if (this.returnType != ReturnType.DISTANCE_1 && this.returnType != ReturnType.CELL_VALUE && isCellPositionInvalid(cellCondition, pos2)) {
				returnValue = cellCondition.fallbackValue;
			}
		}
		
		return returnValue;
	}
	
	public double sample3D(int blockX, int blockY, int blockZ) {
		int spacedGridX = Math.floorDiv(blockX, this.gridDimensions.horizontal);
		int spacedGridY = Math.floorDiv(blockY, this.gridDimensions.vertical);
		int spacedGridZ = Math.floorDiv(blockZ, this.gridDimensions.horizontal);
		
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
					
					int targetX = (spacedGridX + xo) * this.gridDimensions.horizontal + random.nextIntBetweenInclusive(0, (int) (this.gridDimensions.horizontal * this.jitter));
					int targetY = (spacedGridY + yo) * this.gridDimensions.vertical + random.nextIntBetweenInclusive(0, (int) (this.gridDimensions.vertical * this.jitter));
					int targetZ = (spacedGridZ + zo) * this.gridDimensions.horizontal + random.nextIntBetweenInclusive(0, (int) (this.gridDimensions.horizontal * this.jitter));
					float cellValue = random.nextFloat();
					
					int dx = targetX - blockX;
					int dy = targetY - blockY;
					int dz = targetZ - blockZ;
					float distance = (float) Mth.length(dx, dy, dz);
					
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
		
		double returnValue = switch (this.returnType) {
			case CELL_VALUE -> nearestCellValue;
			case DISTANCE_1 -> distance1;
			case DISTANCE_2 -> distance2;
			case DISTANCE_2_ADD -> (distance2 + distance1) * 0.5;
			case DISTANCE_2_SUB -> distance2 - distance1;
			case DISTANCE_2_MUL -> distance2 * distance1 * 0.5;
			case DISTANCE_2_DIV -> distance1 / distance2;
		};
		
		if (this.condition.isPresent()) {
			CellCondition cellCondition = this.condition.get();
			
			if (this.returnType != ReturnType.DISTANCE_2 && isCellPositionInvalid(cellCondition, pos1)) {
				returnValue = cellCondition.fallbackValue;
			}
			
			if (this.returnType != ReturnType.DISTANCE_1 && this.returnType != ReturnType.CELL_VALUE && isCellPositionInvalid(cellCondition, pos2)) {
				returnValue = cellCondition.fallbackValue;
			}
		}
		
		return returnValue;
	}
}