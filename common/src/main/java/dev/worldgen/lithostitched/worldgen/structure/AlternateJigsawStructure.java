package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AlternateJigsawStructure extends Structure {
    public static final Codec<AlternateJigsawStructure> CODEC = ExtraCodecs.validate(
        RecordCodecBuilder.mapCodec((instance) -> instance.group(
            settingsCodec(instance),
            AlternateJigsawConfig.CODEC.forGetter(AlternateJigsawStructure::config)
        ).apply(instance, (AlternateJigsawStructure::new))),
        AlternateJigsawStructure::validate
    ).codec();

    public static final StructureType<AlternateJigsawStructure> TYPE = () -> AlternateJigsawStructure.CODEC;
    private final AlternateJigsawConfig config;

    private static DataResult<AlternateJigsawStructure> validate(AlternateJigsawStructure structure) {
        int i = switch (structure.terrainAdaptation()) {
            case NONE -> 0;
            case BURY, BEARD_THIN, BEARD_BOX -> 12;
        };
        return structure.config.maxDistanceFromCenter() + i > 128 ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128") : DataResult.success(structure);
    }
    protected AlternateJigsawStructure(StructureSettings settings, AlternateJigsawConfig config) {
        super(settings);
        this.config = config;
    }

    public AlternateJigsawConfig config() {
        return this.config;
    }

    @Override
    public @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int i = this.config.startHeight().sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), i, chunkPos.getMinBlockZ());
        return AlternateJigsawGenerator.generate(context, this.config, this.config.size().sample(context.random()), blockPos);
    }

    @Override
    public @NotNull StructureType<?> type() {
        return AlternateJigsawStructure.TYPE;
    }
}
