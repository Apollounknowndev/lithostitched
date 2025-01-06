package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * The interface used for structure conditions.
 * Structure conditions can be used with the `lithostitched:delegating` structure type and provide extra fields to configure structure placement.
 *
 * @author Apollo
 */
public interface PlacementCondition {
    @SuppressWarnings("unchecked")
    Codec<PlacementCondition> BASE_CODEC = ExtraCodecs.lazyInitializedCodec(() -> {
        var modifierRegistry = BuiltInRegistries.REGISTRY.get(LithostitchedRegistryKeys.PLACEMENT_CONDITION_TYPE.location());
        if (modifierRegistry == null) throw new NullPointerException("Placement modifier registry does not exist yet!");
        return ((Registry<MapCodec<? extends PlacementCondition>>) modifierRegistry).byNameCodec();
    }).dispatch(PlacementCondition::codec, MapCodec::codec);

    Codec<PlacementCondition> CODEC = LithostitchedCodecs.withAlternative(BASE_CODEC, BASE_CODEC.listOf(), AllOfPlacementCondition::new);

    boolean test(Context context, BlockPos pos);

    default boolean test(Structure.GenerationContext context, BlockPos pos) {
        return this.test(Context.create(context), pos);
    }

    default boolean test(PlacementContext context, BlockPos pos) {
        return this.test(Context.create(context), pos);
    }

    MapCodec<? extends PlacementCondition> codec();

    record Context(RegistryAccess registries, ChunkGenerator generator, LevelHeightAccessor heightAccessor, RandomState randomState, BiomeSource biomeSource, long seed) {
        private static Context create(Structure.GenerationContext context) {
            return new Context(context.registryAccess(), context.chunkGenerator(), context.heightAccessor(), context.randomState(), context.biomeSource(), context.seed());
        }

        private static Context create(PlacementContext context) {
            WorldGenLevel level = context.getLevel();
            return new Context(level.registryAccess(), context.generator(), level, level.getLevel().getChunkSource().randomState(), context.generator().getBiomeSource(), level.getSeed());
        }
    }
}
