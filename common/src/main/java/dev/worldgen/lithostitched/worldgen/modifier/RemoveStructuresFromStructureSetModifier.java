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
public class RemoveStructuresFromStructureSetModifier extends Modifier {
    public static final Codec<RemoveStructuresFromStructureSetModifier> CODEC = RecordCodecBuilder.create(instance -> addModifierFields(instance).and(instance.group(
        ResourceLocation.CODEC.fieldOf("structure_set").forGetter(RemoveStructuresFromStructureSetModifier::rawStructureSetLocation),
        Structure.CODEC.listOf().fieldOf("structures").forGetter(RemoveStructuresFromStructureSetModifier::entries),
        RegistryOps.retrieveGetter(Registries.STRUCTURE_SET)
    )).apply(instance, RemoveStructuresFromStructureSetModifier::new));
    private final ResourceKey<StructureSet> EMPTY_STRUCTURE_SET = ResourceKey.create(Registries.STRUCTURE_SET, new ResourceLocation(LithostitchedCommon.MOD_ID, "empty"));
    private final ResourceLocation rawStructureSetLocation;
    private final Holder<StructureSet> structureSet;
    private final List<Holder<Structure>> entries;

    public RemoveStructuresFromStructureSetModifier(ModifierPredicate predicate, ResourceLocation rawStructureSetLocation, List<Holder<Structure>> entries, HolderGetter<StructureSet> getter) {
        super(predicate, ModifierPhase.REMOVE);
        this.rawStructureSetLocation = rawStructureSetLocation;
        var structureSetEntry = getter.get(predicate.test() ? ResourceKey.create(Registries.STRUCTURE_SET, rawStructureSetLocation) : EMPTY_STRUCTURE_SET);
        this.structureSet = structureSetEntry.get();
        this.entries = entries;
    }

    public ResourceLocation rawStructureSetLocation() {
        return this.rawStructureSetLocation;
    }

    public Holder<StructureSet> structureSet() {
        return this.structureSet;
    }

    public List<Holder<Structure>> entries() {
        return this.entries;
    }

    @Override
    public void applyModifier() {
        if (this.structureSet.is(EMPTY_STRUCTURE_SET)) return;
        StructureSetAccessor structureSetAccessor = ((StructureSetAccessor)(Object)this.structureSet().value());
        List<StructureSet.StructureSelectionEntry> structureSelectionEntries = new ArrayList<>(this.structureSet().value().structures());
        structureSetAccessor.setStructures(structureSelectionEntries.stream().filter(setEntry -> !entries.contains(setEntry.structure())).collect(Collectors.toList()));
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
