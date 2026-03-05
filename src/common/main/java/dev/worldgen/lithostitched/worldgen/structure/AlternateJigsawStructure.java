package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.msrandom.multiplatform.annotations.Expect;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class AlternateJigsawStructure extends Structure {
    public static final MapCodec<AlternateJigsawStructure> CODEC = RecordCodecBuilder.mapCodec((RecordCodecBuilder.Instance<AlternateJigsawStructure> instance) -> (instance.group(
        settingsCodec(instance),
        AlternateJigsawConfig.CODEC.forGetter(AlternateJigsawStructure::config)
    )).apply(instance, (AlternateJigsawStructure::new))).validate(AlternateJigsawStructure::validate);

    public static final StructureType<AlternateJigsawStructure> TYPE = () -> AlternateJigsawStructure.CODEC;
    private AlternateJigsawConfig config;

    private static DataResult<AlternateJigsawStructure> validate(AlternateJigsawStructure structure) {
        int i = switch (structure.terrainAdaptation()) {
            case NONE -> 0;
            case BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE -> 12;
        };
        return structure.config().maxDistanceFromCenter().horizontal() + i > 128 ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128") : DataResult.success(structure);
    }
    public AlternateJigsawStructure(StructureSettings settings, AlternateJigsawConfig config) {
        super(settings);
        this.config = config;
    }
    
    public void setPoolAliases(List<PoolAliasBinding> poolAliases, boolean append) {
        this.config = this.config.setPoolAliases(poolAliases, append);
    }

    public AlternateJigsawConfig config() {
        return this.config;
    }

    @Override
    public @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int i = config.startHeight().sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), i, chunkPos.getMinBlockZ());
        return generate(context, config, false, config.size().sample(context.random()), blockPos, PoolAliasLookup.create(config.poolAliases(), blockPos, context.seed()));
    }

    @Expect
    public static Optional<Structure.GenerationStub> generate(Structure.GenerationContext context, AlternateJigsawConfig config, boolean vanilla, int size, BlockPos pos, PoolAliasLookup aliasLookup);

    @Override
    public @NotNull StructureType<?> type() {
        return AlternateJigsawStructure.TYPE;
    }
}
