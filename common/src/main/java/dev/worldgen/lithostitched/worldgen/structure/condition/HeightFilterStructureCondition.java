package dev.worldgen.lithostitched.worldgen.structure.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

public record HeightFilterStructureCondition(RangeType rangeType, Optional<Heightmap.Types> heightmap, InclusiveRange<Integer> permittedRange) implements StructureCondition {
    public static final MapCodec<HeightFilterStructureCondition> CODEC = ExtraCodecs.validate(RecordCodecBuilder.mapCodec(instance -> instance.group(
        RangeType.CODEC.fieldOf("range_type").forGetter(HeightFilterStructureCondition::rangeType),
        Heightmap.Types.CODEC.optionalFieldOf("heightmap").forGetter(HeightFilterStructureCondition::heightmap),
        InclusiveRange.INT.fieldOf("permitted_range").forGetter(HeightFilterStructureCondition::permittedRange)
    ).apply(instance, HeightFilterStructureCondition::new)), HeightFilterStructureCondition::validate);

    private DataResult<HeightFilterStructureCondition> validate() {
        if (this.rangeType == RangeType.HEIGHTMAP_RELATIVE && this.heightmap.isEmpty()) {
            return DataResult.error(() -> "Heightmap relative range type must be used with a heightmap");
        }
        return DataResult.success(this);
    }

    @Override
    public boolean test(Structure.GenerationContext context, BlockPos pos) {
        if (this.heightmap.isEmpty()) {
            return this.permittedRange.isValueInRange(pos.getY());
        }

        int heightmapY = context.chunkGenerator().getFirstFreeHeight(pos.getX(), pos.getZ(), this.heightmap.get(), context.heightAccessor(), context.randomState());
        int y = this.rangeType == RangeType.ABSOLUTE ? heightmapY : pos.getY() - heightmapY;

        return this.permittedRange.isValueInRange(y);
    }

    @Override
    public MapCodec<? extends StructureCondition> codec() {
        return CODEC;
    }

    public enum RangeType implements StringRepresentable {
        ABSOLUTE("absolute"),
        HEIGHTMAP_RELATIVE("heightmap_relative");

        public static final Codec<RangeType> CODEC = StringRepresentable.fromEnum(RangeType::values);

        private final String name;

        RangeType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
