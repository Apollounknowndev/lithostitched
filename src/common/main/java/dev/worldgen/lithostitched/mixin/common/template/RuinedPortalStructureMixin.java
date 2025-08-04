package dev.worldgen.lithostitched.mixin.common.template;

import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RuinedPortalStructure.class)
public class RuinedPortalStructureMixin {
    @Redirect(
        method = "findGenerationPoint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
            ordinal = 1
        )
    )
    private ResourceLocation useStandardTemplateList(String name, Structure.GenerationContext generationContext) {
        if (!ConfigHandler.getConfig().breaksSeedParity()) {
            return ResourceLocation.withDefaultNamespace(name);
        }
        return generationContext.registryAccess().lookupOrThrow(LithostitchedRegistryKeys.TEMPLATE_LIST).get(TemplateLists.RUINED_PORTAL_STANDARD).get().value().getRandom(generationContext.random());
    }

    @Redirect(
        method = "findGenerationPoint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
            ordinal = 0
        )
    )
    private ResourceLocation useGiantTemplateList(String name, Structure.GenerationContext generationContext) {
        if (!ConfigHandler.getConfig().breaksSeedParity()) {
            return ResourceLocation.withDefaultNamespace(name);
        }
        return generationContext.registryAccess().lookupOrThrow(LithostitchedRegistryKeys.TEMPLATE_LIST).get(TemplateLists.RUINED_PORTAL_GIANT).get().value().getRandom(generationContext.random());
    }
}
