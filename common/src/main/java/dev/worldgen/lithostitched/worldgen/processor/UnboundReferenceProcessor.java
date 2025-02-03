package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.processor.enums.RandomMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;

/**
 * Hack to allow tag references in structure processors without initially having registry access.
 * Meant for non-jigsaw structure template based structures like shipwrecks.
 */
public class UnboundReferenceProcessor extends StructureProcessor {
    public static final Codec<UnboundReferenceProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("name").forGetter(UnboundReferenceProcessor::name)
    ).apply(instance, UnboundReferenceProcessor::new));

    public static final StructureProcessorType<UnboundReferenceProcessor> TYPE = () -> CODEC;
    private final ResourceKey<StructureProcessorList> key;

    public UnboundReferenceProcessor(ResourceLocation name) {
        this.key = ResourceKey.create(Registries.PROCESSOR_LIST, name);
    }

    public ResourceLocation name() {
        return this.key.location();
    }

    public ReferenceStructureProcessor bind(ServerLevel level) {
        var set = level.registryAccess().registryOrThrow(Registries.PROCESSOR_LIST).getHolder(this.key);
        return new ReferenceStructureProcessor(set.map(HolderSet::direct).orElseGet(HolderSet::direct));
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
