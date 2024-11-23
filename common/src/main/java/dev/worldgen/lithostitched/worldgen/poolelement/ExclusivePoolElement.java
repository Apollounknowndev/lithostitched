package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

public abstract class ExclusivePoolElement extends StructurePoolElement {
    private final StructurePoolElement delegate;
    private final int minDepth;

    public static <P extends ExclusivePoolElement> Products.P2<RecordCodecBuilder.Mu<P>, StructurePoolElement, Integer> addFields(RecordCodecBuilder.Instance<P> codec) {
        return codec.group(
                StructurePoolElement.CODEC.fieldOf("delegate").forGetter(ExclusivePoolElement::delegate),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_depth").orElse(0).forGetter(ExclusivePoolElement::minDepth)
        );
    }

    protected ExclusivePoolElement(StructurePoolElement delegate, int minDepth) {
        super(StructureTemplatePool.Projection.TERRAIN_MATCHING);
        if (delegate instanceof ExclusivePoolElement) throw new IllegalStateException("Cannot nest Lithostitched's exclusive pool elements within one another!");
        this.delegate = delegate;
        this.minDepth = minDepth;
    }

    public StructurePoolElement delegate() {
        return this.delegate;
    }

    public Integer minDepth() {
        return this.minDepth;
    }


    @Override
    public Vec3i getSize(StructureTemplateManager structureTemplateManager, Rotation rotation) {
        return this.delegate.getSize(structureTemplateManager, rotation);
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation, RandomSource randomSource) {
        return this.delegate.getShuffledJigsawBlocks(structureTemplateManager, blockPos, rotation, randomSource);
    }

    @Override
    public BoundingBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation) {
        return this.delegate.getBoundingBox(structureTemplateManager, blockPos, rotation);
    }

    @Override
    public boolean place(StructureTemplateManager structureTemplateManager, WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, BlockPos blockPos, BlockPos blockPos1, Rotation rotation, BoundingBox boundingBox, RandomSource randomSource, boolean b) {
        throw new IllegalStateException("Cannot use Lithostitched's limiting pool element outside of the lithostitched:jigsaw structure type!");
    }
}
