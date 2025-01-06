package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class DelegatingStructure extends Structure {
    public static final MapCodec<DelegatingStructure> CODEC = DelegatingConfig.CODEC.xmap(DelegatingStructure::new, DelegatingStructure::config);
    public static final StructureType<DelegatingStructure> TYPE = () -> DelegatingStructure.CODEC;
    private final DelegatingConfig config;

    public DelegatingStructure(DelegatingConfig config) {
        super(createSettings(config));
        this.config = config;
    }

    public DelegatingConfig config() {
        return this.config;
    }

    public Structure delegate() {
        return this.config.delegate().value();
    }

    @Override
    public Optional<GenerationStub> findValidGenerationPoint(GenerationContext context) {
        return this.findGenerationPoint(context).filter(generationPoint -> isValid(generationPoint, context));
    }

    private boolean isValid(GenerationStub generationPoint, GenerationContext context) {
        BlockPos pos = generationPoint.position();
        if (!this.config.spawnCondition().test(context, pos)) return false;
        return context.validBiome().test(context.chunkGenerator().getBiomeSource().getNoiseBiome(QuartPos.fromBlock(pos.getX()), QuartPos.fromBlock(pos.getY()), QuartPos.fromBlock(pos.getZ()), context.randomState().sampler()));
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        return this.delegate().findValidGenerationPoint(context);
    }

    @Override
    public StructureType<?> type() {
        return TYPE;
    }

    private static StructureSettings createSettings(DelegatingConfig config) {
        Structure delegate = config.delegate().value();
        return new StructureSettings(
            delegate.biomes(),
            delegate.spawnOverrides(),
            delegate.step(),
            delegate.terrainAdaptation()
        );
    }
}
