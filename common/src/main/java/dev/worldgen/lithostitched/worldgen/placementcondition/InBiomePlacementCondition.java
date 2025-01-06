package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

public record InBiomePlacementCondition(HolderSet<Biome> biomes) implements PlacementCondition {
    public static final MapCodec<InBiomePlacementCondition> CODEC = Biome.LIST_CODEC.fieldOf("biomes").xmap(InBiomePlacementCondition::new, InBiomePlacementCondition::biomes);

    @Override
    public boolean test(Context context, BlockPos pos) {
        Holder<Biome> biome = context.biomeSource().getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()), context.randomState().sampler());
        return this.biomes.contains(biome);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
