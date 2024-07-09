package dev.worldgen.lithostitched.worldgen.structure.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

public record InBiomeStructureCondition(HolderSet<Biome> biomes) implements StructureCondition {
    public static final MapCodec<InBiomeStructureCondition> CODEC = Biome.LIST_CODEC.fieldOf("biomes").xmap(InBiomeStructureCondition::new, InBiomeStructureCondition::biomes);

    @Override
    public boolean test(Structure.GenerationContext context, BlockPos pos) {
        Holder<Biome> biome = context.biomeSource().getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()), context.randomState().sampler());
        return this.biomes.contains(biome);
    }

    @Override
    public MapCodec<? extends StructureCondition> codec() {
        return CODEC;
    }
}
