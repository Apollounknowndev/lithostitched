package dev.worldgen.lithostitched.worldgen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.access.StructurePoolAccess;
import dev.worldgen.lithostitched.worldgen.poolelement.ExclusivePoolElement;
import dev.worldgen.lithostitched.worldgen.poolelement.GuaranteedPoolElement;
import dev.worldgen.lithostitched.worldgen.poolelement.LimitedPoolElement;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
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
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;

public class AlternateJigsawGenerator {

    public static Optional<Structure.GenerationStub> generate(Structure.GenerationContext context, Holder<StructureTemplatePool> structurePool, Optional<ResourceLocation> id, int size, BlockPos pos, boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter) {
        RegistryAccess dynamicRegistryManager = context.registryAccess();
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        StructureTemplateManager structureTemplateManager = context.structureTemplateManager();
        LevelHeightAccessor heightLimitView = context.heightAccessor();
        WorldgenRandom chunkRandom = context.random();
        Registry<StructureTemplatePool> registry = dynamicRegistryManager.registryOrThrow(Registries.TEMPLATE_POOL);
        Rotation blockRotation = Rotation.getRandom(chunkRandom);
        StructureTemplatePool structurePool2 = structurePool.value();
        StructurePoolElement structurePoolElement = structurePool2.getRandomTemplate(chunkRandom);
        if (structurePoolElement == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        } else {
            BlockPos blockPos;
            if (id.isPresent()) {
                ResourceLocation identifier = id.get();
                Optional<BlockPos> optional = findStartingJigsawPos(structurePoolElement, identifier, pos, blockRotation, structureTemplateManager, chunkRandom);
                if (optional.isEmpty()) {
                    LithostitchedCommon.LOGGER.error("No starting jigsaw {} found in start pool {}", identifier, structurePool.unwrapKey().map((key) -> key.location().toString()).orElse("<unregistered>"));
                    return Optional.empty();
                }

                blockPos = optional.get();
            } else {
                blockPos = pos;
            }

            Vec3i vec3i = blockPos.subtract(pos);
            BlockPos blockPos2 = pos.subtract(vec3i);
            PoolElementStructurePiece poolStructurePiece = new PoolElementStructurePiece(structureTemplateManager, structurePoolElement, blockPos2, structurePoolElement.getGroundLevelDelta(), blockRotation, structurePoolElement.getBoundingBox(structureTemplateManager, blockPos2, blockRotation));
            BoundingBox blockBox = poolStructurePiece.getBoundingBox();
            int i = (blockBox.maxX() + blockBox.minX()) / 2;
            int j = (blockBox.maxZ() + blockBox.minZ()) / 2;
            int k;
            k = projectStartToHeightmap.map(
                type -> pos.getY() + chunkGenerator.getFirstFreeHeight(i, j, type, heightLimitView, context.randomState())
            ).orElseGet(blockPos2::getY);

            int l = blockBox.minY() + poolStructurePiece.getGroundLevelDelta();
            poolStructurePiece.move(0, k - l, 0);
            int m = k + vec3i.getY();
            return Optional.of(new Structure.GenerationStub(new BlockPos(i, m, j), (collector) -> {
                List<PoolElementStructurePiece> list = Lists.newArrayList();
                list.add(poolStructurePiece);
                if (size > 0) {
                    AABB box = new AABB((i - maxDistanceFromCenter), (m - maxDistanceFromCenter), (j - maxDistanceFromCenter), (i + maxDistanceFromCenter + 1), (m + maxDistanceFromCenter + 1), (j + maxDistanceFromCenter + 1));
                    VoxelShape voxelShape = Shapes.join(Shapes.create(box), Shapes.create(AABB.of(blockBox)), BooleanOp.ONLY_FIRST);
                    generate(context.randomState(), size, useExpansionHack, chunkGenerator, structureTemplateManager, heightLimitView, chunkRandom, registry, poolStructurePiece, list, voxelShape);
                    Objects.requireNonNull(collector);
                    list.forEach(collector::addPiece);
                }
            }));
        }
    }

    private static Optional<BlockPos> findStartingJigsawPos(StructurePoolElement pool, ResourceLocation id, BlockPos pos, Rotation rotation, StructureTemplateManager structureManager, WorldgenRandom random) {
        List<StructureTemplate.StructureBlockInfo> list = pool.getShuffledJigsawBlocks(structureManager, pos, rotation, random);
        Optional<BlockPos> optional = Optional.empty();
        for (StructureTemplate.StructureBlockInfo structureBlockInfo : list) {
            if (structureBlockInfo.nbt() == null) continue;
            ResourceLocation identifier = ResourceLocation.tryParse(structureBlockInfo.nbt().getString("name"));
            if (id.equals(identifier)) {
                optional = Optional.of(structureBlockInfo.pos());
                break;
            }
        }

        return optional;
    }

    private static void generate(RandomState noiseConfig, int maxSize, boolean useExpansionHack, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, LevelHeightAccessor heightLimitView, RandomSource random, Registry<StructureTemplatePool> structurePoolRegistry, PoolElementStructurePiece firstPiece, List<PoolElementStructurePiece> pieces, VoxelShape pieceShape) {
        StructurePoolGenerator structurePoolGenerator = new StructurePoolGenerator(structurePoolRegistry, maxSize, chunkGenerator, structureTemplateManager, pieces, random);
        structurePoolGenerator.structurePieces.addLast(new ShapedPoolStructurePiece(firstPiece, new MutableObject<>(pieceShape), 0));

        while(!structurePoolGenerator.structurePieces.isEmpty()) {
            ShapedPoolStructurePiece shapedPoolStructurePiece = structurePoolGenerator.structurePieces.removeFirst();
            structurePoolGenerator.generatePiece(shapedPoolStructurePiece.piece, shapedPoolStructurePiece.pieceShape, shapedPoolStructurePiece.currentSize, useExpansionHack, heightLimitView, noiseConfig);
        }

    }

    static final class StructurePoolGenerator {
        private final Registry<StructureTemplatePool> registry;
        private final int maxSize;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super PoolElementStructurePiece> piecesToPlace;
        private final RandomSource random;
        private final Map<ExclusivePoolElement, Integer> elementsToCounts;
        final Deque<ShapedPoolStructurePiece> structurePieces = Queues.newArrayDeque();

        StructurePoolGenerator(Registry<StructureTemplatePool> registry, int maxSize, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, List<? super PoolElementStructurePiece> children, RandomSource random) {
            this.registry = registry;
            this.maxSize = maxSize;
            this.chunkGenerator = chunkGenerator;
            this.structureTemplateManager = structureTemplateManager;
            this.piecesToPlace = children;
            this.random = random;
            this.elementsToCounts = new HashMap<>();
        }

        void generatePiece(PoolElementStructurePiece parentPiece, MutableObject<VoxelShape> voxelShape, int depth, boolean useExpansionHack, LevelHeightAccessor world, RandomState noiseConfig) {
            StructurePoolElement anchorElement = parentPiece.getElement();
            MutableObject<VoxelShape> parentShape = new MutableObject<>();

            for (StructureTemplate.StructureBlockInfo anchorJigsawInfo : anchorElement.getShuffledJigsawBlocks(this.structureTemplateManager, parentPiece.getPosition(), parentPiece.getRotation(), this.random)) {
                BoundingBox parentBoundingBox = parentPiece.getBoundingBox();
                BlockPos candidateConnectorPos = anchorJigsawInfo.pos().relative(JigsawBlock.getFrontFacing(anchorJigsawInfo.state()));
                int k = -1;
                Holder<StructureTemplatePool> poolEntry = getStructurePoolEntry(getPoolKey(anchorJigsawInfo));
                if (poolEntry == null) return;
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


                /*
                List<Tuple<StructurePoolElement, Optional<AlternateJigsawStructure.GuaranteedElement>>> childCandidates = Lists.newArrayList();
                if (this.depthsWithGuaranteedElements.stream().sorted().findFirst().orElse(64) <= depth) {
                    List<AlternateJigsawStructure.GuaranteedElement> forcedElementsInStep = this.guaranteedElements.stream().filter(element -> element.minDepth() <= depth && element.acceptablePools().contains(poolEntry)).toList();
                    for (AlternateJigsawStructure.GuaranteedElement forcedElement : forcedElementsInStep) {
                        childCandidates.add(new Tuple<>(forcedElement.element(), Optional.of(forcedElement)));
                    }
                }
                childCandidates.addAll(collectChildCandidateList(getPoolKey(anchorJigsawInfo), depth, true));
                */

                findAndTestChildCandidates(poolEntry, collectChildCandidateList(getPoolKey(anchorJigsawInfo), depth, true), parentPiece, anchorJigsawInfo, childShape, k, depth, useExpansionHack, world, noiseConfig);
            }
        }

        /**
         * Find a valid child from a pool of child candidates.
         * If none are found, go to the template pool's fallback and try again.
         */
        private void findAndTestChildCandidates(Holder<StructureTemplatePool> fallbackEntry, List<StructurePoolElement> childCandidates, PoolElementStructurePiece parentPiece, StructureTemplate.StructureBlockInfo anchorJigsawInfo, MutableObject<VoxelShape> mutableObject2, int k, int depth, boolean useExpansionHack, LevelHeightAccessor world, RandomState noiseConfig) {
            if (childCandidates.isEmpty()) return;
            boolean foundChild = findValidChildPiece(childCandidates, parentPiece, anchorJigsawInfo, mutableObject2, k, depth, useExpansionHack, world, noiseConfig);
            if (!foundChild) {
                findAndTestChildCandidates(fallbackEntry.value().getFallback(), collectChildCandidateList(fallbackEntry.value().getFallback().unwrapKey().orElse(Pools.EMPTY), depth, false), parentPiece, anchorJigsawInfo, mutableObject2, k, depth, useExpansionHack, world, noiseConfig);
            }
        }

        private List<StructurePoolElement> collectChildCandidateList(ResourceKey<StructureTemplatePool> poolKey, int depth, boolean firstIteration) {
            Holder<StructureTemplatePool> pool = this.registry.getHolder(poolKey).orElseThrow();

            if (depth == this.maxSize && firstIteration) {
                pool = pool.value().getFallback();
            }

            if (pool.unwrapKey().isPresent() && pool.unwrapKey().get() == Pools.EMPTY) return List.of();

            if (pool == pool.value().getFallback()) {
                LithostitchedCommon.LOGGER.warn("Template pool fallback references itself: {}", pool.unwrapKey().map(ResourceKey::toString).orElse("<unregistered>"));
                return List.of();
            }

            // Create the list of child candidates, always giving priority to guaranteed elements.
            ShufflingList<StructurePoolElement> structurePoolElementsList = ((StructurePoolAccess)pool.value()).lithostitched$getStructurePoolElements().shuffle();

            List<StructurePoolElement> elements = new ArrayList<>(structurePoolElementsList.stream().filter(element -> element instanceof GuaranteedPoolElement guaranteedElement && guaranteedElement.minDepth() <= depth).toList());
            elements.addAll(structurePoolElementsList.stream().filter(element -> !elements.contains(element)).toList());

            return elements.stream().toList();
        }

        /**
         * Iterate through list of child candidate pieces to find a valid one to use.
         */
        @SuppressWarnings("deprecation")
        private boolean findValidChildPiece(List<StructurePoolElement> childCandidates, PoolElementStructurePiece parentPiece, StructureTemplate.StructureBlockInfo anchorJigsawInfo, MutableObject<VoxelShape> mutableObject2, int k, int depth, boolean useExpansionHack, LevelHeightAccessor world, RandomState noiseConfig) {
            BlockPos anchorPos = anchorJigsawInfo.pos();
            BlockPos candidateConnectorPos = anchorPos.relative(JigsawBlock.getFrontFacing(anchorJigsawInfo.state()));
            int parentMinY = parentPiece.getBoundingBox().minY();
            int anchorDistanceToFloor = anchorPos.getY() - parentMinY;
            StructureTemplatePool.Projection parentProjection = parentPiece.getElement().getProjection();
            boolean parentRigid = parentProjection == StructureTemplatePool.Projection.RIGID;

            for (StructurePoolElement candidateElement : childCandidates.stream().distinct().toList()) {
                if (candidateElement == EmptyPoolElement.INSTANCE) {
                    return true;
                }

                // Handle Lithostitched-exclusive pool element types
                StructurePoolElement processedCandidateElement;
                if (candidateElement instanceof ExclusivePoolElement exclusiveElement) {
                    if (exclusiveElement instanceof LimitedPoolElement limitedElement) {
                        if (!this.elementsToCounts.containsKey(limitedElement)) {
                            this.elementsToCounts.put(limitedElement, limitedElement.limit());
                        }
                        if (this.elementsToCounts.get(limitedElement) < 1) {
                            continue;
                        }
                    } else if (exclusiveElement instanceof GuaranteedPoolElement guaranteedElement) {
                        if (!this.elementsToCounts.containsKey(guaranteedElement)) {
                            this.elementsToCounts.put(guaranteedElement, 0);
                        }
                        if (this.elementsToCounts.get(guaranteedElement) >= guaranteedElement.count()) {
                            continue;
                        }
                    }
                    processedCandidateElement = exclusiveElement.delegate();
                } else {
                    processedCandidateElement = candidateElement;
                }

                label125:
                for (Rotation rotation : Rotation.getShuffled(this.random)) {
                    List<StructureTemplate.StructureBlockInfo> connectorJigsaws = processedCandidateElement.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, rotation, this.random);
                    BoundingBox connectorBoundingBox = processedCandidateElement.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, rotation);


                    // Expansion hack
                    int l;
                    if (useExpansionHack && connectorBoundingBox.getYSpan() <= 16) {
                        l = connectorJigsaws.stream().mapToInt((blockInfo) -> {
                            if (!connectorBoundingBox.isInside(blockInfo.pos().relative(JigsawBlock.getFrontFacing(blockInfo.state())))) {
                                return 0;
                            } else {
                                ResourceKey<StructureTemplatePool> registryKey2 = getPoolKey(blockInfo);
                                Optional<? extends Holder<StructureTemplatePool>> optional1 = this.registry.getHolder(registryKey2);
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
                    for (StructureTemplate.StructureBlockInfo connectorJigsawInfo: connectorJigsaws) {
                        if (JigsawBlock.canAttach(anchorJigsawInfo, connectorJigsawInfo)) {
                            BlockPos connectorPos = connectorJigsawInfo.pos();
                            BlockPos blockPos5 = candidateConnectorPos.subtract(connectorPos);
                            BoundingBox blockBox3 = processedCandidateElement.getBoundingBox(this.structureTemplateManager, blockPos5, rotation);
                            int m = blockBox3.minY();
                            StructureTemplatePool.Projection connectorProjection = processedCandidateElement.getProjection();
                            boolean connectorProjectionRigid = connectorProjection == StructureTemplatePool.Projection.RIGID;
                            int connectorY = connectorPos.getY();
                            int o = anchorDistanceToFloor - connectorY + JigsawBlock.getFrontFacing(anchorJigsawInfo.state()).getStepY();
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
                                if (candidateElement instanceof ExclusivePoolElement exclusiveElement) {
                                    if (exclusiveElement instanceof LimitedPoolElement limitedElement) {
                                        this.elementsToCounts.put(limitedElement, this.elementsToCounts.get(limitedElement) - 1);
                                    } else if (exclusiveElement instanceof GuaranteedPoolElement guaranteedElement) {
                                        this.elementsToCounts.put(guaranteedElement, this.elementsToCounts.get(guaranteedElement) + 1);
                                    }
                                }

                                // At this point the piece is ready to be placed
                                mutableObject2.setValue(Shapes.joinUnoptimized(mutableObject2.getValue(), Shapes.create(AABB.of(blockBox4)), BooleanOp.ONLY_FIRST));
                                r = parentPiece.getGroundLevelDelta();
                                int s;
                                if (connectorProjectionRigid) {
                                    s = r - o;
                                } else {
                                    s = processedCandidateElement.getGroundLevelDelta();
                                }

                                PoolElementStructurePiece poolStructurePiece = new PoolElementStructurePiece(this.structureTemplateManager, processedCandidateElement, blockPos6, s, rotation, blockBox4);

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

                                parentPiece.addJunction(new JigsawJunction(candidateConnectorPos.getX(), t - anchorDistanceToFloor + r,
                                        candidateConnectorPos.getZ(), o, connectorProjection));
                                poolStructurePiece.addJunction(new JigsawJunction(anchorPos.getX(), t - connectorY + s, anchorPos.getZ(), -o, parentProjection));

                                this.piecesToPlace.add(poolStructurePiece);
                                if (depth + 1 <= this.maxSize) {
                                    this.structurePieces.addLast(new ShapedPoolStructurePiece(poolStructurePiece, mutableObject2, depth + 1));
                                }
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }


        private Holder<StructureTemplatePool> getStructurePoolEntry(ResourceKey<StructureTemplatePool> key) {
            Optional<? extends Holder<StructureTemplatePool>> optional = this.registry.getHolder(key);
            if (optional.isEmpty()) {
                LithostitchedCommon.LOGGER.warn("Non-existent template pool reference: {}", key.location());
            } else {
                Holder<StructureTemplatePool> regularPool = optional.get();
                if ((regularPool.value()).size() == 0) {
                    if (!regularPool.is(Pools.EMPTY)) {
                        LithostitchedCommon.LOGGER.warn("Empty template pool reference: {}", key.location());
                    }
                } else {
                    return regularPool;
                }
            }
            return null;
        }

        private static ResourceKey<StructureTemplatePool> getPoolKey(StructureTemplate.StructureBlockInfo blockInfo) {
            return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(blockInfo.nbt().getString("pool")));
        }
    }

    private record ShapedPoolStructurePiece(PoolElementStructurePiece piece, MutableObject<VoxelShape> pieceShape, int currentSize) {}
}
