package dev.worldgen.lithostitched.worldgen.structure;

import com.google.common.collect.Lists;
import dev.worldgen.apollib.Apollib;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.worldgen.structure.MaxDistanceFromCenter;
import dev.worldgen.lithostitched.duck.StructurePoolAccess;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingConfig;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SequencedPriorityIterator;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
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
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;

public class AlternateJigsawGenerator {
    public static Optional<Structure.GenerationStub> generate(Structure.GenerationContext context, AlternateJigsawConfig config, boolean vanilla, int size, BlockPos pos, PoolAliasLookup aliasLookup) {
        RegistryAccess registries = context.registryAccess();
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        StructureTemplateManager structureTemplateManager = context.structureTemplateManager();
        LevelHeightAccessor heightLimitView = context.heightAccessor();
        WorldgenRandom random = context.random();

        Registry<StructureTemplatePool> registry = Apollib.registry(registries, Registries.TEMPLATE_POOL);
        Rotation rotation = config.fixedRotation() ? Rotation.NONE : Rotation.getRandom(random);

        StructurePoolElement startingElement = config.startPool().unwrapKey().flatMap(
                (resourceKey) -> registry.getOptional(aliasLookup.lookup(resourceKey))
        ).orElse(config.startPool().value()).getRandomTemplate(random);

        if (startingElement == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        }

        BlockPos startPos;
        Optional<ResourceLocation> startJigsawName = config.startJigsawName();
        if (startJigsawName.isPresent()) {
            ResourceLocation identifier = startJigsawName.get();
            Optional<BlockPos> optional = findNamedJigsaw(startingElement, identifier, pos, rotation, structureTemplateManager, random);
            if (optional.isEmpty()) {
                Lithostitched.LOGGER.error("No starting jigsaw {} found in start pool {}", identifier, config.startPool().unwrapKey().map((key) -> key.location().toString()).orElse("<unregistered>"));
                return Optional.empty();
            }

            startPos = optional.get();
        } else {
            startPos = pos;
        }

        Vec3i vec3i = startPos.subtract(pos);
        BlockPos blockPos2 = pos.subtract(vec3i);
        PoolElementStructurePiece piece = new PoolElementStructurePiece(structureTemplateManager, startingElement, blockPos2, startingElement.getGroundLevelDelta(), rotation, startingElement.getBoundingBox(structureTemplateManager, blockPos2, rotation), config.liquidSettings());
        BoundingBox blockBox = piece.getBoundingBox();
        int originX = (blockBox.maxX() + blockBox.minX()) / 2;
        int originZ = (blockBox.maxZ() + blockBox.minZ()) / 2;
        Optional<Integer> y;
        y = config.startProjection().map(
            either -> either.map(
                snap -> snap.findY(new BlockPos(originX, blockPos2.getY(), originZ), context, heightLimitView, context.randomState()),
                type -> Optional.of(pos.getY() + chunkGenerator.getFirstFreeHeight(originX, originZ, type, heightLimitView, context.randomState()))
            )
        ).orElseGet(() -> Optional.of(blockPos2.getY()));

        if (y.isEmpty()) return Optional.empty();

        int l = blockBox.minY() + piece.getGroundLevelDelta();
        piece.move(0, y.get() - l, 0);

        if (pieceWithinPaddingBounds(heightLimitView, config.dimensionPadding(), piece.getBoundingBox())) {
            return Optional.empty();
        }

        int originY = y.get() + vec3i.getY();
        return Optional.of(new Structure.GenerationStub(new BlockPos(originX, originY, originZ), (collector) -> {
            List<PoolElementStructurePiece> list = Lists.newArrayList();
            list.add(piece);
            if (size > 0) {
                MaxDistanceFromCenter maxDistance = config.maxDistanceFromCenter();
                AABB box = new AABB(
                        originX - maxDistance.horizontal(),
                        Math.max(originY - maxDistance.vertical(), heightLimitView.getMinBuildHeight() + config.dimensionPadding().bottom()),
                        originZ - maxDistance.horizontal(),
                        originX + maxDistance.horizontal() + 1,
                        Math.min(originY + maxDistance.vertical() + 1, heightLimitView.getMaxBuildHeight()  - config.dimensionPadding().top()),
                        originZ + maxDistance.horizontal() + 1
                );
                BoxOctree boxOctree = new BoxOctree(box);
                if (!getConfig(startingElement).otherPiecesCanIntersect()) {
                    boxOctree.addBox(AABB.of(blockBox));
                }
                generatePieces(context, vanilla, size, config.useExpansionHack(), chunkGenerator, structureTemplateManager, heightLimitView, random, registry, piece, list, boxOctree, aliasLookup, config.liquidSettings());
            }
            Objects.requireNonNull(collector);
            list.forEach(collector::addPiece);
        }));
    }
    
    private static boolean pieceWithinPaddingBounds(LevelHeightAccessor levelHeightAccessor, DimensionPadding dimensionPadding, BoundingBox boundingBox) {
        if (dimensionPadding == DimensionPadding.ZERO) {
            return false;
        } else {
            int minY = levelHeightAccessor.getMinBuildHeight() + dimensionPadding.bottom();
            int maxY = levelHeightAccessor.getMaxBuildHeight() - dimensionPadding.top();
            return boundingBox.minY() < minY || boundingBox.maxY() > maxY;
        }
    }
    
    private static Optional<BlockPos> findNamedJigsaw(StructurePoolElement pool, ResourceLocation id, BlockPos pos, Rotation rotation, StructureTemplateManager structureManager, WorldgenRandom random) {
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

    private static void generatePieces(Structure.GenerationContext context, boolean vanilla, int maxSize, boolean useExpansionHack, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, LevelHeightAccessor heightLimitView, RandomSource random, Registry<StructureTemplatePool> structurePoolRegistry, PoolElementStructurePiece firstPiece, List<PoolElementStructurePiece> pieces, BoxOctree boxOctree, PoolAliasLookup aliasLookup, LiquidSettings liquidSettings) {
        StructurePoolGenerator generator = new StructurePoolGenerator(context, vanilla, structurePoolRegistry, maxSize, chunkGenerator, structureTemplateManager, pieces, random);
        generator.generatePiece(firstPiece, boxOctree, 0, useExpansionHack, heightLimitView, aliasLookup, liquidSettings);

        while(generator.pieces.hasNext()) {
            PieceState pieceState = generator.pieces.next();
            generator.generatePiece(pieceState.piece, pieceState.octree, pieceState.currentSize, useExpansionHack, heightLimitView, aliasLookup, liquidSettings);
        }
    }

    private static DelegatingConfig getConfig(StructurePoolElement element) {
        return element instanceof DelegatingPoolElement delegating ? delegating.config() : new DelegatingConfig(element);
    }

    static final class StructurePoolGenerator {
        private final Structure.GenerationContext context;
        private final boolean vanilla;
        private final Registry<StructureTemplatePool> registry;
        private final int maxSize;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super PoolElementStructurePiece> piecesToPlace;
        private final RandomSource random;
        private final Map<ResourceLocation, Integer> groupCounts = new HashMap<>();
        final SequencedPriorityIterator<PieceState> pieces = new SequencedPriorityIterator<>();

        private StructurePoolGenerator(Structure.GenerationContext context, boolean vanilla, Registry<StructureTemplatePool> registry, int maxSize, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, List<? super PoolElementStructurePiece> children, RandomSource random) {
            this.context = context;
            this.vanilla = vanilla;
            this.registry = registry;
            this.maxSize = maxSize;
            this.chunkGenerator = chunkGenerator;
            this.structureTemplateManager = structureTemplateManager;
            this.piecesToPlace = children;
            this.random = random;
        }
        
        private void generatePiece(PoolElementStructurePiece parentPiece, BoxOctree parentOctree, int depth, boolean useExpansionHack, LevelHeightAccessor world, PoolAliasLookup aliasLookup, LiquidSettings liquidSettings) {
            StructurePoolElement anchorElement = parentPiece.getElement();
            BoundingBox parentBoundingBox = parentPiece.getBoundingBox();
            BoxOctree directParentOctree = null;
            
            for (StructureTemplate.StructureBlockInfo anchorJigsawInfo : anchorElement.getShuffledJigsawBlocks(this.structureTemplateManager, parentPiece.getPosition(), parentPiece.getRotation(), this.random)) {
                BlockPos candidateConnectorPos = anchorJigsawInfo.pos().relative(JigsawBlock.getFrontFacing(anchorJigsawInfo.state()));
                Holder<StructureTemplatePool> poolEntry = getTemplatePoolHolder(getTemplatePoolKey(anchorJigsawInfo, aliasLookup), candidateConnectorPos);
                if (poolEntry == null) continue;
                boolean connectorInParentBoundingBox = parentBoundingBox.isInside(candidateConnectorPos);
                
                BoxOctree octree;
                if (connectorInParentBoundingBox && !getConfig(anchorElement).otherPiecesCanIntersect()) {
                    if (directParentOctree == null) {
                        directParentOctree = new BoxOctree(AABB.of(parentBoundingBox));
                    }
                    octree = directParentOctree;
                } else {
                    octree = parentOctree;
                }
                
                MutableObject<List<ResourceKey<StructureTemplatePool>>> checkedPools = new MutableObject<>(new ArrayList<>());
                findAndTestChildCandidates(poolEntry, checkedPools, parentPiece, anchorJigsawInfo, octree, -1, depth, useExpansionHack, world, true, aliasLookup, liquidSettings);
            }
        }
        
        private static ResourceKey<StructureTemplatePool> getTemplatePoolKey(StructureTemplate.StructureBlockInfo info, PoolAliasLookup aliasLookup) {
            CompoundTag compoundTag = Objects.requireNonNull(info.nbt(), () -> info + " nbt was null");
            ResourceKey<StructureTemplatePool> resourceKey = Pools.parseKey(compoundTag.getString("pool"));
            
            return aliasLookup.lookup(resourceKey);
        }

        /**
         * Find a valid child from a pool of child candidates.
         * If none are found, go to the template pool's fallback and try again.
         */
        private void findAndTestChildCandidates(Holder<StructureTemplatePool> entry, MutableObject<List<ResourceKey<StructureTemplatePool>>> checkedPools, PoolElementStructurePiece parentPiece, StructureTemplate.StructureBlockInfo anchorJigsawInfo, BoxOctree octree, int k, int depth, boolean useExpansionHack, LevelHeightAccessor world, boolean firstIteration, PoolAliasLookup aliasLookup, LiquidSettings liquidSettings) {
            List<StructurePoolElement> childCandidates = this.getPoolElements(entry.unwrapKey().orElse(Pools.EMPTY), checkedPools, depth, firstIteration);
            
            if (childCandidates.isEmpty()) return;
            boolean foundChild = findValidChildPiece(childCandidates, parentPiece, anchorJigsawInfo, octree, k, depth, useExpansionHack, world, aliasLookup, liquidSettings);
            if (!foundChild) {
                findAndTestChildCandidates(entry.value().getFallback(), checkedPools, parentPiece, anchorJigsawInfo, octree, k, depth, useExpansionHack, world, false, aliasLookup, liquidSettings);
            }
        }
        
        private List<StructurePoolElement> getPoolElements(ResourceKey<StructureTemplatePool> poolKey, MutableObject<List<ResourceKey<StructureTemplatePool>>> checkedPools, int depth, boolean firstIteration) {
            // No point grabbing the pool if it's the empty pool
            if (poolKey == Pools.EMPTY) return List.of();
            
            if (Lithostitched.breaksSeedParity() || !this.vanilla) {
                // If we've already iterated over this pool, don't iterate over it again to prevent infinite looping
                if (checkedPools.getValue().contains(poolKey)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (ResourceKey<StructureTemplatePool> checkedPoolKey : checkedPools.getValue()) {
                        stringBuilder.append(checkedPoolKey.location()).append(" -> ");
                    }
                    stringBuilder.append(poolKey.location());
                    
                    Lithostitched.debug("Template pool fallback chain found: {}", stringBuilder);
                    return List.of();
                }
                
                checkedPools.getValue().add(poolKey);
                
                // Get pool to get the elements, start with fallback pool if at max size
                Holder<StructureTemplatePool> pool = this.registry.getHolder(poolKey).orElseThrow();
                
                // Skip straight to fallback if on max depth
                if (depth == this.maxSize && firstIteration) {
                    pool = pool.value().getFallback();
                }
                
                return ((StructurePoolAccess)pool.value()).getLithostitchedTemplates().shuffle(random);
            }
            
            if (!firstIteration) return List.of();
            
            // Get pool to get the elements, start with fallback pool if at max size
            Holder<StructureTemplatePool> pool = this.registry.getHolder(poolKey).orElseThrow();
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
        private boolean findValidChildPiece(List<StructurePoolElement> elements, PoolElementStructurePiece parentPiece, StructureTemplate.StructureBlockInfo anchorJigsawInfo, BoxOctree octree, int k, int depth, boolean useExpansionHack, LevelHeightAccessor world, PoolAliasLookup aliasLookup, LiquidSettings liquidSettings) {
            BlockPos anchorPos = anchorJigsawInfo.pos();
            BlockPos candidateConnectorPos = anchorPos.relative(JigsawBlock.getFrontFacing(anchorJigsawInfo.state()));
            int parentMinY = parentPiece.getBoundingBox().minY();
            int anchorDistanceToFloor = anchorPos.getY() - parentMinY;
            StructureTemplatePool.Projection parentProjection = parentPiece.getElement().getProjection();
            boolean parentRigid = parentProjection == StructureTemplatePool.Projection.RIGID;
            
            for (StructurePoolElement element : elements) {
                if (element == EmptyPoolElement.INSTANCE) {
                    return true;
                }
                
                DelegatingConfig config = new DelegatingConfig(element);
                boolean isDelegating = false;
                if (element instanceof DelegatingPoolElement delegating) {
                    config = delegating.config();
                    isDelegating = true;
                    if (config.shouldCancelPlacement(context, candidateConnectorPos, depth, this.groupCounts.getOrDefault(config.getName(), 0))) {
                        continue;
                    }
                }
                
                for (Rotation rotation : Rotation.getShuffled(this.random)) {
                    List<StructureTemplate.StructureBlockInfo> connectorJigsaws = element.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, rotation, this.random);
                    BoundingBox connectorBoundingBox = element.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, rotation);
                    
                    // Expansion hack
                    int l;
                    if (useExpansionHack && connectorBoundingBox.getYSpan() <= 16) {
                        l = connectorJigsaws.stream().mapToInt((blockInfo) -> {
                            if (!connectorBoundingBox.isInside(blockInfo.pos().relative(JigsawBlock.getFrontFacing(blockInfo.state())))) {
                                return 0;
                            } else {
                                ResourceKey<StructureTemplatePool> registryKey2 = getTemplatePoolKey(blockInfo, aliasLookup);
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
                            BoundingBox blockBox3 = element.getBoundingBox(this.structureTemplateManager, blockPos5, rotation);
                            int m = blockBox3.minY();
                            StructureTemplatePool.Projection connectorProjection = element.getProjection();
                            boolean connectorProjectionRigid = connectorProjection == StructureTemplatePool.Projection.RIGID;
                            int connectorY = connectorPos.getY();
                            int o = anchorDistanceToFloor - connectorY + JigsawBlock.getFrontFacing(anchorJigsawInfo.state()).getStepY();
                            int p;
                            if (parentRigid && connectorProjectionRigid) {
                                p = parentMinY + o;
                            } else {
                                if (k == -1) {
                                    k = this.chunkGenerator.getFirstFreeHeight(anchorPos.getX(), anchorPos.getZ(), config.terrainMatchingHeightmap(), world, context.randomState());
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
                            
                            
                            if (config.allowBoundingBoxCollisions() || octree.withinBoundsButNotIntersectingChildren(AABB.of(blockBox4).deflate(0.25))) {
                                // At this point the piece is ready to be placed
                                if (isDelegating) {
                                    this.groupCounts.put(config.getName(), this.groupCounts.getOrDefault(config.getName(), 0) + 1);
                                }
                                
                                if (!config.otherPiecesCanIntersect()) {
                                    octree.addBox(AABB.of(blockBox4));
                                }
                                
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
                                        k = this.chunkGenerator.getFirstFreeHeight(anchorPos.getX(), anchorPos.getZ(), config.terrainMatchingHeightmap(), world, context.randomState());
                                    }
                                    
                                    t = k + o / 2;
                                }
                                
                                parentPiece.addJunction(new JigsawJunction(candidateConnectorPos.getX(), t - anchorDistanceToFloor + r,
                                    candidateConnectorPos.getZ(), o, connectorProjection));
                                poolStructurePiece.addJunction(new JigsawJunction(anchorPos.getX(), t - connectorY + s, anchorPos.getZ(), -o, parentProjection));
                                
                                this.piecesToPlace.add(poolStructurePiece);
                                if (depth + 1 <= this.maxSize) {
                                    int priority = anchorJigsawInfo.nbt() != null ? anchorJigsawInfo.nbt().getInt("placement_priority") : 0;
                                    this.pieces.add(new PieceState(poolStructurePiece, octree, depth + 1), priority);
                                }
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        private Holder<StructureTemplatePool> getTemplatePoolHolder(ResourceKey<StructureTemplatePool> key, BlockPos pos) {
            Optional<? extends Holder<StructureTemplatePool>> optional = this.registry.getHolder(key);
            if (optional.isEmpty()) {
                Lithostitched.LOGGER.warn("Couldn't find template pool reference: {} (Jigsaw block location: {})", key.location(), pos.toShortString());
            } else {
                Holder<StructureTemplatePool> regularPool = optional.get();
                if ((regularPool.value()).size() == 0) {
                    if (!regularPool.is(Pools.EMPTY)) {
                        Lithostitched.LOGGER.warn("Referenced template pool is empty: {} (Jigsaw block location: {})", key.location(), pos.toShortString());
                    }
                } else {
                    return regularPool;
                }
            }
            return null;
        }

        private static BlockPos adjustJigsawPos(StructureTemplate.StructureBlockInfo jigsawInfo) {
            return jigsawInfo.pos().relative(JigsawBlock.getFrontFacing(jigsawInfo.state()));
        }
    }

    private record PieceState(PoolElementStructurePiece piece, BoxOctree octree, int currentSize) {}
}