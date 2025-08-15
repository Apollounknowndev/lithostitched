package dev.worldgen.lithostitched.worldgen.surface.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.surface.technical.ContextExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import javax.annotation.Nonnull;

/**
 * Surface conditions, used for creating the condition stack.
 *
 * @author VoidsongDragonfly
 */
public class LithostitchedConditionSources {

    public enum CliffConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<CliffConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((ContextExtension)(Object)pContext).naturalphilosophy$getCliff();
        }
    }

    public enum FlatConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<FlatConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((ContextExtension)(Object)pContext).naturalphilosophy$getFlat();
        }
    }

    public enum FlatLiquidConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<FlatLiquidConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((ContextExtension)(Object)pContext).naturalphilosophy$getFlatLiquid();
        }
    }

    public enum LandTopLayerConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<LandTopLayerConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((ContextExtension)(Object)pContext).naturalphilosophy$getLandTopLayer();
        }
    }

    public record UnderwaterConditionSource(boolean shallow) implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<UnderwaterConditionSource> CODEC = KeyDispatchDataCodec.of(
            RecordCodecBuilder.mapCodec(
                source -> source.group(
                    Codec.BOOL.optionalFieldOf("shallow", false).forGetter(UnderwaterConditionSource::shallow)
                ).apply(source, UnderwaterConditionSource::new)
            )
        );

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            class UnderwaterCondition implements SurfaceRules.Condition {
                @Override
                public boolean test() {
                    // Exit early if we're above water
                    if (pContext.waterHeight == Integer.MIN_VALUE) return false;
                    // If we don't care about shallowness, return early, else check the Vanilla "shallow water" parameters
                    return !shallow || ((pContext.blockY + pContext.stoneDepthAbove) >= (pContext.waterHeight - 6 - pContext.surfaceDepth));
                }
            }

            return new UnderwaterCondition();
        }
    }

    public record CaveDepthConditionSource(int depth) implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<CaveDepthConditionSource> CODEC = KeyDispatchDataCodec.of(
            RecordCodecBuilder.mapCodec(
                source -> source.group(
                    Codec.INT.fieldOf("depth").forGetter(CaveDepthConditionSource::depth)
                ).apply(source, CaveDepthConditionSource::new)
            )
        );

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            class CaveDepthCondition implements SurfaceRules.Condition {
                @Override
                public boolean test() {
                    int heightmapDepth = ((ContextExtension)(Object)pContext).naturalphilosophy$getOceanHeightmapDepth();
                    // Return early if we're above the necessary depth
                    if (heightmapDepth - depth <= pContext.blockY) return false;
                    // If we're shallower than twelve blocks, we do not need to check the air blocks above this block
                    // We remove/add stoneDepthAbove to make sure we stay congruous with the top block of the cave
                    int currentDepth = heightmapDepth - pContext.blockY + pContext.stoneDepthAbove;
                    if (currentDepth < 12) return true;
                    // Check to make sure we're not underneath a massive overhang by checking if greater than 2/3ths what's above is air
                    MutableBlockPos pos = new MutableBlockPos(pContext.blockX, pContext.blockY + pContext.stoneDepthAbove, pContext.blockZ);
                    for (int i = 1 + pContext.stoneDepthAbove; i < (currentDepth*3)/4; i++)
                        if (!pContext.chunk.getBlockState(pos.setY(pContext.blockY + i)).canBeReplaced()) return true;
                    // Variable store for future operations
                    int i = pContext.blockX & 15;
                    int j = pContext.blockZ & 15;
                    // Movements within the chunk for close block checks
                    int searchLevel = pContext.blockY + pContext.stoneDepthAbove- 2;
                    int north = Math.max(j - 1, 0);
                    int east  = Math.min(i + 1, 15);
                    int south = Math.min(j + 1, 15);
                    int west  = Math.max(i - 1, 0);
                    // Now we check to make sure we're not on the side of a cliff in a windswept biome
                    boolean lip = false;
                    lip = lip || pContext.chunk.getBlockState(new BlockPos(pContext.blockX, searchLevel, pContext.blockZ-j+north)).isAir();
                    lip = lip || pContext.chunk.getBlockState(new BlockPos(pContext.blockX-i+east, searchLevel, pContext.blockZ)).isAir();
                    lip = lip || pContext.chunk.getBlockState(new BlockPos(pContext.blockX, searchLevel, pContext.blockZ-j+south)).isAir();
                    lip = lip || pContext.chunk.getBlockState(new BlockPos(pContext.blockX-i+west, searchLevel, pContext.blockZ)).isAir();
                    return lip;
                }
            }

            return new CaveDepthCondition();
        }
    }

    public static class ExtendedBiomeConditionSource implements SurfaceRules.ConditionSource {
        public static final KeyDispatchDataCodec<ExtendedBiomeConditionSource> CODEC = KeyDispatchDataCodec.of(
            RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biome_is").xmap(ExtendedBiomeConditionSource::new, biomeSource -> biomeSource.biomeSet)
        );
        public final HolderSet<Biome> biomeSet;

        public ExtendedBiomeConditionSource(HolderSet<Biome> biomes) {
            this.biomeSet = biomes;
        }

        @Override
        @Nonnull
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(final SurfaceRules.Context pContext) {
            class BiomeCondition implements SurfaceRules.Condition {
                @Override
                public boolean test() {
                    return biomeSet.contains(pContext.biome.get());
                }
            }

            return new BiomeCondition();
        }

        @Override
        @Nonnull
        public String toString() {
            return "ExtendedBiomeConditionSource[biomes=" + this.biomeSet + "]";
        }
    }
}
