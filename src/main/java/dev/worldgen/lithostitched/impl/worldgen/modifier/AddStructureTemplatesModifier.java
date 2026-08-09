package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import dev.worldgen.lithostitched.impl.worldgen.modifier.template.TemplateList;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public record AddStructureTemplatesModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<TemplateList> targets, List<Identifier> templates) implements WorldgenModifier {
    public static final MapCodec<AddStructureTemplatesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(AddStructureTemplatesModifier::priority),
        RegistryCodecs.holderSet(LithostitchedRegistries.TEMPLATE_LIST).fieldOf("targets").forGetter(AddStructureTemplatesModifier::targets),
        LithostitchedCodecs.compactList(Identifier.CODEC).fieldOf("templates").forGetter(AddStructureTemplatesModifier::templates)
    ).apply(instance, AddStructureTemplatesModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        this.targets.forEach(holder -> holder.value().addAll(this.templates));
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
