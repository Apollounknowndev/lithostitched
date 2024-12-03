package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.StructureProcessorListAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
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
public record AddProcessorListProcessorsModifier(ModifierPredicate predicate, HolderSet<StructureProcessorList> processorLists, StructureProcessorList processors) implements Modifier {
    public static final Codec<AddProcessorListProcessorsModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        registrySet(Registries.PROCESSOR_LIST, "processor_list").forGetter(AddProcessorListProcessorsModifier::processorLists),
        StructureProcessorType.LIST_OBJECT_CODEC.fieldOf("processors").forGetter(AddProcessorListProcessorsModifier::processors)
    )).apply(instance, AddProcessorListProcessorsModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.ADD;
    }

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
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
