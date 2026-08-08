package dev.worldgen.lithostitched.worldgen.feature;

import net.minecraft.util.valueproviders.IntProviders;
import dev.worldgen.lithostitched.util.MiscUtils;
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
                    int minInclusive = config.columnRadius().minInclusive();
                    int maxInclusive = config.columnRadius().maxInclusive();
                    
                    int unclampedRadius = (int) (range.height() * config.maxColumnRadiusToCaveHeightRatio());
                    int maxRadius = Mth.clamp(unclampedRadius, minInclusive, maxInclusive);

                    int radius = Mth.randomBetweenInclusive(random, minInclusive, maxRadius);

                    LargeDripstone ceilingDripstone = makeDripstone(
                        config.stateProvider(), random, origin.atY(range.ceiling() - 1), false, radius, config.stalactiteBluntness(), config.heightScale()
                    );
                    LargeDripstone floorDripstone = makeDripstone(
                        config.stateProvider(), random, origin.atY(range.floor() + 1), true, radius, config.stalagmiteBluntness(), config.heightScale()
                    );

                    WindOffsetter windOffsetter;

                    if (ceilingDripstone.isSuitableForWind(config) && floorDripstone.isSuitableForWind(config)) {
                        windOffsetter = new WindOffsetter(origin.getY(), random, config.windSpeed());
                    } else {
                        windOffsetter = WindOffsetter.noWind();
                    }

                    boolean stalactiteBaseEmbeddedInStone = ceilingDripstone.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(level, windOffsetter);
                    boolean stalagmiteBaseEmbeddedInStone = floorDripstone.moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(level, windOffsetter);
                    if (stalactiteBaseEmbeddedInStone) {
                        ceilingDripstone.placeBlocks(level, random, windOffsetter);
                    }

                    if (stalagmiteBaseEmbeddedInStone) {
                        floorDripstone.placeBlocks(level, random, windOffsetter);
                    }

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private static LargeDripstone makeDripstone(BlockStateProvider stateProvider, RandomSource random, BlockPos root, boolean pointingUp, int radius, FloatProvider bluntness, FloatProvider scale) {
        return new LargeDripstone(stateProvider, root, pointingUp, radius, bluntness.sample(random), scale.sample(random));
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
        
        private boolean moveBackUntilBaseIsInsideStoneAndShrinkRadiusIfNecessary(final WorldGenLevel level, final LargeDripstoneFeature.WindOffsetter wind) {
            while (this.radius > 1) {
                BlockPos.MutableBlockPos newRoot = this.root.mutable();
                int maxTries = Math.min(10, this.getHeight());
                
                for (int i = 0; i < maxTries; i++) {
                    if (level.getBlockState(newRoot).is(Blocks.LAVA)) {
                        return false;
                    }
                    
                    if (DripstoneUtils.isCircleMostlyEmbeddedInStone(level, wind.offset(newRoot), this.radius)) {
                        this.root = newRoot;
                        return true;
                    }
                    
                    newRoot.move(this.pointingUp ? Direction.DOWN : Direction.UP);
                }
                
                this.radius /= 2;
            }
            
            return false;
        }

        private int getHeightAtRadius(float checkRadius) {
            return (int)DripstoneUtils.getDripstoneHeight(checkRadius, this.radius, this.scale, this.bluntness);
        }

        void placeBlocks(WorldGenLevel level, RandomSource random, WindOffsetter windOffsetter) {
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

                            for (int i = 0; i < height && pos.getY() < maxY; i++) {
                                BlockPos dripstonePos = windOffsetter.offset(pos);
                                if (DripstoneUtils.isEmptyOrWaterOrLava(level, dripstonePos)) {
                                    placedBlock = true;
                                    level.setBlock(dripstonePos, this.stateProvider.getState(level, random, dripstonePos), 2);
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

        boolean isSuitableForWind(LargeDripstoneConfig config) {
            return this.radius >= config.minRadiusForWind() && this.bluntness >= (double)config.minBluntnessForWind();
        }
    }

    static final class WindOffsetter {
        private final int originY;
        private final Vec3 windSpeed;

        WindOffsetter(int y, RandomSource random, FloatProvider windSpeedRange) {
            this.originY = y;
            float speed = windSpeedRange.sample(random);
            float direction = Mth.randomBetween(random, 0.0F, (float) Math.PI);
            this.windSpeed = new Vec3(MiscUtils.cos(direction) * speed, 0.0, MiscUtils.sin(direction) * speed);
        }

        private WindOffsetter() {
            this.originY = 0;
            this.windSpeed = null;
        }

        static WindOffsetter noWind() {
            return new WindOffsetter();
        }

        BlockPos offset(BlockPos pos) {
            if (this.windSpeed == null) {
                return pos;
            } else {
                int dy = this.originY - pos.getY();
                Vec3 totalWindAdjust = this.windSpeed.scale(dy);
                return pos.offset(Mth.floor(totalWindAdjust.x), 0, Mth.floor(totalWindAdjust.z));
            }
        }
    }
}
