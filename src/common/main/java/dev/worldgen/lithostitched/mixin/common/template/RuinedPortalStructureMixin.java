package dev.worldgen.lithostitched.mixin.common.template;

import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.worldgen.modifier.template.TemplateLists;
import net.minecraft.resources.Identifier;
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
            target = "Lnet/minecraft/resources/Identifier;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/Identifier;",
            ordinal = 1
        )
    )
    private Identifier useStandardTemplateList(String name, Structure.GenerationContext context) {
        if (!ConfigHandler.getConfig().breaksSeedParity()) {
            return Identifier.withDefaultNamespace(name);
        }
        return TemplateLists.getRandom(context.registryAccess(), TemplateLists.RUINED_PORTAL_STANDARD, context.random());
    }

    @Redirect(
        method = "findGenerationPoint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/Identifier;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/Identifier;",
            ordinal = 0
        )
    )
    private Identifier useGiantTemplateList(String name, Structure.GenerationContext context) {
        if (!ConfigHandler.getConfig().breaksSeedParity()) {
            return Identifier.withDefaultNamespace(name);
        }
        return TemplateLists.getRandom(context.registryAccess(), TemplateLists.RUINED_PORTAL_GIANT, context.random());
    }
}
