package dev.worldgen.lithostitched.api.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.worldgen.processor.enums.RandomMode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public record RandomSettings(RandomMode mode, Identifier name) {
    private static final Codec<RandomSettings> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RandomMode.CODEC.fieldOf("mode").orElse(RandomMode.PER_BLOCK).forGetter(RandomSettings::mode),
        Identifier.CODEC.fieldOf("name").forGetter(RandomSettings::name)
    ).apply(instance, RandomSettings::new));

    public RandomSettings(RandomMode mode) {
        this(mode, Lithostitched.id("default"));
    }

    public static final Codec<RandomSettings> CODEC = Codec.withAlternative(
        FULL_CODEC,
        RandomMode.CODEC,
        RandomSettings::new
    );

    public RandomSource create(WorldGenLevel level, BlockPos piecePos, BlockPos pivotPos, StructureTemplate.StructureBlockInfo processedBlockInfo) {
        return RandomSource.create(level.getSeed() + this.name.hashCode()).forkPositional().at(this.mode.select(piecePos, pivotPos, processedBlockInfo));
    }
}
