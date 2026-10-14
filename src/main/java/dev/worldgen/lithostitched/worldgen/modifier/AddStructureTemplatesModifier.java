package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateList;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record AddStructureTemplatesModifier(Optional<LoadPredicate> predicate, int priority, HolderTarget<TemplateList> targets, List<Identifier> templates) implements WorldgenModifier {
    public static final MapCodec<AddStructureTemplatesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(AddStructureTemplatesModifier::priority),
        HolderTarget.createCodec(LithostitchedRegistries.TEMPLATE_LIST).fieldOf("targets").forGetter(AddStructureTemplatesModifier::targets),
        ExtraCodecs.compactListCodec(Identifier.CODEC).fieldOf("templates").forGetter(AddStructureTemplatesModifier::templates)
    ).apply(instance, AddStructureTemplatesModifier::new));
    
    @Override
    public void apply(RegistryAccess registries) {
        for (Holder<TemplateList> list : this.targets.getHolders(registries)) {
            list.value().addAll(this.templates);
        }
    }
    
    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
