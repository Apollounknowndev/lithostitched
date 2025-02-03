package dev.worldgen.lithostitched.worldgen.structure;

import com.google.common.collect.Lists;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.access.StructurePoolAccess;
import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SequencedPriorityIterator;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;

public class AlternateJigsawGenerator {

    public static Optional<Structure.GenerationStub> generate(Structure.GenerationContext context, AlternateJigsawConfig config, boolean vanilla, int size, BlockPos pos, PoolAliasLookup aliasLookup) {
        RegistryAccess registries = context.registryAccess();
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        StructureTemplateManager structureTemplateManager = context.structureTemplateManager();
        LevelHeightAccessor heightView = context.heightAccessor();
        WorldgenRandom random = context.random();

        Registry<StructureTemplatePool> templatePoolRegistry = registries.lookupOrThrow(Registries.TEMPLATE_POOL);
        Rotation rotation = Rotation.getRandom(random);

        StructurePoolElement startingElement = config.startPool().unwrapKey().flatMap(
            resourceKey -> templatePoolRegistry.getOptional(aliasLookup.lookup(resourceKey))
        ).orElse(config.startPool().value()).getRandomTemplate(random);

        if (startingElement == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        }

        BlockPos blockPos = pos;
        Optional<ResourceLocation> startJigsawName = config.startJigsawName();
        if (startJigsawName.isPresent()) {
            Optional<BlockPos> optional = findNamedJigsaw(startingElement, startJigsawName.get(), pos, rotation, structureTemplateManager, random);
            if (optional.isEmpty()) {
                LithostitchedCommon.LOGGER.error("No starting jigsaw {} found in start pool {}", startJigsawName.get(), config.startPool().unwrapKey().map(key -> key.location().toString()).orElse("<unregistered>"));
                return Optional.empty();
            }

            blockPos = optional.get();
        }

        Vec3i vec3i = blockPos.subtract(pos);
        BlockPos blockPos2 = pos.subtract(vec3i);
        PoolElementStructurePiece poolStructurePiece = new PoolElementStructurePiece(structureTemplateManager, startingElement, blockPos2, startingElement.getGroundLevelDelta(), rotation, startingElement.getBoundingBox(structureTemplateManager, blockPos2, rotation), config.liquidSettings());
        BoundingBox blockBox = poolStructurePiece.getBoundingBox();
        int x = (blockBox.maxX() + blockBox.minX()) / 2;
        int z = (blockBox.maxZ() + blockBox.minZ()) / 2;
        int startHeight;
        startHeight = config.projectStartToHeightmap().map(
            type -> pos.getY() + chunkGenerator.getFirstFreeHeight(x, z, type, heightView, context.randomState())
        ).orElseGet(blockPos2::getY);

        int l = blockBox.minY() + poolStructurePiece.getGroundLevelDelta();
        poolStructurePiece.move(0, startHeight - l, 0);
        int y = startHeight + vec3i.getY();
        return Optional.of(new Structure.GenerationStub(new BlockPos(x, y, z), (collector) -> {
            List<PoolElementStructurePiece> pieces = Lists.newArrayList();
            pieces.add(poolStructurePiece);
            if (size > 0) {
                int maxDistanceFromCenter = config.maxDistanceFromCenter();
                AABB box = new AABB((x - maxDistanceFromCenter), Math.max(y - maxDistanceFromCenter, heightView.getMinY() + config.dimensionPadding().bottom()), (z - maxDistanceFromCenter), (x + maxDistanceFromCenter + 1), Math.min(y + maxDistanceFromCenter + 1, heightView.getMaxY() - config.dimensionPadding().top()), (z + maxDistanceFromCenter + 1));
                VoxelShape voxelShape = Shapes.join(Shapes.create(box), Shapes.create(AABB.of(blockBox)), BooleanOp.ONLY_FIRST);
                generate(vanilla, context.randomState(), size, config.useExpansionHack(), chunkGenerator, structureTemplateManager, heightView, random, templatePoolRegistry, poolStructurePiece, pieces, voxelShape, aliasLookup, config.liquidSettings());
                Objects.requireNonNull(collector);
                pieces.forEach(collector::addPiece);
            }
        }));
    }

    private static Optional<BlockPos> findNamedJigsaw(StructurePoolElement pool, ResourceLocation name, BlockPos pos, Rotation rotation, StructureTemplateManager structureManager, WorldgenRandom random) {
        List<StructureTemplate.JigsawBlockInfo> list = pool.getShuffledJigsawBlocks(structureManager, pos, rotation, random);

        for (StructureTemplate.JigsawBlockInfo jigsawBlock : list) {
            if (name.equals(jigsawBlock.name())) {
                return Optional.of(jigsawBlock.info().pos());
            }
        }

        return Optional.empty();
    }

    private static void generate(boolean vanilla, RandomState noiseConfig, int maxSize, boolean useExpansionHack, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, LevelHeightAccessor heightLimitView, RandomSource random, Registry<StructureTemplatePool> structurePoolRegistry, PoolElementStructurePiece firstPiece, List<PoolElementStructurePiece> pieces, VoxelShape pieceShape, PoolAliasLookup aliasLookup, LiquidSettings liquidSettings) {
        StructurePoolGenerator generator = new StructurePoolGenerator(vanilla, structurePoolRegistry, maxSize, chunkGenerator, structureTemplateManager, pieces, random);
        generator.generatePiece(firstPiece, new MutableObject<>(pieceShape), 0, useExpansionHack, heightLimitView, noiseConfig, aliasLookup, liquidSettings);

        while(generator.pieces.hasNext()) {
            PieceState pieceState = generator.pieces.next();
            generator.generatePiece(pieceState.piece, pieceState.pieceShape, pieceState.depth, useExpansionHack, heightLimitView, noiseConfig, aliasLookup, liquidSettings);
        }
    }

    static final class StructurePoolGenerator {
        private final boolean vanilla;
        private final Registry<StructureTemplatePool> registry;
        private final int maxSize;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super PoolElementStructurePiece> piecesToPlace;
        private final RandomSource random;
        private final Map<StructurePoolElement, Integer> groupCounts = new HashMap<>();
        final SequencedPriorityIterator<PieceState> pieces = new SequencedPriorityIterator<>();

        private StructurePoolGenerator(boolean vanilla, Registry<StructureTemplatePool> registry, int maxSize, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, List<? super PoolElementStructurePiece> children, RandomSource random) {
            this.vanilla = vanilla;
            this.registry = registry;
            this.maxSize = maxSize;
            this.chunkGenerator = chunkGenerator;
            this.structureTemplateManager = structureTemplateManager;
            this.piecesToPlace = children;
            this.random = random;
        }

        private void generatePiece(PoolElementStructurePiece parentPiece, MutableObject<VoxelShape> voxelShape, int depth, boolean useExpansionHack, LevelHeightAccessor world, RandomState noiseConfig, PoolAliasLookup aliasLookup, LiquidSettings liquidSettings) {
            StructurePoolElement anchorElement = parentPiece.getElement();
            BoundingBox parentBoundingBox = parentPiece.getBoundingBox();
            MutableObject<VoxelShape> parentShape = new MutableObject<>();

            for (StructureTemplate.JigsawBlockInfo anchorJigsaw : anchorElement.getShuffledJigsawBlocks(this.structureTemplateManager, parentPiece.getPosition(), parentPiece.getRotation(), this.random)) {
                StructureTemplate.StructureBlockInfo anchorInfo = anchorJigsaw.info();

                BlockPos candidateConnectorPos = adjustJigsawPos(anchorInfo);

                Holder<StructureTemplatePool> poolEntry = getTemplatePoolHolder(getTemplatePoolKey(anchorJigsaw, aliasLookup));
                if (poolEntry == null) continue;
                boolean connectorInParentBoundingBox = parentBoundingBox.isInside(candidateConnectorPos);
                MutableObject<VoxelShape> childShape;
                if (connectorInParentBoundingBox) {
                    childShape = parentShape;
                    if (parentShape.getValue() == null) {
                        parentShape.setValue(Shapes.create(AABB.of(parentBoundingBox)));
                    }
                } else {
                    childShape = voxelShape;
                }

                MutableObject<List<ResourceKey<StructureTemplatePool>>> checkedPools = new MutableObject<>(new ArrayList<>());
                findAndTestChildCandidates(poolEntry, checkedPools, parentPiece, anchorJigsaw, childShape, -1, depth, useExpansionHack, world, noiseConfig, true, aliasLookup, liquidSettings);
            }
        }

        /**
         * Find a valid child from a pool of child candidates.
         * If none are found, go to the template pool's fallback and try again.
         */
        private void findAndTestChildCandidates(Holder<StructureTemplatePool> entry, MutableObject<List<ResourceKey<StructureTemplatePool>>> checkedPools, PoolElementStructurePiece parentPiece, StructureTemplate.JigsawBlockInfo anchorJigsawInfo, MutableObject<VoxelShape> mutableObject2, int k, int depth, boolean useExpansionHack, LevelHeightAccessor world, RandomState noiseConfig, boolean firstIteration, PoolAliasLookup aliasLookup, LiquidSettings liquidSettings) {
            List<StructurePoolElement> childCandidates = this.getPoolElements(entry.unwrapKey().orElse(Pools.EMPTY), checkedPools, depth, firstIteration);

            if (childCandidates.isEmpty()) return;
            boolean foundChild = findValidChildPiece(childCandidates, parentPiece, anchorJigsawInfo, mutableObject2, k, depth, useExpansionHack, world, noiseConfig, aliasLookup, liquidSettings);
            if (!foundChild) {
                findAndTestChildCandidates(entry.value().getFallback(), checkedPools, parentPiece, anchorJigsawInfo, mutableObject2, k, depth, useExpansionHack, world, noiseConfig, false, aliasLookup, liquidSettings);
            }
        }

        private List<StructurePoolElement> getPoolElements(ResourceKey<StructureTemplatePool> poolKey, MutableObject<List<ResourceKey<StructureTemplatePool>>> checkedPools, int depth, boolean firstIteration) {
            // No point grabbing the pool if it's the empty pool
            if (poolKey == Pools.EMPTY) return List.of();

            if (ConfigHandler.getConfig().breaksSeedParity() || !this.vanilla) {
                // If we've already iterated over this pool, don't iterate over it again to prevent infinite looping
                if (checkedPools.getValue().contains(poolKey)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (ResourceKey<StructureTemplatePool> checkedPoolKey : checkedPools.getValue()) {
                        stringBuilder.append(checkedPoolKey.location()).append(" -> ");
                    }
                    stringBuilder.append(poolKey.location());

                    LithostitchedCommon.LOGGER.warn("Template pool fallback chain found: {}", stringBuilder);
                    return List.of();
                }

                checkedPools.getValue().add(poolKey);

                // Get pool to get the elements, start with fallback pool if at max size
                Holder<StructureTemplatePool> pool = this.registry.get(poolKey).orElseThrow();

                // Skip straight to fallback if on max depth
                if (depth == this.maxSize && firstIteration) {
                    pool = pool.value().getFallback();
                }

                return ((StructurePoolAccess)pool.value()).getLithostitchedTemplates().shuffle(random);
            }

            if (!firstIteration) return List.of();

            // Get pool to get the elements, start with fallback pool if at max size
            Holder<StructureTemplatePool> pool = this.registry.get(poolKey).orElseThrow();
            Holder<StructureTemplatePool> fallback = pool.value().getFallback();

            List<StructurePoolElement> elements = new ArrayList<>();

            if (depth != this.maxSize) {
               elements.addAll(pool.value().getShuffledTemplates(this.random));
            }

            elements.addAll(fallback.value().getShuffledTemplates(this.random));

            return elements;
        }

        /**
         * Iterate through list of child candidate pieces to find a valid one to use.
         */
        @SuppressWarnings("deprecation")
        private boolean findValidChildPiece(List<StructurePoolElement> elements, PoolElementStructurePiece parentPiece, StructureTemplate.JigsawBlockInfo anchorJigsaw, MutableObject<VoxelShape> mutableObject2, int k, int depth, boolean useExpansionHack, LevelHeightAccessor world, RandomState noiseConfig, PoolAliasLookup aliasLookup, LiquidSettings liquidSettings) {
            StructureTemplate.StructureBlockInfo anchorInfo = anchorJigsaw.info();

            BlockPos anchorPos = anchorInfo.pos();
            BlockPos candidateConnectorPos = adjustJigsawPos(anchorInfo);
            int parentMinY = parentPiece.getBoundingBox().minY();
            int anchorDistanceToFloor = anchorPos.getY() - parentMinY;
            StructureTemplatePool.Projection parentProjection = parentPiece.getElement().getProjection();
            boolean parentRigid = parentProjection == StructureTemplatePool.Projection.RIGID;

            for (StructurePoolElement element : elements) {
                if (element == EmptyPoolElement.INSTANCE) {
                    return true;
                }

                if (element instanceof DelegatingPoolElement delegating) {
                    if (delegating.config().shouldSkip(depth, this.groupCounts.getOrDefault(delegating, 0))) {
                        continue;
                    }
                }

                for (Rotation rotation : Rotation.getShuffled(this.random)) {
                    List<StructureTemplate.JigsawBlockInfo> connectorJigsaws = element.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, rotation, this.random);
                    BoundingBox connectorBoundingBox = element.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, rotation);

                    // Expansion hack
                    int l;
                    if (useExpansionHack && connectorBoundingBox.getYSpan() <= 16) {
                        l = connectorJigsaws.stream().mapToInt((jigsawInfo) -> {
                            StructureTemplate.StructureBlockInfo blockInfo = jigsawInfo.info();
                            if (!connectorBoundingBox.isInside(adjustJigsawPos(blockInfo))) {
                                return 0;
                            } else {
                                ResourceKey<StructureTemplatePool> registryKey2 = getTemplatePoolKey(jigsawInfo, aliasLookup);
                                Optional<? extends Holder<StructureTemplatePool>> optional1 = this.registry.get(registryKey2);
                                Optional<Holder<StructureTemplatePool>> optional2 = optional1.map(entry -> entry.value().getFallback());
                                int i2 = optional1.map(entry -> entry.value().getMaxSize(this.structureTemplateManager)).orElse(0);
                                int j2 = optional2.map(entry -> entry.value().getMaxSize(this.structureTemplateManager)).orElse(0);
                                return Math.max(i2, j2);
                            }
                        }).max().orElse(0);
                    } else {
                        l = 0;
                    }

                    // Find valid jigsaw block to attach
                    for (StructureTemplate.JigsawBlockInfo connectorJigsawInfo: connectorJigsaws) {
                        if (JigsawBlock.canAttach(anchorJigsaw, connectorJigsawInfo)) {
                            BlockPos connectorPos = connectorJigsawInfo.info().pos();
                            BlockPos blockPos5 = candidateConnectorPos.subtract(connectorPos);
                            BoundingBox blockBox3 = element.getBoundingBox(this.structureTemplateManager, blockPos5, rotation);
                            int m = blockBox3.minY();
                            StructureTemplatePool.Projection connectorProjection = element.getProjection();
                            boolean connectorProjectionRigid = connectorProjection == StructureTemplatePool.Projection.RIGID;
                            int connectorY = connectorPos.getY();
                            int o = anchorDistanceToFloor - connectorY + JigsawBlock.getFrontFacing(anchorInfo.state()).getStepY();
                            int p;
                            if (parentRigid && connectorProjectionRigid) {
                                p = parentMinY + o;
                            } else {
                                if (k == -1) {
                                    k = this.chunkGenerator.getFirstFreeHeight(anchorPos.getX(), anchorPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, world, noiseConfig);
                                }

                                p = k - connectorY;
                            }

                            int q = p - m;
                            BoundingBox blockBox4 = blockBox3.moved(0, q, 0);
                            BlockPos blockPos6 = blockPos5.offset(0, q, 0);


                            int r;
                            if (l > 0) {
                                r = Math.max(l + 1, blockBox4.maxY() - blockBox4.minY());
                                blockBox4.encapsulate(new BlockPos(blockBox4.minX(), blockBox4.minY() + r, blockBox4.minZ()));
                            }


                            if (!Shapes.joinIsNotEmpty(mutableObject2.getValue(), Shapes.create(AABB.of(blockBox4).deflate(0.25)), BooleanOp.ONLY_SECOND)) {
                                if (element instanceof DelegatingPoolElement delegating) {
                                    this.groupCounts.put(delegating, this.groupCounts.getOrDefault(delegating, 0) + 1);
                                }

                                // At this point the piece is ready to be placed
                                mutableObject2.setValue(Shapes.joinUnoptimized(mutableObject2.getValue(), Shapes.create(AABB.of(blockBox4)), BooleanOp.ONLY_FIRST));
                                r = parentPiece.getGroundLevelDelta();
                                int s;
                                if (connectorProjectionRigid) {
                                    s = r - o;
                                } else {
                                    s = element.getGroundLevelDelta();
                                }

                                PoolElementStructurePiece poolStructurePiece = new PoolElementStructurePiece(this.structureTemplateManager, element, blockPos6, s, rotation, blockBox4, liquidSettings);

                                int t;
                                if (parentRigid) {
                                    t = parentMinY + anchorDistanceToFloor;
                                } else if (connectorProjectionRigid) {
                                    t = p + connectorY;
                                } else {
                                    if (k == -1) {
                                        k = this.chunkGenerator.getFirstFreeHeight(anchorPos.getX(), anchorPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, world, noiseConfig);
                                    }

                                    t = k + o / 2;
                                }

                                parentPiece.addJunction(new JigsawJunction(candidateConnectorPos.getX(), t - anchorDistanceToFloor + r, candidateConnectorPos.getZ(), o, connectorProjection));
                                poolStructurePiece.addJunction(new JigsawJunction(anchorPos.getX(), t - connectorY + s, anchorPos.getZ(), -o, parentProjection));

                                this.piecesToPlace.add(poolStructurePiece);
                                if (depth + 1 <= this.maxSize) {
                                    PieceState pieceState = new PieceState(poolStructurePiece, mutableObject2, depth + 1);
                                    this.pieces.add(pieceState, anchorJigsaw.placementPriority());
                                }
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        private Holder<StructureTemplatePool> getTemplatePoolHolder(ResourceKey<StructureTemplatePool> key) {
            Optional<? extends Holder<StructureTemplatePool>> optional = this.registry.get(key);
            if (optional.isEmpty()) {
                LithostitchedCommon.LOGGER.warn("Couldn't find template pool reference: {}", key.location());
            } else {
                Holder<StructureTemplatePool> regularPool = optional.get();
                if ((regularPool.value()).size() == 0) {
                    if (!regularPool.is(Pools.EMPTY)) {
                        LithostitchedCommon.LOGGER.warn("Referenced template pool is empty: {}", key.location());
                    }
                } else {
                    return regularPool;
                }
            }
            return null;
        }

        private static ResourceKey<StructureTemplatePool> getTemplatePoolKey(StructureTemplate.JigsawBlockInfo jigsaw, PoolAliasLookup aliasLookup) {
            return aliasLookup.lookup(Pools.createKey(jigsaw.pool()));
        }

        private static BlockPos adjustJigsawPos(StructureTemplate.StructureBlockInfo jigsawInfo) {
            return jigsawInfo.pos().relative(JigsawBlock.getFrontFacing(jigsawInfo.state()));
        }
    }

    private record PieceState(PoolElementStructurePiece piece, MutableObject<VoxelShape> pieceShape, int depth) {}
}
