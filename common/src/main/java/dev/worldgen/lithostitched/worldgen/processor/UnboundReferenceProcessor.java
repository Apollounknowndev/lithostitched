package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

/**
 * Hack to allow tag references in structure processors without initially having registry access.
 * Meant for non-jigsaw structure template based structures like shipwrecks.
 */
public class UnboundReferenceProcessor extends StructureProcessor {
    public static final MapCodec<UnboundReferenceProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("name").forGetter(UnboundReferenceProcessor::name)
    ).apply(instance, UnboundReferenceProcessor::new));

    public static final StructureProcessorType<UnboundReferenceProcessor> TYPE = () -> CODEC;
    private final ResourceLocation name;

    public UnboundReferenceProcessor(ResourceLocation name) {
        this.name = name;
    }

    public ResourceLocation name() {
        return this.name;
    }

    public ReferenceStructureProcessor bind(ServerLevel level) {
        var set = level.registryAccess().registryOrThrow(Registries.PROCESSOR_LIST).getHolder(this.name);
        return new ReferenceStructureProcessor(set.isPresent() ? HolderSet.direct(set.get()) : HolderSet.empty());
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, BlockPos blockPos2, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlaceSettings structurePlaceSettings) {
        throw new IllegalStateException("[Lithostitched] Unbound reference structure processor should never be processed!");
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return TYPE;
    }
}
