package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.SinglePoolElementAccessor;
import dev.worldgen.lithostitched.mixin.common.StructureTemplatePoolAccessor;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.poolelement.ExclusivePoolElement;
import net.minecraft.core.Holder;
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

/**
 * A {@link Modifier} implementation that sets/adds structure processors to a template pool element entry.
 *
 * @author Apollo
 */
public record SetPoolElementProcessorsModifier(ModifierPredicate predicate, Holder<StructureTemplatePool> templatePool, Optional<List<ResourceLocation>> locations, Holder<StructureProcessorList> processorList, boolean append) implements Modifier {
    public static final Codec<SetPoolElementProcessorsModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        StructureTemplatePool.CODEC.fieldOf("template_pool").forGetter(SetPoolElementProcessorsModifier::templatePool),
        LithostitchedCodecs.singleOrList(ResourceLocation.CODEC).optionalFieldOf("locations").forGetter(SetPoolElementProcessorsModifier::locations),
        StructureProcessorType.LIST_CODEC.fieldOf("processor_list").forGetter(SetPoolElementProcessorsModifier::processorList),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetPoolElementProcessorsModifier::append)
    )).apply(instance, SetPoolElementProcessorsModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }

    @Override
    public void applyModifier() {
        StructureTemplatePoolAccessor pool = ((StructureTemplatePoolAccessor)this.templatePool().value());

        for (StructurePoolElement element : pool.getRawTemplates().stream().map(Pair::getFirst).toList()) {
            applyModifier(element);
        }
    }

    private void applyModifier(StructurePoolElement element) {
        if (element instanceof SinglePoolElement) {
            SinglePoolElementAccessor accessor = (SinglePoolElementAccessor)element;
            var template = accessor.getTemplate().left();
            if (locations.isEmpty() || (template.isPresent() && locations.get().contains(template.get()))) {
                addProcessor(accessor);
            }
        } else if (element instanceof ExclusivePoolElement exclusive) {
            applyModifier(exclusive.delegate());
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
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
