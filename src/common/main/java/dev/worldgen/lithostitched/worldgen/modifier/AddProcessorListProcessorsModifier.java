package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.StructureProcessorListAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.ArrayList;
import java.util.List;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

/**
 * A {@link Modifier} implementation that adds structure processors to a {@link StructureProcessorList} entry.
 *
 * @author Apollo
 */
public record AddProcessorListProcessorsModifier(HolderSet<StructureProcessorList> processorLists, StructureProcessorList processors) implements Modifier {
    public static final MapCodec<AddProcessorListProcessorsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        registrySet(Registries.PROCESSOR_LIST, "processor_lists").forGetter(AddProcessorListProcessorsModifier::processorLists),
        StructureProcessorType.LIST_OBJECT_CODEC.fieldOf("processors").forGetter(AddProcessorListProcessorsModifier::processors)
    ).apply(instance, AddProcessorListProcessorsModifier::new));

    @Override
    public void applyModifier() {
        this.processorLists.stream().map(Holder::value).forEach(this::applyModifier);
    }

    public void applyModifier(StructureProcessorList processorList) {
        StructureProcessorListAccessor accessor = (StructureProcessorListAccessor) processorList;

        List<StructureProcessor> structureProcessors = new ArrayList<>(processorList.list());
        structureProcessors.addAll(this.processors.list());

        accessor.setProcessors(structureProcessors);
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.ADD;
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
