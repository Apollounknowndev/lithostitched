package dev.worldgen.lithostitched.worldgen.modifier.template;

import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

import java.util.Map;

public interface TemplateLists {
    Map<Integer, String> MANSION_FLOORS = Map.of(1, "first_floor/", 2, "second_floor/", 3, "third_floor/");

    ResourceKey<TemplateList> NETHER_FOSSIL = key("nether_fossil");

    ResourceKey<TemplateList> RUINED_PORTAL_STANDARD = key("ruined_portal/standard");
    ResourceKey<TemplateList> RUINED_PORTAL_GIANT = key("ruined_portal/giant");

    ResourceKey<TemplateList> SHIPWRECK_BEACHED = key("shipwreck/beached");
    ResourceKey<TemplateList> SHIPWRECK_OCEAN = key("shipwreck/ocean");

    private static ResourceKey<TemplateList> key(String name) {
        return ResourceKey.create(LithostitchedRegistries.TEMPLATE_LIST, Lithostitched.id(name));
    }

    static ResourceKey<TemplateList> mansion(int floor, String name) {
        return ResourceKey.create(LithostitchedRegistries.TEMPLATE_LIST, Lithostitched.id("woodland_mansion/" + MANSION_FLOORS.get(floor) + name));
    }

    static Identifier getRandom(RegistryAccess registries, ResourceKey<TemplateList> list, RandomSource random) {
        return registries.lookupOrThrow(LithostitchedRegistries.TEMPLATE_LIST).get(list).get().value().getRandom(random);
    }

    interface Mansion {
    }
}
