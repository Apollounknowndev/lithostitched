package dev.worldgen.lithostitched.impl.worldgen.modifier.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.impl.worldgen.structure.DelegatingConfig;
import dev.worldgen.lithostitched.impl.worldgen.structure.DelegatingStructure;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

public record SetStructureAttributesModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Structure> structures, EnvironmentAttributeMap attributes, boolean append) implements WorldgenModifier {
    public static final MapCodec<SetStructureAttributesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(SetStructureAttributesModifier::priority),
        RegistryCodecs.holderSet(Registries.STRUCTURE).fieldOf("structures").forGetter(SetStructureAttributesModifier::structures),
        EnvironmentAttributeMap.CODEC_ONLY_POSITIONAL.fieldOf("attributes").forGetter(SetStructureAttributesModifier::attributes),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetStructureAttributesModifier::append)
    ).apply(instance, SetStructureAttributesModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        for (Holder<Structure> structure : this.structures) {
            if (structure.value() instanceof DelegatingStructure delegating) {
                delegating.config().setAttributes(this.attributes, this.append);
            } else {
                if (structure instanceof Holder.Reference<Structure> reference) {
                    DelegatingStructure delegating = new DelegatingStructure(new DelegatingConfig(Holder.direct(structure.value()), Optional.empty()));
                    delegating.config().setAttributes(this.attributes, this.append);
                    ((HolderReferenceAccessor<Structure>)structure).setValue(delegating);
                    ((MappedRegistryAccessor<Structure>) Lithostitched.registry(registries, Registries.STRUCTURE)).getByValue().put(delegating, reference);
                }
            }
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
