package dev.worldgen.lithostitched.worldgen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.msrandom.multiplatform.annotations.Actual;

import java.util.Optional;

public class AlternateJigsawStructureActual {
    @Actual
    public static Optional<Structure.GenerationStub> generate(Structure.GenerationContext context, AlternateJigsawConfig config, boolean vanilla, int size, BlockPos pos, PoolAliasLookup aliasLookup) {
        return AlternateJigsawGenerator.generate(context, config, vanilla, size, pos, aliasLookup);
    }
}
