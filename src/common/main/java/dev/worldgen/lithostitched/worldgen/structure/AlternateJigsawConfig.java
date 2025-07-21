package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record AlternateJigsawConfig(Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, IntProvider size, HeightProvider startHeight, boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter, List<PoolAliasBinding> poolAliases, DimensionPadding dimensionPadding, LiquidSettings liquidSettings) {

    public static final MapCodec<AlternateJigsawConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(AlternateJigsawConfig::startPool),
        ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(AlternateJigsawConfig::startJigsawName),
        IntProvider.codec(0, 20).fieldOf("size").forGetter(AlternateJigsawConfig::size),
        HeightProvider.CODEC.fieldOf("start_height").forGetter(AlternateJigsawConfig::startHeight),
        Codec.BOOL.fieldOf("use_expansion_hack").forGetter(AlternateJigsawConfig::useExpansionHack),
        Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(AlternateJigsawConfig::projectStartToHeightmap),
        Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(AlternateJigsawConfig::maxDistanceFromCenter),
        Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(AlternateJigsawConfig::poolAliases),
        DimensionPadding.CODEC.optionalFieldOf("dimension_padding", DimensionPadding.ZERO).forGetter(AlternateJigsawConfig::dimensionPadding),
        LiquidSettings.CODEC.optionalFieldOf("liquid_settings", LiquidSettings.APPLY_WATERLOGGING).forGetter(AlternateJigsawConfig::liquidSettings)
    ).apply(instance, (AlternateJigsawConfig::new)));

    public AlternateJigsawConfig setPoolAliases(List<PoolAliasBinding> poolAliases, boolean append) {
        List<PoolAliasBinding> mergedAliases = new ArrayList<>();
        if (append) mergedAliases.addAll(this.poolAliases);
        mergedAliases.addAll(poolAliases);

        return new AlternateJigsawConfig(
            this.startPool,
            this.startJigsawName,
            this.size,
            this.startHeight,
            this.useExpansionHack,
            this.projectStartToHeightmap,
            this.maxDistanceFromCenter,
            mergedAliases,
            this.dimensionPadding,
            this.liquidSettings
        );
    }
}
