package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.NotNull;

public class AlternateJigsawStructure extends Structure {
    public static final Codec<AlternateJigsawStructure> CODEC = ExtraCodecs.validate(
        RecordCodecBuilder.mapCodec((instance) -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(AlternateJigsawStructure::startPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(AlternateJigsawStructure::startJigsawName),
            IntProvider.codec(0, 20).fieldOf("size").forGetter(AlternateJigsawStructure::size),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(AlternateJigsawStructure::startHeight),
            Codec.BOOL.fieldOf("use_expansion_hack").forGetter(AlternateJigsawStructure::useExpansionHack),
            Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(AlternateJigsawStructure::projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(AlternateJigsawStructure::maxDistanceFromCenter),
            GuaranteedElement.CODEC.listOf().fieldOf("guaranteed_elements").orElse(List.of()).forGetter(AlternateJigsawStructure::guaranteedElements)
        ).apply(instance, (AlternateJigsawStructure::new))),
        AlternateJigsawStructure::validate
    ).codec();

    public static final StructureType<AlternateJigsawStructure> ALTERNATE_JIGSAW_TYPE = () -> AlternateJigsawStructure.CODEC;

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final IntProvider size;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final List<GuaranteedElement> guaranteedElements;
    private static DataResult<AlternateJigsawStructure> validate(AlternateJigsawStructure structure) {
        int i = switch (structure.terrainAdaptation()) {
            case NONE -> 0;
            case BURY, BEARD_THIN, BEARD_BOX -> 12;
        };
        return structure.maxDistanceFromCenter + i > 128 ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128") : DataResult.success(structure);
    }
    protected AlternateJigsawStructure(StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, IntProvider size, HeightProvider startHeight, boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter, List<GuaranteedElement> guaranteedElements) {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.guaranteedElements = guaranteedElements;
    }

    public Holder<StructureTemplatePool> startPool() {
        return this.startPool;
    }
    public Optional<ResourceLocation> startJigsawName() {
        return this.startJigsawName;
    }
    public IntProvider size() {
        return this.size;
    }
    public HeightProvider startHeight() {
        return this.startHeight;
    }
    public boolean useExpansionHack() {
        return this.useExpansionHack;
    }
    public Optional<Heightmap.Types> projectStartToHeightmap() {
        return this.projectStartToHeightmap;
    }
    public int maxDistanceFromCenter() {
        return this.maxDistanceFromCenter;
    }
    public List<GuaranteedElement> guaranteedElements() {
        return this.guaranteedElements;
    }

    @Override
    public @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int i = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), i, chunkPos.getMinBlockZ());
        return AlternateJigsawGenerator.generate(context, this.startPool, this.startJigsawName, this.size.sample(context.random()), blockPos, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter, this.guaranteedElements);
    }

    @Override
    public @NotNull StructureType<?> type() {
        return AlternateJigsawStructure.ALTERNATE_JIGSAW_TYPE;
    }

    public record GuaranteedElement(StructurePoolElement element, HolderSet<StructureTemplatePool> acceptablePools, int minDepth) {
        public static final Codec<GuaranteedElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StructurePoolElement.CODEC.fieldOf("element").forGetter(GuaranteedElement::element),
            RegistryCodecs.homogeneousList(Registries.TEMPLATE_POOL).fieldOf("acceptable_pools").forGetter(GuaranteedElement::acceptablePools),
            ExtraCodecs.POSITIVE_INT.fieldOf("min_depth").forGetter(GuaranteedElement::minDepth)
        ).apply(instance, GuaranteedElement::new));
    }
}
