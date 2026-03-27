package dev.worldgen.lithostitched.api.tag;

import dev.worldgen.lithostitched.Lithostitched;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

@SuppressWarnings("unused")
public interface LithostitchedProcessorListTags {
	TagKey<StructureProcessorList> SHIPWRECK_PALETTES = create("shipwreck_palettes");
	
	private static TagKey<StructureProcessorList> create(String name) {
		return TagKey.create(Registries.PROCESSOR_LIST, Lithostitched.id(name));
	}
}
