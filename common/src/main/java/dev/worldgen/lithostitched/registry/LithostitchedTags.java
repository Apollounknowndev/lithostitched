package dev.worldgen.lithostitched.registry;

import dev.worldgen.lithostitched.LithostitchedCommon;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public interface LithostitchedTags {
    TagKey<StructureProcessorList> SHIPWRECK_PALETTES = tag(Registries.PROCESSOR_LIST, "shipwreck_palettes");

    private static <T> TagKey<T> tag(ResourceKey<Registry<T>> key, String name) {
        return TagKey.create(key, LithostitchedCommon.id(name));
    }
}
