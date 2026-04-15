package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

public class LithostitchedFeaturePoolElement extends StructurePoolElement {
    public static final MapCodec<LithostitchedFeaturePoolElement> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        LithostitchedFeatureConfig.CODEC.forGetter(LithostitchedFeaturePoolElement::config),
        projectionCodec()
    ).apply(i, LithostitchedFeaturePoolElement::new));
    public static final StructurePoolElementType<LithostitchedFeaturePoolElement> TYPE = () -> CODEC;
    private final LithostitchedFeatureConfig config;
    private final CompoundTag defaultJigsawNBT;
    
    protected LithostitchedFeaturePoolElement(LithostitchedFeatureConfig config, Projection projection) {
        super(projection);
        this.config = config;
        this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
    }
    
    public LithostitchedFeatureConfig config() {
        return this.config;
    }
    
    private CompoundTag fillDefaultJigsawNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", this.config().jigsawName().toString());
        tag.putString("final_state", "minecraft:air");
        tag.putString("pool", Pools.EMPTY.toString());
        tag.putString("target", this.config().targetName().toString());
        tag.putString("joint", JigsawBlockEntity.JointType.ROLLABLE.getSerializedName());
        return tag;
    }
    
    @Override
    public Vec3i getSize(final StructureTemplateManager structureTemplateManager, final Rotation rotation) {
        return Vec3i.ZERO;
    }
    
    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(
        final StructureTemplateManager structureTemplateManager, final BlockPos position, final Rotation rotation, final RandomSource random
    ) {
        return List.of(
            new StructureTemplate.StructureBlockInfo(
                position,
                Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(Direction.DOWN, Direction.SOUTH)),
                this.defaultJigsawNBT
            )
        );
    }
    
    @Override
    public BoundingBox getBoundingBox(final StructureTemplateManager structureTemplateManager, final BlockPos position, final Rotation rotation) {
        Vec3i size = this.getSize(structureTemplateManager, rotation);
        return new BoundingBox(
            position.getX(), position.getY(), position.getZ(), position.getX() + size.getX(), position.getY() + size.getY(), position.getZ() + size.getZ()
        );
    }
    
    @Override
    public boolean place(
        final StructureTemplateManager structureTemplateManager,
        final WorldGenLevel level,
        final StructureManager structureManager,
        final ChunkGenerator generator,
        final BlockPos position,
        final BlockPos referencePos,
        final Rotation rotation,
        final BoundingBox chunkBB,
        final RandomSource random,
        final LiquidSettings liquidSettings,
        final boolean keepJigsaws
    ) {
        return this.config.feature().value().place(level, generator, random, position);
    }
    
    @Override
    public StructurePoolElementType<?> getType() {
        return TYPE;
    }
}
