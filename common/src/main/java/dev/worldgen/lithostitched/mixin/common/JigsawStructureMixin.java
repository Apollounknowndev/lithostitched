package dev.worldgen.lithostitched.mixin.common;

import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawConfig;
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Optional;

@Mixin(JigsawStructure.class)
public class JigsawStructureMixin {

    @Shadow @Final private List<PoolAliasBinding> poolAliases;

    @Redirect(
            method = "findGenerationPoint",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/pools/JigsawPlacement;addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;ILnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/pools/DimensionPadding;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)Ljava/util/Optional;"
            )
    )
    private Optional<Structure.GenerationStub> init(Structure.GenerationContext context, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, int size, BlockPos pos, boolean useExpansionHack, Optional<Heightmap.Types> heightmapProjection, int maxDistToCenter, PoolAliasLookup lookup, DimensionPadding padding, LiquidSettings liquidSettings) {
        return AlternateJigsawGenerator.generate(context, new AlternateJigsawConfig(startPool, startJigsawName, ConstantInt.of(size), ConstantHeight.of(VerticalAnchor.BOTTOM), useExpansionHack, heightmapProjection, maxDistToCenter, poolAliases, padding, liquidSettings), true, size, pos, lookup);
    }
}
