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
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Modifier} implementation that adds structure set entries to a {@link StructureSet} entry.
 *
 * @author Apollo
 */
public class AddStructureSetEntriesModifier extends Modifier {
    public static final Codec<AddStructureSetEntriesModifier> CODEC = RecordCodecBuilder.create(instance -> addModifierFields(instance).and(instance.group(
        ResourceLocation.CODEC.fieldOf("structure_set").forGetter(AddStructureSetEntriesModifier::rawStructureSetLocation),
        StructureSet.StructureSelectionEntry.CODEC.listOf().fieldOf("entries").forGetter(AddStructureSetEntriesModifier::entries),
        RegistryOps.retrieveGetter(Registries.STRUCTURE_SET)
    )).apply(instance, AddStructureSetEntriesModifier::new));
    private final ResourceKey<StructureSet> EMPTY_STRUCTURE_SET = ResourceKey.create(Registries.STRUCTURE_SET, new ResourceLocation(LithostitchedCommon.MOD_ID, "empty"));
    private final ResourceLocation rawStructureSetLocation;
    private final Holder<StructureSet> structureSet;
    private final List<StructureSet.StructureSelectionEntry> entries;

    public AddStructureSetEntriesModifier(ModifierPredicate predicate, ResourceLocation rawStructureSetLocation, List<StructureSet.StructureSelectionEntry> entries, HolderGetter<StructureSet> getter) {
        super(predicate, ModifierPhase.ADD);
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

    public List<StructureSet.StructureSelectionEntry> entries() {
        return this.entries;
    }

    @Override
    public void applyModifier() {
        if (this.structureSet.is(EMPTY_STRUCTURE_SET)) return;
        StructureSetAccessor structureSetAccessor = ((StructureSetAccessor)(Object)this.structureSet().value());
        List<StructureSet.StructureSelectionEntry> structureSelectionEntries = new ArrayList<>(this.structureSet().value().structures());
        structureSelectionEntries.addAll(this.entries());
        structureSetAccessor.setStructures(structureSelectionEntries);
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
