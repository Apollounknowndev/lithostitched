package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.LithostitchedCommon;
import dev.worldgen.lithostitched.mixin.common.StructureSetAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link Modifier} implementation that removes structures from a {@link StructureSet} entry.
 *
 * @author Apollo
 */
public record RemoveStructuresFromStructureSetModifier(ModifierPredicate predicate, Holder<StructureSet> structureSet, List<Holder<Structure>> entries) implements Modifier {
    public static final Codec<RemoveStructuresFromStructureSetModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        StructureSet.CODEC.fieldOf("structure_set").forGetter(RemoveStructuresFromStructureSetModifier::structureSet),
        Structure.CODEC.listOf().fieldOf("structures").forGetter(RemoveStructuresFromStructureSetModifier::entries)
    )).apply(instance, RemoveStructuresFromStructureSetModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.REMOVE;
    }

    @Override
    public void applyModifier() {
        StructureSetAccessor structureSetAccessor = ((StructureSetAccessor)(Object)this.structureSet().value());
        List<StructureSet.StructureSelectionEntry> structureSelectionEntries = new ArrayList<>(this.structureSet().value().structures());
        structureSetAccessor.setStructures(structureSelectionEntries.stream().filter(setEntry -> !entries.contains(setEntry.structure())).collect(Collectors.toList()));
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
