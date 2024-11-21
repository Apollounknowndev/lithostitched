package dev.worldgen.lithostitched.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.worldgen.processor.enums.RandomMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;

/**
 * Hack to allow tag references in structure processors without initially having registry access.
 * Meant for non-jigsaw structure template based structures like shipwrecks.
 */
public class UnboundTagStructureProcessor extends StructureProcessor {
    public static final Codec<UnboundTagStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        TagKey.codec(Registries.PROCESSOR_LIST).fieldOf("tag").forGetter(UnboundTagStructureProcessor::tag)
    ).apply(instance, UnboundTagStructureProcessor::new));

    public static final StructureProcessorType<UnboundTagStructureProcessor> TYPE = () -> CODEC;
    private final TagKey<StructureProcessorList> tag;

    public UnboundTagStructureProcessor(TagKey<StructureProcessorList> tag) {
        this.tag = tag;
    }

    public TagKey<StructureProcessorList> tag() {
        return this.tag;
    }

    public ApplyRandomStructureProcessor bind(ServerLevel level) {
        var set = level.registryAccess().registryOrThrow(Registries.PROCESSOR_LIST).getTag(tag);
        return new ApplyRandomStructureProcessor(set.isPresent() ? set.get() : HolderSet.direct(), new RandomSettings(RandomMode.PER_PIECE, LithostitchedCommon.id("rebound_reference")));
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
