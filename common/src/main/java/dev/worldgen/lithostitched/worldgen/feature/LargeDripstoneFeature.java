package dev.worldgen.lithostitched.worldgen.feature;

import dev.worldgen.lithostitched.worldgen.feature.config.LargeDripstoneConfig;
import dev.worldgen.lithostitched.worldgen.feature.util.DripstoneUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class LargeDripstoneFeature extends Feature<LargeDripstoneConfig> {
    public static LargeDripstoneFeature FEATURE = new LargeDripstoneFeature();
    public LargeDripstoneFeature() {
        super(LargeDripstoneConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<LargeDripstoneConfig> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        LargeDripstoneConfig config = context.config();
        RandomSource random = context.random();

        if (!DripstoneUtils.isEmptyOrWater(level, origin)) {
            return false;
        } else {
            Optional<Column> column = Column.scan(level, origin, config.floorToCeilingSearchRange(), DripstoneUtils::isEmptyOrWater, state -> DripstoneUtils.isReplaceableOrLava(state, config.replaceableBlocks()));
            if (column.isPresent() && column.get() instanceof Column.Range range) {
                if (range.height() < 4) {
                    return false;
                } else {
                    int unclampedRadius = (int) (range.height() * config.maxColumnRadiusToCaveHeightRatio());
                    int maxRadius = Mth.clamp(unclampedRadius, config.columnRadius().getMinValue(), config.columnRadius().getMaxValue());

                    int radius = Mth.randomBetweenInclusive(random, config.columnRadius().getMinValue(), maxRadius);

                    LargeDripstoneFeature.LargeDripstone ceilingDripstone = makeDripstone(
                        config.stateProvider(), random, origin.atY(range.ceiling() - 1), false, radius, config.stalactiteBluntness(), config.heightScale()
                    );
                    LargeDripstoneFeature.LargeDripstone floorDripstone = makeDripstone(
                        config.stateProvider(), random, origin.atY(range.floor() + 1), true, radius, config.stalagmiteBluntness(), config.heightScale()
                    );

                    LargeDripstoneFeature.WindOffsetter windOffsetter;

                    if (ceilingDripstone.isSuitableForWind(config) && floorDripstone.isSuitableForWind(config)) {
                        windOffsetter = new LargeDripstoneFeature.WindOffsetter(origin.getY(), random, config.windSpeed());
                    } else {
                        windOffsetter = LargeDripstoneFeature.WindOffsetter.noWind();
                    }

                    boolean $$14 = ceilingDripstone.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(level, windOffsetter);
                    boolean $$15 = floorDripstone.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(level, windOffsetter);
                    if ($$14) {
                        ceilingDripstone.placeBlocks(level, random, windOffsetter);
                    }

                    if ($$15) {
                        floorDripstone.placeBlocks(level, random, windOffsetter);
                    }

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private static LargeDripstoneFeature.LargeDripstone makeDripstone(BlockStateProvider stateProvider, RandomSource random, BlockPos root, boolean pointingUp, int radius, FloatProvider bluntness, FloatProvider scale) {
        return new LargeDripstoneFeature.LargeDripstone(stateProvider, root, pointingUp, radius, bluntness.sample(random), scale.sample(random));
    }

    static final class LargeDripstone {
        private final BlockStateProvider stateProvider;
        private BlockPos root;
        private final boolean pointingUp;
        private int radius;
        private final double bluntness;
        private final double scale;

        LargeDripstone(BlockStateProvider stateProvider, BlockPos root, boolean pointingUp, int radius, double bluntness, double scale) {
            this.stateProvider = stateProvider;
            this.root = root;
            this.pointingUp = pointingUp;
            this.radius = radius;
            this.bluntness = bluntness;
            this.scale = scale;
        }

        private int getHeight() {
            return this.getHeightAtRadius(0.0F);
        }

        boolean moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(WorldGenLevel $$0, LargeDripstoneFeature.WindOffsetter $$1) {
            while (this.radius > 1) {
                BlockPos.MutableBlockPos $$2 = this.root.mutable();
                int $$3 = Math.min(10, this.getHeight());

                for (int $$4 = 0; $$4 < $$3; $$4++) {
                    if ($$0.getBlockState($$2).is(Blocks.LAVA)) {
                        return false;
                    }

                    if (DripstoneUtils.isCircleMostlyEmbeddedInStone($$0, $$1.offset($$2), this.radius)) {
                        this.root = $$2;
                        return true;
                    }

                    $$2.move(this.pointingUp ? Direction.DOWN : Direction.UP);
                }

                this.radius /= 2;
            }

            return false;
        }

        private int getHeightAtRadius(float $$0) {
            return (int)DripstoneUtils.getDripstoneHeight($$0, this.radius, this.scale, this.bluntness);
        }

        void placeBlocks(WorldGenLevel level, RandomSource random, LargeDripstoneFeature.WindOffsetter windOffsetter) {
            for (int x = -this.radius; x <= this.radius; x++) {
                for (int z = -this.radius; z <= this.radius; z++) {

                    float rootDistance = Mth.sqrt((float)(x * x + z * z));

                    if (!(rootDistance > (float)this.radius)) {
                        int height = this.getHeightAtRadius(rootDistance);
                        if (height > 0) {
                            if (random.nextFloat() < 0.2) {
                                height = (int)(height * Mth.randomBetween(random, 0.8F, 1.0F));
                            }

                            BlockPos.MutableBlockPos pos = this.root.offset(x, 0, z).mutable();
                            boolean placedBlock = false;
                            int maxY = this.pointingUp ? level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ()) : Integer.MAX_VALUE;

                            for (int $$10 = 0; $$10 < height && pos.getY() < maxY; $$10++) {
                                BlockPos dripstonePos = windOffsetter.offset(pos);
                                if (DripstoneUtils.isEmptyOrWaterOrLava(level, dripstonePos)) {
                                    placedBlock = true;
                                    level.setBlock(dripstonePos, this.stateProvider.getState(random, dripstonePos), 2);
                                } else if (placedBlock && level.getBlockState(dripstonePos).is(BlockTags.BASE_STONE_OVERWORLD)) {
                                    break;
                                }

                                pos.move(this.pointingUp ? Direction.UP : Direction.DOWN);
                            }
                        }
                    }
                }
            }
        }

        boolean isSuitableForWind(LargeDripstoneConfig $$0) {
            return this.radius >= $$0.minRadiusForWind() && this.bluntness >= (double)$$0.minBluntnessForWind();
        }
    }

    static final class WindOffsetter {
        private final int originY;
        private final Vec3 windSpeed;

        WindOffsetter(int y, RandomSource random, FloatProvider $$2) {
            this.originY = y;
            float $$3 = $$2.sample(random);
            float $$4 = Mth.randomBetween(random, 0.0F, (float) Math.PI);
            this.windSpeed = new Vec3(Mth.cos($$4) * $$3, 0.0, Mth.sin($$4) * $$3);
        }

        private WindOffsetter() {
            this.originY = 0;
            this.windSpeed = null;
        }

        static LargeDripstoneFeature.WindOffsetter noWind() {
            return new LargeDripstoneFeature.WindOffsetter();
        }

        BlockPos offset(BlockPos $$0) {
            if (this.windSpeed == null) {
                return $$0;
            } else {
                int $$1 = this.originY - $$0.getY();
                Vec3 $$2 = this.windSpeed.scale($$1);
                return $$0.offset(Mth.floor($$2.x), 0, Mth.floor($$2.z));
            }
        }
    }
}
