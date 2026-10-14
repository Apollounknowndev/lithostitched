package dev.worldgen.lithostitched.api.tag;

import dev.worldgen.lithostitched.Lithostitched;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public interface LithostitchedBlockTags {
	/**
	 * Blocks in this tag can have Abandoned Mineshaft log/chain extensions built off of them.
	 */
	TagKey<Block> SUPPORTS_MINESHAFT_EXTENSIONS = create("supports_mineshaft_extension");
	/**
	 * Blocks in this tag can have leaves from overgrown Ruined Portals generated on top of them.
	 */
	TagKey<Block> SUPPORTS_RUINED_PORTAL_LEAVES = create("supports_ruined_portal_leaves");
	
	private static TagKey<Block> create(String name) {
		return TagKey.create(Registries.BLOCK, Lithostitched.id(name));
	}
}
