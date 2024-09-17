package dev.worldgen.lithostitched.worldgen.structure.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

public record HeightFilterStructureCondition(Optional<Heightmap.Types> heightmap, VerticalAnchor minInclusive, VerticalAnchor maxInclusive) implements StructureCondition {
    public static final MapCodec<HeightFilterStructureCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Heightmap.Types.CODEC.optionalFieldOf("heightmap").forGetter(HeightFilterStructureCondition::heightmap),
        VerticalAnchor.CODEC.fieldOf("min_inclusive").orElse(VerticalAnchor.BOTTOM).forGetter(HeightFilterStructureCondition::minInclusive),
        VerticalAnchor.CODEC.fieldOf("max_inclusive").orElse(VerticalAnchor.TOP).forGetter(HeightFilterStructureCondition::maxInclusive)
    ).apply(instance, HeightFilterStructureCondition::new));

    @Override
    public boolean test(Structure.GenerationContext context, BlockPos pos) {
        int y = this.heightmap.map(type -> context.chunkGenerator().getFirstFreeHeight(pos.getX(), pos.getZ(), type, context.heightAccessor(), context.randomState())).orElse(pos.getY());
        WorldGenerationContext heightContext = new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor());
        return !(y > this.maxInclusive.resolveY(heightContext) || y < this.minInclusive.resolveY(heightContext));
    }

    @Override
    public MapCodec<? extends StructureCondition> codec() {
        return CODEC;
    }
}
