package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.SinglePoolElementAccessor;
import dev.worldgen.lithostitched.mixin.common.StructureTemplatePoolAccessor;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.compactList;
import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

/**
 * A {@link Modifier} implementation that sets/adds structure processors to a template pool element entry.
 *
 * @author Apollo
 */
public record SetPoolElementProcessorsModifier(HolderSet<StructureTemplatePool> templatePools, Optional<List<ResourceLocation>> locations, Holder<StructureProcessorList> processorList, boolean append) implements Modifier {
    public static final MapCodec<SetPoolElementProcessorsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        registrySet(Registries.TEMPLATE_POOL, "template_pools").forGetter(SetPoolElementProcessorsModifier::templatePools),
        compactList(ResourceLocation.CODEC).optionalFieldOf("locations").forGetter(SetPoolElementProcessorsModifier::locations),
        StructureProcessorType.LIST_CODEC.fieldOf("processor_list").forGetter(SetPoolElementProcessorsModifier::processorList),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetPoolElementProcessorsModifier::append)
    ).apply(instance, SetPoolElementProcessorsModifier::new));

    @Override
    public void applyModifier() {
        for (Holder<StructureTemplatePool> templatePool : this.templatePools) {
            StructureTemplatePoolAccessor pool = ((StructureTemplatePoolAccessor)templatePool.value());

            for (StructurePoolElement element : pool.getRawTemplates().stream().map(Pair::getFirst).toList()) {
                applyModifier(element);
            }
        }
    }

    private void applyModifier(StructurePoolElement element) {
        if (element instanceof SinglePoolElement) {
            SinglePoolElementAccessor accessor = (SinglePoolElementAccessor)element;
            var template = accessor.getTemplate().left();
            if (locations.isEmpty() || (template.isPresent() && locations.get().contains(template.get()))) {
                addProcessor(accessor);
            }
        } else if (element instanceof DelegatingPoolElement delegating) {
            applyModifier(delegating.delegate());
        }
    }

    private void addProcessor(SinglePoolElementAccessor element) {
        List<StructureProcessor> processors = new ArrayList<>();
        if (append) {
            processors.addAll(element.getProcessors().value().list());
        }
        processors.addAll(processorList.value().list());

        element.setProcessors(Holder.direct(new StructureProcessorList(processors)));
    }

    @Override
    public ModifierPhase getPhase() {
        return this.append ? ModifierPhase.REPLACE : ModifierPhase.ADD;
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
