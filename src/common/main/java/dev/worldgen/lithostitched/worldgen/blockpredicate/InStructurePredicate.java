package dev.worldgen.lithostitched.worldgen.blockpredicate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public record InStructurePredicate(Optional<Holder<Structure>> structure, SearchRange searchRange) implements BlockPredicate {
    public static final MapCodec<InStructurePredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Structure.CODEC.optionalFieldOf("structure").forGetter(InStructurePredicate::structure),
        SearchRange.CODEC.fieldOf("search_range").forGetter(InStructurePredicate::searchRange)
    ).apply(instance, InStructurePredicate::new));
    public static final BlockPredicateType<InStructurePredicate> TYPE = () -> CODEC;

    @Override
    public BlockPredicateType<?> type() {
        return TYPE;
    }

    @Override
    public boolean test(WorldGenLevel worldGenLevel, BlockPos pos) {
        BoundingBox adjustedBox = this.searchRange.box().moved(pos.getX(), pos.getY(), pos.getZ());
        ServerLevel level = worldGenLevel.getLevel();
        StructureManager manager = level.structureManager();

        Map<Structure, LongSet> references = new HashMap<>();
        adjustedBox.intersectingChunks().forEach(chunk ->
            references.putAll(level.getChunk(chunk.x, chunk.z, ChunkStatus.STRUCTURE_REFERENCES).getAllReferences())
        );

        for (Map.Entry<Structure, LongSet> reference : references.entrySet()) {
            if (structure.isPresent() && !structure.get().value().equals(reference.getKey())) continue;

            Predicate<StructureStart> predicate = start -> {
                for (StructurePiece piece : start.getPieces()) {
                    if (piece.getBoundingBox().intersects(adjustedBox)) {
                        return true;
                    }
                }
                return false;
            };

            MutableBoolean overlappingBox = new MutableBoolean(false);
            manager.fillStartsForStructure(reference.getKey(), reference.getValue(), (start) -> {
                if (overlappingBox.isFalse() && predicate.test(start)) {
                    overlappingBox.setTrue();
                }
            });

            if (overlappingBox.isTrue()) {
                return true;
            }
        }

        return false;
    }


    public record SearchRange(int horizontal, int vertical) {
        private static final Codec<Integer> BASE_CODEC = Codec.intRange(0, 32);
        private static final Codec<SearchRange> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BASE_CODEC.fieldOf("horizontal").forGetter(SearchRange::horizontal),
            ExtraCodecs.intRange(0, DimensionType.Y_SIZE).optionalFieldOf("vertical", DimensionType.Y_SIZE).forGetter(SearchRange::vertical)
        ).apply(instance, SearchRange::new));

        public static final Codec<SearchRange> CODEC = Codec.either(FULL_CODEC, BASE_CODEC).xmap(
            either -> either.map(Function.identity(), SearchRange::new),
            maxDistance -> maxDistance.horizontal == maxDistance.vertical ? Either.right(maxDistance.horizontal) : Either.left(maxDistance)
        );

        public SearchRange(int value) {
            this(value, value);
        }

        public BoundingBox box() {
            return new BoundingBox(-this.horizontal, -this.vertical, -this.horizontal, this.horizontal, this.vertical, this.horizontal);
        }
    }
}