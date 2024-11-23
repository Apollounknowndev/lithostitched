package dev.worldgen.lithostitched.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.LithostitchedCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
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
        return structure.config().maxDistanceFromCenter() + i > 128 ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128") : DataResult.success(structure);
    }
    protected AlternateJigsawStructure(StructureSettings settings, AlternateJigsawConfig config) {
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
        return AlternateJigsawGenerator.generate(context, config, false, config.size().sample(context.random()), blockPos, PoolAliasLookup.create(config.poolAliases(), blockPos, context.seed()));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return AlternateJigsawStructure.TYPE;
    }
}
