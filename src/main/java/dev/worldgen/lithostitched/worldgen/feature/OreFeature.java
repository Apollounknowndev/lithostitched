package dev.worldgen.lithostitched.worldgen.feature;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.util.MiscUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.BitSet;
import java.util.List;

public record OreFeature(int size, List<Target> targets) implements Feature {
    public static final MapCodec<OreFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.intRange(0, 128).fieldOf("size").forGetter(OreFeature::size),
        Target.CODEC.listOf().fieldOf("targets").forGetter(OreFeature::targets)
    ).apply(instance, OreFeature::new));
    
    public static OreFeature create(int size, List<Pair<BlockPredicate, BlockStateProvider>> targets) {
        return new OreFeature(size, targets.stream().map(pair -> new Target(pair.getFirst(), pair.getSecond())).toList());
    }
    
    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
    
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        float $$5 = random.nextFloat() * (float) Math.PI;
        float $$6 = this.size() / 8.0F;
        int $$7 = Mth.ceil((this.size() / 16.0F * 2.0F + 1.0F) / 2.0F);
        double $$8 = (double)origin.getX() + Math.sin($$5) * (double)$$6;
        double $$9 = (double)origin.getX() - Math.sin($$5) * (double)$$6;
        double $$10 = (double)origin.getZ() + Math.cos($$5) * (double)$$6;
        double $$11 = (double)origin.getZ() - Math.cos($$5) * (double)$$6;
        double $$13 = origin.getY() + random.nextInt(3) - 2;
        double $$14 = origin.getY() + random.nextInt(3) - 2;

        int minX = origin.getX() - Mth.ceil($$6) - $$7;
        int minY = origin.getY() - 2 - $$7;
        int minZ = origin.getZ() - Mth.ceil($$6) - $$7;

        int maxXZ = 2 * (Mth.ceil($$6) + $$7);
        int maxY = 2 * (2 + $$7);

        for (int x = minX; x <= minX + maxXZ; x++) {
            for (int y = minZ; y <= minZ + maxXZ; y++) {
                if (minY <= level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, y)) {
                    return this.doPlace(level, random, $$8, $$9, $$10, $$11, $$13, $$14, minX, minY, minZ, maxXZ, maxY);
                }
            }
        }

        return false;
    }

    private boolean doPlace(
	    WorldGenLevel level,
	    RandomSource random,
	    double $$3,
	    double $$4,
	    double $$5,
	    double $$6,
	    double $$7,
	    double $$8,
	    int minX,
	    int minY,
	    int minZ,
	    int maxXZ,
	    int maxY
    ) {
        int blocksPlaced = 0;
        BitSet $$15 = new BitSet(maxXZ * maxY * maxXZ);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int $$17 = this.size();
        double[] $$18 = new double[$$17 * 4];

        for (int $$19 = 0; $$19 < $$17; $$19++) {
            float $$20 = (float)$$19 / (float)$$17;
            double $$21 = Mth.lerp($$20, $$3, $$4);
            double $$22 = Mth.lerp($$20, $$7, $$8);
            double $$23 = Mth.lerp($$20, $$5, $$6);
            double $$24 = random.nextDouble() * (double)$$17 / 16.0;
            double $$25 = ((double)(MiscUtils.sin((float) Math.PI * $$20) + 1.0F) * $$24 + 1.0) / 2.0;
            $$18[$$19 * 4] = $$21;
            $$18[$$19 * 4 + 1] = $$22;
            $$18[$$19 * 4 + 2] = $$23;
            $$18[$$19 * 4 + 3] = $$25;
        }

        for (int $$26 = 0; $$26 < $$17 - 1; $$26++) {
            if (!($$18[$$26 * 4 + 3] <= 0.0)) {
                for (int $$27 = $$26 + 1; $$27 < $$17; $$27++) {
                    if (!($$18[$$27 * 4 + 3] <= 0.0)) {
                        double $$28 = $$18[$$26 * 4] - $$18[$$27 * 4];
                        double $$29 = $$18[$$26 * 4 + 1] - $$18[$$27 * 4 + 1];
                        double $$30 = $$18[$$26 * 4 + 2] - $$18[$$27 * 4 + 2];
                        double $$31 = $$18[$$26 * 4 + 3] - $$18[$$27 * 4 + 3];
                        if ($$31 * $$31 > $$28 * $$28 + $$29 * $$29 + $$30 * $$30) {
                            if ($$31 > 0.0) {
                                $$18[$$27 * 4 + 3] = -1.0;
                            } else {
                                $$18[$$26 * 4 + 3] = -1.0;
                            }
                        }
                    }
                }
            }
        }

        try (BulkSectionAccess bulkSectionAccess = new BulkSectionAccess(level)) {
            for (int $$33 = 0; $$33 < $$17; $$33++) {
                double $$34 = $$18[$$33 * 4 + 3];
                if (!($$34 < 0.0)) {
                    double $$35 = $$18[$$33 * 4];
                    double $$36 = $$18[$$33 * 4 + 1];
                    double $$37 = $$18[$$33 * 4 + 2];
                    int $$38 = Math.max(Mth.floor($$35 - $$34), minX);
                    int $$39 = Math.max(Mth.floor($$36 - $$34), minY);
                    int $$40 = Math.max(Mth.floor($$37 - $$34), minZ);
                    int $$41 = Math.max(Mth.floor($$35 + $$34), $$38);
                    int $$42 = Math.max(Mth.floor($$36 + $$34), $$39);
                    int $$43 = Math.max(Mth.floor($$37 + $$34), $$40);

                    for (int x = $$38; x <= $$41; x++) {
                        double $$45 = ((double)x + 0.5 - $$35) / $$34;
                        if ($$45 * $$45 < 1.0) {
                            for (int y = $$39; y <= $$42; y++) {
                                double $$47 = ((double)y + 0.5 - $$36) / $$34;
                                if ($$45 * $$45 + $$47 * $$47 < 1.0) {
                                    for (int z = $$40; z <= $$43; z++) {
                                        double $$49 = ((double)z + 0.5 - $$37) / $$34;
                                        if ($$45 * $$45 + $$47 * $$47 + $$49 * $$49 < 1.0 && !level.isOutsideBuildHeight(y)) {
                                            int $$50 = x - minX + (y - minY) * maxXZ + (z - minZ) * maxXZ * maxY;
                                            if (!$$15.get($$50)) {
                                                $$15.set($$50);
                                                pos.set(x, y, z);
                                                if (level.ensureCanWrite(pos)) {
                                                    LevelChunkSection section = bulkSectionAccess.getSection(pos);
                                                    if (section != null) {
                                                        int sectionX = SectionPos.sectionRelative(x);
                                                        int sectionY = SectionPos.sectionRelative(y);
                                                        int sectionZ = SectionPos.sectionRelative(z);

                                                        for (Target target : this.targets()) {
                                                            if (target.predicate().test(level, pos)) {
                                                                section.setBlockState(sectionX, sectionY, sectionZ, target.stateProvider().getState(level, random, pos), false);
                                                                blocksPlaced++;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return blocksPlaced > 0;
    }
    
    public record Target(BlockPredicate predicate, BlockStateProvider stateProvider) {
        public static final Codec<Target> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPredicate.CODEC.fieldOf("predicate").forGetter(Target::predicate),
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Target::stateProvider)
        ).apply(instance, Target::new));
    }
}
