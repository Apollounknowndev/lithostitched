package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import dev.worldgen.lithostitched.mixin.common.StructureSetAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.worldgen.lithostitched.impl.LithostitchedCodecs.holderTarget;

/**
 * A {@link WorldgenModifier} implementation that removes structures from a {@link StructureSet} entry.
 *
 * @author Apollo
 */
public record RemoveStructureSetEntriesModifier(Optional<LoadPredicate> predicate, int priority, HolderTarget<StructureSet> structureSets, List<Holder<Structure>> entries) implements WorldgenModifier {
    public static final MapCodec<RemoveStructureSetEntriesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_REMOVE_CODEC.forGetter(RemoveStructureSetEntriesModifier::priority),
        holderTarget(Registries.STRUCTURE_SET, "structure_sets").forGetter(RemoveStructureSetEntriesModifier::structureSets),
        Structure.CODEC.listOf().fieldOf("structures").forGetter(RemoveStructureSetEntriesModifier::entries)
    ).apply(instance, RemoveStructureSetEntriesModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        for (Holder<StructureSet> set : this.structureSets.getHolders(registries)) {
            this.applyModifier(set.value());
        }
    }

    private void applyModifier(StructureSet set) {
        StructureSetAccessor structureSetAccessor = ((StructureSetAccessor)(Object)set);
        List<StructureSet.StructureSelectionEntry> structureSelectionEntries = new ArrayList<>(set.structures());
        structureSetAccessor.setStructures(structureSelectionEntries.stream().filter(setEntry -> !entries.contains(setEntry.structure())).collect(Collectors.toList()));
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
