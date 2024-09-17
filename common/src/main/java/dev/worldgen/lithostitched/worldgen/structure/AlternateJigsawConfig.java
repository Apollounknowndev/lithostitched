package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

public record AlternateJigsawConfig(Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, IntProvider size, HeightProvider startHeight, boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter) {

    public static final MapCodec<AlternateJigsawConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(AlternateJigsawConfig::startPool),
        ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(AlternateJigsawConfig::startJigsawName),
        IntProvider.codec(0, 20).fieldOf("size").forGetter(AlternateJigsawConfig::size),
        HeightProvider.CODEC.fieldOf("start_height").forGetter(AlternateJigsawConfig::startHeight),
        Codec.BOOL.fieldOf("use_expansion_hack").forGetter(AlternateJigsawConfig::useExpansionHack),
        Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(AlternateJigsawConfig::projectStartToHeightmap),
        Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(AlternateJigsawConfig::maxDistanceFromCenter)
    ).apply(instance, AlternateJigsawConfig::new));
}
