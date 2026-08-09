package dev.worldgen.lithostitched.api.tag;

import dev.worldgen.lithostitched.impl.Lithostitched;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

@SuppressWarnings("unused")
public interface LithostitchedTemplatePoolTags {
	TagKey<StructureTemplatePool> TRIAL_SPAWNER_MELEE = create("trial_spawner/melee");
	TagKey<StructureTemplatePool> TRIAL_SPAWNER_SMALL_MELEE = create("trial_spawner/small_melee");
	TagKey<StructureTemplatePool> TRIAL_SPAWNER_RANGED = create("trial_spawner/ranged");
	TagKey<StructureTemplatePool> TRIAL_SPAWNER_SLOW_RANGED = create("trial_spawner/slow_ranged");
	
	private static TagKey<StructureTemplatePool> create(String name) {
		return TagKey.create(Registries.TEMPLATE_POOL, Lithostitched.id(name));
	}
}
