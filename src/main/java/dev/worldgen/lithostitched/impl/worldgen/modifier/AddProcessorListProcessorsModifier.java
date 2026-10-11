package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import dev.worldgen.lithostitched.mixin.common.StructureProcessorListAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.worldgen.lithostitched.impl.LithostitchedCodecs.holderTarget;

/**
 * A {@link WorldgenModifier} implementation that adds structure processors to a {@link StructureProcessorList} entry.
 *
 * @author Apollo
 */
public record AddProcessorListProcessorsModifier(Optional<LoadPredicate> predicate, int priority, HolderTarget<StructureProcessorList> processorLists, StructureProcessorList processors) implements WorldgenModifier {
    public static final MapCodec<AddProcessorListProcessorsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(AddProcessorListProcessorsModifier::priority),
        HolderTarget.createCodec(Registries.PROCESSOR_LIST).fieldOf("processor_lists").forGetter(AddProcessorListProcessorsModifier::processorLists),
        StructureProcessorType.LIST_OBJECT_CODEC.fieldOf("processors").forGetter(AddProcessorListProcessorsModifier::processors)
    ).apply(instance, AddProcessorListProcessorsModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        for (Holder<StructureProcessorList> processorList : this.processorLists.getHolders(registries)) {
            this.applyModifier(processorList.value());
        }
    }

    public void applyModifier(StructureProcessorList processorList) {
        StructureProcessorListAccessor accessor = (StructureProcessorListAccessor) processorList;

        List<StructureProcessor> structureProcessors = new ArrayList<>(processorList.list());
        structureProcessors.addAll(this.processors.list());

        accessor.setProcessors(structureProcessors);
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
