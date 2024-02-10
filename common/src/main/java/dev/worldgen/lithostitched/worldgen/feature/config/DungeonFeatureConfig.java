package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record DungeonFeatureConfig(int minOpenings, int maxOpenings, IntProvider radius, int maxChests, SimpleWeightedRandomList<EntityType<?>> spawnerMobs, BlockStateProvider floorProvider, BlockStateProvider wallProvider, TagKey<Block> dungeonInvalidBlocks, ResourceLocation lootTable) implements FeatureConfiguration {
    private static final SimpleWeightedRandomList<EntityType<?>> DEFAULT_MOBS = SimpleWeightedRandomList.<EntityType<?>>builder().add(EntityType.ZOMBIE, 2).add(EntityType.SKELETON, 1).add(EntityType.SPIDER, 1).build();
    private static final SimpleWeightedRandomList<BlockState> DEFAULT_FLOOR = SimpleWeightedRandomList.<BlockState>builder().add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3).add(Blocks.COBBLESTONE.defaultBlockState(), 1).build();

    public static final Codec<DungeonFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_openings").orElse(1).forGetter(DungeonFeatureConfig::minOpenings),
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_openings").orElse(5).forGetter(DungeonFeatureConfig::maxOpenings),
        IntProvider.codec(1, 16).fieldOf("radius").orElse(UniformInt.of(2, 3)).forGetter(DungeonFeatureConfig::radius),
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_chests").orElse(2).forGetter(DungeonFeatureConfig::maxChests),
        SimpleWeightedRandomList.wrappedCodec(BuiltInRegistries.ENTITY_TYPE.byNameCodec()).fieldOf("spawner_entity").orElse(DEFAULT_MOBS).forGetter(DungeonFeatureConfig::spawnerMobs),
        BlockStateProvider.CODEC.fieldOf("floor_provider").orElse(new WeightedStateProvider(DEFAULT_FLOOR)).forGetter(DungeonFeatureConfig::floorProvider),
        BlockStateProvider.CODEC.fieldOf("wall_provider").orElse(SimpleStateProvider.simple(Blocks.COBBLESTONE)).forGetter(DungeonFeatureConfig::wallProvider),
        TagKey.codec(Registries.BLOCK).fieldOf("dungeon_invalid_blocks").orElse(BlockTags.FEATURES_CANNOT_REPLACE).forGetter(DungeonFeatureConfig::dungeonInvalidBlocks),
        ResourceLocation.CODEC.fieldOf("loot_table").orElse(BuiltInLootTables.SIMPLE_DUNGEON).forGetter(DungeonFeatureConfig::lootTable)
    ).apply(instance, DungeonFeatureConfig::new));
}
