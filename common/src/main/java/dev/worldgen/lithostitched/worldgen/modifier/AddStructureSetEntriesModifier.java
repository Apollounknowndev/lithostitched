package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.StructureSetAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

/**
 * A {@link Modifier} implementation that adds structure set entries to a {@link StructureSet} entry.
 *
 * @author Apollo
 */
public record AddStructureSetEntriesModifier(HolderSet<StructureSet> structureSets, List<StructureSet.StructureSelectionEntry> entries) implements Modifier {
    public static final MapCodec<AddStructureSetEntriesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        registrySet(Registries.STRUCTURE_SET, "structure_sets").forGetter(AddStructureSetEntriesModifier::structureSets),
        StructureSet.StructureSelectionEntry.CODEC.listOf().fieldOf("entries").forGetter(AddStructureSetEntriesModifier::entries)
    ).apply(instance, AddStructureSetEntriesModifier::new));

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.ADD;
    }

    @Override
    public void applyModifier() {
        this.structureSets.stream().map(Holder::value).forEach(this::applyModifier);
    }

    public void applyModifier(StructureSet structureSet) {
        StructureSetAccessor structureSetAccessor = ((StructureSetAccessor)(Object)structureSet);
        List<StructureSet.StructureSelectionEntry> structureSelectionEntries = new ArrayList<>(structureSet.structures());
        structureSelectionEntries.addAll(this.entries());
        structureSetAccessor.setStructures(structureSelectionEntries);
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
