package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateList;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.Identifier;

import java.util.List;

public record AddStructureTemplatesModifier(int priority, HolderSet<TemplateList> targets, List<Identifier> templates) implements Modifier {
    public static final MapCodec<AddStructureTemplatesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_DEFAULT.forGetter(AddStructureTemplatesModifier::priority),
        RegistryCodecs.homogeneousList(LithostitchedRegistryKeys.TEMPLATE_LIST).fieldOf("targets").forGetter(AddStructureTemplatesModifier::targets),
        LithostitchedCodecs.compactList(Identifier.CODEC).fieldOf("templates").forGetter(AddStructureTemplatesModifier::templates)
    ).apply(instance, AddStructureTemplatesModifier::new));

    @Override
    public void applyModifier() {
        this.targets.forEach(holder -> holder.value().addAll(this.templates));
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
