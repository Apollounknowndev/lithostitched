package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingConfig;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingStructure;
import dev.worldgen.lithostitched.worldgen.structure.condition.StructureCondition;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;

public record SetStructureSpawnConditionModifier(ModifierPredicate predicate, Holder<Structure> structure, StructureCondition spawnCondition, boolean append) implements Modifier {
    public static final Codec<SetStructureSpawnConditionModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        Structure.CODEC.fieldOf("structure").forGetter(SetStructureSpawnConditionModifier::structure),
        StructureCondition.CODEC.fieldOf("spawn_condition").forGetter(SetStructureSpawnConditionModifier::spawnCondition),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetStructureSpawnConditionModifier::append)
    )).apply(instance, SetStructureSpawnConditionModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }


    @Override
    public ModifierPhase getPhase() {
        return this.append ? ModifierPhase.REPLACE : ModifierPhase.ADD;
    }

    @Override
    public void applyModifier(RegistryAccess registries) {
        if (this.structure.value() instanceof DelegatingStructure delegating) {
            delegating.config().setSpawnCondition(this.spawnCondition, this.append);
        } else {
            if (this.structure instanceof Holder.Reference<Structure> reference) {
                final Structure delegating = new DelegatingStructure(new DelegatingConfig(Holder.direct(this.structure.value()), this.spawnCondition));
                ((HolderReferenceAccessor<Structure>)this.structure).setValue(delegating);
                ((MappedRegistryAccessor<Structure>)registries.registryOrThrow(Registries.STRUCTURE)).getByValue().put(delegating, reference);
            }
        }
    }

    @Override
    public void applyModifier() {}

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
