package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.StructureProcessorListAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Modifier} implementation that adds structure processors to a {@link StructureProcessorList} entry.
 *
 * @author Apollo
 */
public record AddProcessorListProcessorsModifier(Holder<StructureProcessorList> processorList, StructureProcessorList processors) implements Modifier {
    public static final MapCodec<AddProcessorListProcessorsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        RegistryFileCodec.create(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC, false).fieldOf("processor_list").forGetter(AddProcessorListProcessorsModifier::processorList),
        StructureProcessorType.LIST_OBJECT_CODEC.fieldOf("processors").forGetter(AddProcessorListProcessorsModifier::processors)
    ).apply(instance, AddProcessorListProcessorsModifier::new));

    @Override
    public void applyModifier() {
        StructureProcessorListAccessor accessor = (StructureProcessorListAccessor) this.processorList.value();

        List<StructureProcessor> structureProcessors = new ArrayList<>(this.processorList.value().list());
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
