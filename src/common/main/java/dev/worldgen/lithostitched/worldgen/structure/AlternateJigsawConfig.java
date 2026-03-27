package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.worldgen.structure.MaxDistanceFromCenter;
import dev.worldgen.lithostitched.api.worldgen.structure.SurfaceSnap;
import dev.worldgen.lithostitched.impl.LithostitchedVersion;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record AlternateJigsawConfig(
    Holder<StructureTemplatePool> startPool,
    Optional<Identifier> startJigsawName,
    IntProvider size,
    boolean fixedRotation,
    HeightProvider startHeight,
    boolean useExpansionHack,
    Optional<Either<SurfaceSnap, Heightmap.Types>> startProjection,
    MaxDistanceFromCenter maxDistanceFromCenter,
    List<PoolAliasBinding> poolAliases,
    DimensionPadding dimensionPadding,
    LiquidSettings liquidSettings
) {
    public static final MapCodec<AlternateJigsawConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(AlternateJigsawConfig::startPool),
        Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(AlternateJigsawConfig::startJigsawName),
        LithostitchedVersion.intProviderCodec(0, 128).fieldOf("size").forGetter(AlternateJigsawConfig::size),
        Codec.BOOL.optionalFieldOf("fixed_rotation", false).forGetter(AlternateJigsawConfig::fixedRotation),
        HeightProvider.CODEC.fieldOf("start_height").forGetter(AlternateJigsawConfig::startHeight),
        Codec.BOOL.fieldOf("use_expansion_hack").forGetter(AlternateJigsawConfig::useExpansionHack),
        Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(AlternateJigsawConfig::projectStartToHeightmap),
        Codec.either(SurfaceSnap.CODEC, Heightmap.Types.CODEC).optionalFieldOf("start_projection").forGetter(AlternateJigsawConfig::startProjection),
        MaxDistanceFromCenter.CODEC.fieldOf("max_distance_from_center").forGetter(AlternateJigsawConfig::maxDistanceFromCenter),
        Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(AlternateJigsawConfig::poolAliases),
        DimensionPadding.CODEC.optionalFieldOf("dimension_padding", DimensionPadding.ZERO).forGetter(AlternateJigsawConfig::dimensionPadding),
        LiquidSettings.CODEC.optionalFieldOf("liquid_settings", LiquidSettings.APPLY_WATERLOGGING).forGetter(AlternateJigsawConfig::liquidSettings)
    ).apply(instance, AlternateJigsawConfig::create));

    public static AlternateJigsawConfig create(
            Holder<StructureTemplatePool> startPool,
            Optional<Identifier> startJigsawName,
            IntProvider size,
            boolean fixedRotation,
            HeightProvider startHeight,
            boolean useExpansionHack,
            Optional<Heightmap.Types> legacyHeightmapProjection,
            Optional<Either<SurfaceSnap, Heightmap.Types>> startProjection,
            MaxDistanceFromCenter maxDistanceFromCenter,
            List<PoolAliasBinding> poolAliases,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings
    ) {
        return new AlternateJigsawConfig(
            startPool,
            startJigsawName,
            size,
            fixedRotation,
            startHeight,
            useExpansionHack,
            legacyHeightmapProjection.map(Either::<SurfaceSnap, Heightmap.Types>right).or(() -> startProjection),
            maxDistanceFromCenter,
            poolAliases,
            dimensionPadding,
            liquidSettings
        );
    }

    public AlternateJigsawConfig setPoolAliases(List<PoolAliasBinding> poolAliases, boolean append) {
        List<PoolAliasBinding> mergedAliases = new ArrayList<>();
        if (append) mergedAliases.addAll(this.poolAliases);
        mergedAliases.addAll(poolAliases);

        return new AlternateJigsawConfig(
            this.startPool,
            this.startJigsawName,
            this.size,
            this.fixedRotation,
            this.startHeight,
            this.useExpansionHack,
            this.startProjection,
            this.maxDistanceFromCenter,
            mergedAliases,
            this.dimensionPadding,
            this.liquidSettings
        );
    }

    private Optional<Heightmap.Types> projectStartToHeightmap() {
        if (this.startProjection.isPresent()) {
            return this.startProjection.get().map(surfaceSnap -> Optional.empty(), Optional::of);
        }
        return Optional.empty();
    }
}
