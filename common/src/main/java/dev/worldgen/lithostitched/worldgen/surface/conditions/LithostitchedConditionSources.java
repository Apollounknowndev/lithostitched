package dev.worldgen.lithostitched.worldgen.surface.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.surface.technical.IContextExtension;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Surface conditions, used for creating the condition stack.
 * @author VoidsongDragonfly
 */
public class LithostitchedConditionSources {

    public enum CliffConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<CliffConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((IContextExtension)(Object)pContext).lithostitched$getCliff();
        }
    }

    public enum FlatConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<FlatConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((IContextExtension)(Object)pContext).lithostitched$getFlat();
        }
    }

    public enum FlatLiquidConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<FlatLiquidConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((IContextExtension)(Object)pContext).lithostitched$getFlatLiquid();
        }
    }

    public enum LandTopLayerConditionSource implements SurfaceRules.ConditionSource {
        INSTANCE;

        public static final KeyDispatchDataCodec<LandTopLayerConditionSource> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        @SuppressWarnings("DataFlowIssue")
        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return ((IContextExtension)(Object)pContext).lithostitched$getLandTopLayer();
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
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return new LithostitchedSurfaceConditions.UnderwaterCondition(pContext, shallow);
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
        public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
            return CODEC;
        }

        public SurfaceRules.Condition apply(SurfaceRules.Context pContext) {
            return new LithostitchedSurfaceConditions.CaveDepthCondition(pContext, depth);
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
        public String toString() {
            return "ExtendedBiomeConditionSource[biomes=" + this.biomeSet + "]";
        }
    }
}
