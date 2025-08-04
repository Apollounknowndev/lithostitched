package dev.worldgen.lithostitched.worldgen.modifier.template;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import net.minecraft.resources.ResourceKey;

public interface TemplateLists {
    ResourceKey<TemplateList> RUINED_PORTAL_STANDARD = key("ruined_portal/standard");
    ResourceKey<TemplateList> RUINED_PORTAL_GIANT = key("ruined_portal/giant");

    private static ResourceKey<TemplateList> key(String name) {
        return ResourceKey.create(LithostitchedRegistryKeys.TEMPLATE_LIST, Lithostitched.id(name));
    }
}
