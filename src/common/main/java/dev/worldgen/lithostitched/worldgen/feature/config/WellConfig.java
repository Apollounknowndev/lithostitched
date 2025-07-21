package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record WellConfig(BlockStateProvider groundProvider, BlockStateProvider suspiciousProvider, BlockStateProvider standardProvider, BlockStateProvider slabProvider, BlockStateProvider fluidProvider, IntProvider suspiciousPlacements, ResourceKey<LootTable> suspiciousLootTable) implements FeatureConfiguration {
    public static final Codec<WellConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("ground_provider").orElse(BlockStateProvider.simple(Blocks.SAND)).forGetter(WellConfig::groundProvider),
        BlockStateProvider.CODEC.fieldOf("suspicious_provider").orElse(BlockStateProvider.simple(Blocks.SUSPICIOUS_SAND)).forGetter(WellConfig::suspiciousProvider),
        BlockStateProvider.CODEC.fieldOf("standard_provider").orElse(BlockStateProvider.simple(Blocks.SANDSTONE)).forGetter(WellConfig::standardProvider),
        BlockStateProvider.CODEC.fieldOf("slab_provider").orElse(BlockStateProvider.simple(Blocks.SANDSTONE_SLAB)).forGetter(WellConfig::slabProvider),
        BlockStateProvider.CODEC.fieldOf("fluid_provider").orElse(BlockStateProvider.simple(Blocks.WATER)).forGetter(WellConfig::fluidProvider),
        IntProvider.codec(0, 4).fieldOf("suspicious_block_placements").orElse(ConstantInt.of(1)).forGetter(WellConfig::suspiciousPlacements),
        ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("suspicious_loot_table").orElse(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY).forGetter(WellConfig::suspiciousLootTable)
    ).apply(instance, WellConfig::new));
}