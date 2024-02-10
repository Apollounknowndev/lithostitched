package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public record WellFeatureConfig(BlockStateProvider groundProvider, BlockStateProvider suspiciousProvider, BlockStateProvider standardProvider, BlockStateProvider slabProvider, BlockStateProvider fluidProvider, IntProvider suspiciousPlacements, ResourceLocation suspiciousLootTable) implements FeatureConfiguration {
    public static final Codec<WellFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("ground_provider").orElse(BlockStateProvider.simple(Blocks.SAND)).forGetter(WellFeatureConfig::groundProvider),
        BlockStateProvider.CODEC.fieldOf("suspicious_provider").orElse(BlockStateProvider.simple(Blocks.SUSPICIOUS_SAND)).forGetter(WellFeatureConfig::suspiciousProvider),
        BlockStateProvider.CODEC.fieldOf("standard_provider").orElse(BlockStateProvider.simple(Blocks.SANDSTONE)).forGetter(WellFeatureConfig::standardProvider),
        BlockStateProvider.CODEC.fieldOf("slab_provider").orElse(BlockStateProvider.simple(Blocks.SANDSTONE_SLAB)).forGetter(WellFeatureConfig::slabProvider),
        BlockStateProvider.CODEC.fieldOf("fluid_provider").orElse(BlockStateProvider.simple(Blocks.WATER)).forGetter(WellFeatureConfig::fluidProvider),
        IntProvider.codec(0, 4).fieldOf("suspicious_block_placements").orElse(ConstantInt.of(1)).forGetter(WellFeatureConfig::suspiciousPlacements),
        ResourceLocation.CODEC.fieldOf("suspicious_loot_table").orElse(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY).forGetter(WellFeatureConfig::suspiciousLootTable)
    ).apply(instance, WellFeatureConfig::new));
}