package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingConfig;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingStructure;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

public record SetStructureSpawnConditionModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Structure> structures, PlacementCondition spawnCondition, boolean append) implements WorldgenModifier {
    public static final MapCodec<SetStructureSpawnConditionModifier> CODEC = RecordCodecBuilder.<SetStructureSpawnConditionModifier>mapCodec(instance -> instance.group(
        PREDICATE_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(SetStructureSpawnConditionModifier::priority),
        registrySet(Registries.STRUCTURE, "structures").forGetter(SetStructureSpawnConditionModifier::structures),
        PlacementCondition.CODEC.fieldOf("spawn_condition").forGetter(SetStructureSpawnConditionModifier::spawnCondition),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetStructureSpawnConditionModifier::append)
    ).apply(instance, SetStructureSpawnConditionModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        this.structures.forEach(structure -> this.applyModifier(registries, structure));
    }

    private void applyModifier(RegistryAccess registries, Holder<Structure> structure) {
        if (structure.value() instanceof DelegatingStructure delegating) {
            delegating.config().setSpawnCondition(this.spawnCondition, this.append);
        } else {
            if (structure instanceof Holder.Reference<Structure> reference) {
                final Structure delegating = new DelegatingStructure(new DelegatingConfig(Holder.direct(structure.value()), Optional.of(this.spawnCondition)));
                ((HolderReferenceAccessor<Structure>)structure).setValue(delegating);
                ((MappedRegistryAccessor<Structure>) Lithostitched.registry(registries, Registries.STRUCTURE)).getByValue().put(delegating, reference);
            }
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
