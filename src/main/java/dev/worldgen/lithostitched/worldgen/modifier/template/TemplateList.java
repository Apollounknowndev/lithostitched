package dev.worldgen.lithostitched.worldgen.modifier.template;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public record TemplateList(List<ResourceLocation> templates) {
    public static final Codec<TemplateList> CODEC = ExtraCodecs.nonEmptyList(ResourceLocation.CODEC.listOf()).xmap(TemplateList::new, TemplateList::templates);

    public TemplateList(List<ResourceLocation> templates) {
        this.templates = new ArrayList<>(templates);
    }

    public ResourceLocation getRandom(RandomSource randomSource) {
        return templates.get(randomSource.nextInt(templates.size()));
    }

    public void addAll(List<ResourceLocation> templates) {
        this.templates.addAll(templates);
    }
}
