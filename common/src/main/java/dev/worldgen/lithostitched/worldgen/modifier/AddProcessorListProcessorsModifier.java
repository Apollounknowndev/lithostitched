package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.access.StructurePoolAccess;
import dev.worldgen.lithostitched.mixin.common.StructureProcessorListAccessor;
import dev.worldgen.lithostitched.mixin.common.StructureSetAccessor;
import dev.worldgen.lithostitched.mixin.common.StructureTemplatePoolAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
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
public record AddProcessorListProcessorsModifier(ModifierPredicate predicate, Holder<StructureProcessorList> processorList, StructureProcessorList processors) implements Modifier {
    public static final Codec<AddProcessorListProcessorsModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        RegistryFileCodec.create(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC, false).fieldOf("processor_list").forGetter(AddProcessorListProcessorsModifier::processorList),
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
        StructureProcessorListAccessor accessor = (StructureProcessorListAccessor) this.processorList.value();

        List<StructureProcessor> structureProcessors = new ArrayList<>(this.processorList.value().list());
        structureProcessors.addAll(this.processors.list());

        accessor.setProcessors(structureProcessors);
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
