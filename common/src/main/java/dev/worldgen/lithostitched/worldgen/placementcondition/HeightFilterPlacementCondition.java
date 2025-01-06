package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

public record HeightFilterPlacementCondition(RangeType rangeType, Optional<Heightmap.Types> heightmap, InclusiveRange<Integer> permittedRange) implements PlacementCondition {
    public static final MapCodec<HeightFilterPlacementCondition> CODEC = RecordCodecBuilder.<HeightFilterPlacementCondition>mapCodec(instance -> instance.group(
        RangeType.CODEC.fieldOf("range_type").forGetter(HeightFilterPlacementCondition::rangeType),
        Heightmap.Types.CODEC.optionalFieldOf("heightmap").forGetter(HeightFilterPlacementCondition::heightmap),
        InclusiveRange.INT.fieldOf("permitted_range").forGetter(HeightFilterPlacementCondition::permittedRange)
    ).apply(instance, HeightFilterPlacementCondition::new)).validate(HeightFilterPlacementCondition::validate);

    private DataResult<HeightFilterPlacementCondition> validate() {
        if (this.rangeType == RangeType.HEIGHTMAP_RELATIVE && this.heightmap.isEmpty()) {
            return DataResult.error(() -> "Heightmap relative range type must be used with a heightmap");
        }
        return DataResult.success(this);
    }

    @Override
    public boolean test(Context context, BlockPos pos) {
        if (this.heightmap.isEmpty()) {
            return this.permittedRange.isValueInRange(pos.getY());
        }

        int heightmapY = context.generator().getFirstFreeHeight(pos.getX(), pos.getZ(), this.heightmap.get(), context.heightAccessor(), context.randomState());
        int y = this.rangeType == RangeType.ABSOLUTE ? heightmapY : pos.getY() - heightmapY;

        return this.permittedRange.isValueInRange(y);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
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
