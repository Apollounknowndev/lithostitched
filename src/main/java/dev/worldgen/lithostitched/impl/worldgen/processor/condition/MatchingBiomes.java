package dev.worldgen.lithostitched.impl.worldgen.processor.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.processor.enums.ProcessorPosition;
import dev.worldgen.lithostitched.api.worldgen.processorcondition.ProcessorCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

public record MatchingBiomes(HolderSet<Biome> biomes, ProcessorPosition position) implements ProcessorCondition {
    public static final MapCodec<MatchingBiomes> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(MatchingBiomes::biomes),
        ProcessorPosition.CODEC.fieldOf("position").forGetter(MatchingBiomes::position)
    ).apply(instance, MatchingBiomes::new));

    @Override
    public boolean test(WorldGenLevel level, Data data, StructurePlaceSettings settings, RandomSource random) {
        if (!(level.getChunkSource() instanceof ServerChunkCache source)) return false;
        BlockPos pos = this.position.select(data);
        Holder<Biome> biome = source.getGenerator().getBiomeSource().getNoiseBiome(
            QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()),
            source.randomState().sampler()
        );
        return this.biomes.contains(biome);
    }

    @Override
    public MapCodec<? extends ProcessorCondition> codec() {
        return CODEC;
    }
}