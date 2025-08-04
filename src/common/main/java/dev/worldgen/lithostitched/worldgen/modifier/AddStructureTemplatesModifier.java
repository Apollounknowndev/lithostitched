package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateList;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record AddStructureTemplatesModifier(int priority, Holder<TemplateList> target, List<ResourceLocation> templates) implements Modifier {
    public static final MapCodec<AddStructureTemplatesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_DEFAULT.forGetter(AddStructureTemplatesModifier::priority),
        RegistryFileCodec.create(LithostitchedRegistryKeys.TEMPLATE_LIST, TemplateList.CODEC, false).fieldOf("target").forGetter(AddStructureTemplatesModifier::target),
        LithostitchedCodecs.compactList(ResourceLocation.CODEC).fieldOf("templates").forGetter(AddStructureTemplatesModifier::templates)
    ).apply(instance, AddStructureTemplatesModifier::new));

    @Override
    public void applyModifier() {
        this.target.value().addAll(this.templates);
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
