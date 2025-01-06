package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.StructureSetAccessor;
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
public record RemoveStructureSetEntriesModifier(HolderSet<StructureSet> structureSets, List<Holder<Structure>> entries) implements Modifier {
    public static final MapCodec<RemoveStructureSetEntriesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        registrySet(Registries.STRUCTURE_SET, "structure_sets").forGetter(RemoveStructureSetEntriesModifier::structureSets),
        Structure.CODEC.listOf().fieldOf("structures").forGetter(RemoveStructureSetEntriesModifier::entries)
    ).apply(instance, RemoveStructureSetEntriesModifier::new));

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
    public ModifierPhase getPhase() {
        return ModifierPhase.REMOVE;
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
