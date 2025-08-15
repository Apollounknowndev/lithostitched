package dev.worldgen.lithostitched.worldgen.surface.conditions;

import dev.worldgen.lithostitched.worldgen.surface.technical.IContextExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * Surface conditions, used for evaluating the condition stack.
 * @author VoidsongDragonfly
 */
public class LithostitchedSurfaceConditions {
    /**
     * Column-lazy condition for checking if this column is a part of a cliff. Avoids cave lips and the edges of cave lips at the bottom side.
     * Similar to {@link net.minecraft.world.level.levelgen.SurfaceRules.Context.SteepMaterialCondition `minecraft:steep`} but does not apply only in one cardinal direction and properly determines cliffs from heightmap.
     * May break if surface rules are not evaluated from top to bottom; however currently surface rules are evaluated from the top down.
     * @author VoidsongDragonfly
     */
    public static class CliffCondition extends SurfaceRules.LazyXZCondition {
        public CliffCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            // Variable store for future operations
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            ChunkAccess chunk = this.context.chunk;
            int north = Math.max(j - 1, 0);
            int east  = Math.min(i + 1, 15);
            int south = Math.min(j + 1, 15);
            int west  = Math.max(i - 1, 0);
            // Heightmap heights
            int northHeight = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, north);
            int eastHeight  = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, east, j);
            int southHeight = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, south);
            int westHeight  = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, west, j);
            // Get the height difference we need to check to ensure this is a cliff
            int difference = (Math.max(northHeight, Math.max(eastHeight, Math.max(southHeight, westHeight))) - Math.min(northHeight, Math.min(eastHeight, Math.min(southHeight, westHeight))));
            // Exit early, to ensure we don't make the more intensive checks.
            if (difference < 3) return false;
            // Check that we're not on a cliff top-lip. Since the scan is top-down, this catches tops first despite being LazyXZ TODO: possibly not make this apply to big cliff-sides
            boolean lip = this.context.stoneDepthBelow <= 2;
            // Check that we're not at the bottom of a hanging-over cave entrance. These air checks function because, and so this catches the top despite being LazyXZ
            lip = lip || chunk.getBlockState(new BlockPos(this.context.blockX, this.context.blockY+2, this.context.blockZ-j+north)).isAir() && this.context.blockY+2 < northHeight;
            lip = lip || chunk.getBlockState(new BlockPos(this.context.blockX-i+east, this.context.blockY+2, this.context.blockZ)).isAir() && this.context.blockY+2 < eastHeight;
            lip = lip || chunk.getBlockState(new BlockPos(this.context.blockX, this.context.blockY+2, this.context.blockZ-j+south)).isAir() && this.context.blockY+2 < southHeight;
            lip = lip || chunk.getBlockState(new BlockPos(this.context.blockX-i+west, this.context.blockY+2, this.context.blockZ)).isAir() && this.context.blockY+2 < westHeight;
            return !lip;
        }
    }

    /**
     * Column-lazy condition for checking if this column is perfectly flat on the surface.
     * Does not work 100% faithfully on chunk borders due to the vagarities of surface rule chunk access, may return true when not totally flat.
     * @author VoidsongDragonfly
     */
    public static class FlatCondition extends SurfaceRules.LazyXZCondition {
        public FlatCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            // Shift to chunkwise coordinates
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            // Ensure we're not outside the chunk
            int north = Math.max(j - 1, 0);
            int east  = Math.min(i + 1, 15);
            int south = Math.min(j + 1, 15);
            int west  = Math.max(i - 1, 0);
            // Check the heightmaps of the neighboring blocks at water-level. This is not optimized because it's only used in one rule currently.
            ChunkAccess chunk = this.context.chunk;
            int northHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, north);
            int eastHeight  = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, east, j);
            int southHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, south);
            int westHeight  = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, west, j);
            // Check deviation from expected height
            return Math.max(northHeight, Math.max(southHeight, Math.max(westHeight, eastHeight))) - Math.min(northHeight, Math.min(southHeight, Math.min(westHeight, eastHeight))) == 0 && northHeight == chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, j);
        }
    }

    /**
     * Column-lazy condition for checking if this column is perfectly flat on the surface.
     * Will return true only when verifiably flat; this by necessity cuts out chunk borders above sea level despite their possibility of being flat.
     * Intended for use with liquids such as water which MUST be flat or else they will flow.
     * @author VoidsongDragonfly
     */
    public static class FlatLiquidCondition extends SurfaceRules.LazyXZCondition {
        public FlatLiquidCondition(SurfaceRules.Context context) {
            super(context);
        }

        @Override
        protected boolean compute() {
            // Shift to chunkwise coordinates
            int i = this.context.blockX & 15;
            int j = this.context.blockZ & 15;
            // Ensure we're not outside the chunk
            int north = Math.max(j - 1, 0);
            int east  = Math.min(i + 1, 15);
            int south = Math.min(j + 1, 15);
            int west  = Math.max(i - 1, 0);
            // Check the heightmaps of the neighboring blocks at water-level. This is not optimized because it's only used in one rule currently.
            ChunkAccess chunk = this.context.chunk;
            int northHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, north);
            int eastHeight  = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, east, j);
            int southHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, south);
            int westHeight  = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, west, j);
            // Since we scan from the top down, we can ensure we're flat and not going to spill water downwards
            boolean bottom = !chunk.getBlockState(new BlockPos(this.context.blockX, this.context.blockY - 1, this.context.blockZ)).canBeReplaced();
            // Check deviation from expected height
            boolean flat = Math.max(northHeight, Math.max(southHeight, Math.max(westHeight, eastHeight))) - Math.min(northHeight, Math.min(southHeight, Math.min(westHeight, eastHeight))) == 0 && northHeight == chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, j);
            // The check to return false on chunk borders is a massive kludge, but I use this with _water_. I can't afford flowing water....
            boolean nonChunkBorder = !(((north == j || south == j)||(west == i || east == i)) && this.context.blockY > 63);
            // Combine all the checks together
            return flat && bottom && nonChunkBorder;
        }
    }

    /**
     * Non-lazy condition intended for simplifying non-water-top-layer-of-surface checks
     * @param context the {@link SurfaceRules.Context context} the condition evaluates with
     * @author VoidsongDragonfly
     */
    public record LandTopLayerCondition(SurfaceRules.Context context) implements SurfaceRules.Condition {
        @Override
        public boolean test() {
            return context.waterHeight == Integer.MIN_VALUE && context.stoneDepthAbove <= 1;
        }
    }

    /**
     * Non-lazy condition intended to simplify the process of checking whether a block is underwater
     * Shallowness follows the Vanilla {@link net.minecraft.world.level.levelgen.SurfaceRules.WaterConditionSource `minecraft:water`} condition as used in Vanilla surface rules.
     * @param context the {@link SurfaceRules.Context context} the condition evaluates with
     * @param shallow boolean for whether to check for shallow water only or to pass any underwater block
     * @author VoidsongDragonfly
     */
    public record UnderwaterCondition(SurfaceRules.Context context, boolean shallow) implements SurfaceRules.Condition {
        @Override
        public boolean test() {
            // Exit early if we're above water
            if (context.waterHeight == Integer.MIN_VALUE) return false;
            // If we don't care about shallowness, return early, else check the Vanilla "shallow water" parameters
            return !shallow || ((context.blockY + context.stoneDepthAbove) >= (context.waterHeight - 6 - context.surfaceDepth));
        }
    }

    /**
     * Non-lazy condition intended for use in ensuring grass and other surface blocks are not placed in subsurface caves.
     * Is not a perfect condition, is mostly intended as a good-enough heuristic to ensure the above. WILL pass true on some above-ground overhangs.
     * Intended to pass false on most overhangs that would let light into the blocks below them, but may not succeed.
     * Checks if a position is more than a set depth below the world surface and returns true if so.
     * @param context the {@link SurfaceRules.Context context} the condition evaluates with
     * @param depth the depth below the {@link Heightmap.Types `OCEAN_FLOOR_WG`} heightmap that this block must be lower than to return true
     * @author VoidsongDragonfly
     */
    public record CaveDepthCondition(SurfaceRules.Context context, int depth) implements SurfaceRules.Condition {
        @Override
        public boolean test() {
            int heightmapDepth = ((IContextExtension)(Object)context).lithostitched$getOceanHeightmapDepth();
            // Return early if we're above the necessary depth
            if (heightmapDepth - depth <= context.blockY) return false;
            // If we're shallower than twelve blocks, we do not need to check the air blocks above this block
            // We remove/add stoneDepthAbove to make sure we stay congruous with the top block of the cave
            int currentDepth = heightmapDepth - context.blockY + context.stoneDepthAbove;
            if (currentDepth < 12) return true;
            // Check to make sure we're not underneath a massive overhang by checking if greater than 2/3ths what's above is air
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(context.blockX, context.blockY + context.stoneDepthAbove, context.blockZ);
            for (int i = 1 + context.stoneDepthAbove; i < (currentDepth*3)/4; i++)
                if (!context.chunk.getBlockState(pos.setY(context.blockY + i)).canBeReplaced()) return true;
            // Variable store for future operations
            int i = context.blockX & 15;
            int j = context.blockZ & 15;
            // Movements within the chunk for close block checks
            int searchLevel = context.blockY + context.stoneDepthAbove- 2;
            int north = Math.max(j - 1, 0);
            int east  = Math.min(i + 1, 15);
            int south = Math.min(j + 1, 15);
            int west  = Math.max(i - 1, 0);
            // Now we check to make sure we're not on the side of a cliff in a windswept biome
            boolean lip = false;
            lip = lip || context.chunk.getBlockState(new BlockPos(context.blockX, searchLevel, context.blockZ-j+north)).isAir();
            lip = lip || context.chunk.getBlockState(new BlockPos(context.blockX-i+east, searchLevel, context.blockZ)).isAir();
            lip = lip || context.chunk.getBlockState(new BlockPos(context.blockX, searchLevel, context.blockZ-j+south)).isAir();
            lip = lip || context.chunk.getBlockState(new BlockPos(context.blockX-i+west, searchLevel, context.blockZ)).isAir();
            return lip;
        }
    }
}
