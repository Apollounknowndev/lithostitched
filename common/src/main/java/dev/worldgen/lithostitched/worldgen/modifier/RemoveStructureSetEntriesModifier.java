package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.StructureSetAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

/**
 * A {@link Modifier} implementation that removes structures from a {@link StructureSet} entry.
 *
 * @author Apollo
 */
public record RemoveStructureSetEntriesModifier(ModifierPredicate predicate, HolderSet<StructureSet> structureSets, List<Holder<Structure>> entries) implements Modifier {
    public static final Codec<RemoveStructureSetEntriesModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        registrySet(Registries.STRUCTURE_SET, "structure_set").forGetter(RemoveStructureSetEntriesModifier::structureSets),
        Structure.CODEC.listOf().fieldOf("structures").forGetter(RemoveStructureSetEntriesModifier::entries)
    )).apply(instance, RemoveStructureSetEntriesModifier::new));

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
        this.structureSets.stream().map(Holder::value).forEach(this::applyModifier);
    }

    private void applyModifier(StructureSet structureSet) {
        StructureSetAccessor structureSetAccessor = ((StructureSetAccessor)(Object)structureSet);
        List<StructureSet.StructureSelectionEntry> structureSelectionEntries = new ArrayList<>(structureSet.structures());
        structureSetAccessor.setStructures(structureSelectionEntries.stream().filter(setEntry -> !entries.contains(setEntry.structure())).collect(Collectors.toList()));
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
