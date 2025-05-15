package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Optional;

public class DelegatingPoolElement extends StructurePoolElement {
    public static final MapCodec<DelegatingPoolElement> CODEC = DelegatingConfig.CODEC.xmap(DelegatingPoolElement::new, DelegatingPoolElement::config);
    public static final StructurePoolElementType<DelegatingPoolElement> TYPE = () -> CODEC;

    protected final DelegatingConfig config;

    protected DelegatingPoolElement(DelegatingConfig config) {
        super(config.delegate().getProjection());
        this.config = config;
    }

    protected DelegatingPoolElement(StructurePoolElement delegate, Optional<Integer> minDepth, Optional<Integer> forcedCount, Optional<Integer> maxCount) {
        this(new DelegatingConfig(delegate, Optional.empty(), minDepth.map(min -> Optional.of(new InclusiveRange<>(min, Integer.MAX_VALUE))).orElse(Optional.empty()), forcedCount, maxCount, Optional.empty()));
    }

    public DelegatingConfig config() {
        return this.config;
    }

    public StructurePoolElement delegate() {
        return this.config.delegate();
    }

    public Optional<Integer> minDepth() {
        return this.config.allowedDepth().map(InclusiveRange::minInclusive);
    }

    public boolean prioritized() {
        return this.config.forcedCount().isPresent();
    }

    @Override
    public Vec3i getSize(StructureTemplateManager structureTemplateManager, Rotation rotation) {
        return this.config.delegate().getSize(structureTemplateManager, rotation);
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation, RandomSource randomSource) {
        return this.config.delegate().getShuffledJigsawBlocks(structureTemplateManager, blockPos, rotation, randomSource);
    }

    @Override
    public BoundingBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation) {
        return this.config.delegate().getBoundingBox(structureTemplateManager, blockPos, rotation);
    }

    @Override
    public boolean place(StructureTemplateManager structureTemplateManager, WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, BlockPos blockPos, BlockPos blockPos1, Rotation rotation, BoundingBox boundingBox, RandomSource randomSource, LiquidSettings liquidSettings, boolean b) {
        return this.config.delegate().place(structureTemplateManager, worldGenLevel, structureManager, chunkGenerator, blockPos, blockPos1, rotation, boundingBox, randomSource, liquidSettings, b);
    }

    @Override
    public StructurePoolElement setProjection(StructureTemplatePool.Projection projection) {
        super.setProjection(projection);
        this.config.delegate().setProjection(projection);
        return this;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return TYPE;
    }
}
