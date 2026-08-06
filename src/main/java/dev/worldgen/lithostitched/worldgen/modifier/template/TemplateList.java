package dev.worldgen.lithostitched.worldgen.modifier.template;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public record TemplateList(List<Identifier> templates) {
    public static final Codec<TemplateList> CODEC = ExtraCodecs.nonEmptyList(Identifier.CODEC.listOf()).xmap(TemplateList::new, TemplateList::templates);

    public TemplateList(List<Identifier> templates) {
        this.templates = new ArrayList<>(templates);
    }

    public Identifier getRandom(RandomSource randomSource) {
        return templates.get(randomSource.nextInt(templates.size()));
    }

    public void addAll(List<Identifier> templates) {
        this.templates.addAll(templates);
    }
}
