package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.StructureSetAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.worldgen.lithostitched.impl.LithostitchedCodecs.registrySet;

/**
 * A {@link WorldgenModifier} implementation that adds structure set entries to a {@link StructureSet} entry.
 *
 * @author Apollo
 */
public record AddStructureSetEntriesModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<StructureSet> structureSets, List<StructureSelectionEntry> entries) implements WorldgenModifier {
    public static final MapCodec<AddStructureSetEntriesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(AddStructureSetEntriesModifier::priority),
        registrySet(Registries.STRUCTURE_SET, "structure_sets").forGetter(AddStructureSetEntriesModifier::structureSets),
        StructureSelectionEntry.CODEC.listOf().fieldOf("entries").forGetter(AddStructureSetEntriesModifier::entries)
    ).apply(instance, AddStructureSetEntriesModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        this.structureSets.stream().map(Holder::value).forEach(this::applyModifier);
    }

    public void applyModifier(StructureSet structureSet) {
        StructureSetAccessor structureSetAccessor = ((StructureSetAccessor)(Object)structureSet);
        List<StructureSelectionEntry> structureSelectionEntries = new ArrayList<>(structureSet.structures());
        structureSelectionEntries.addAll(this.entries());
        structureSetAccessor.setStructures(structureSelectionEntries);
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
