plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.2-neoforge"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    // Makes version- and loader-specific properties apply from `stoncutter.properties.toml`
    properties {
        tags(version, loader)
    }

    // Adds constants to Stonecutter comments (i.e. for `//? if fabric {...`)
    constants {
        match(loader, "fabric", "neoforge")
    }

    swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    constants["release"] = properties.get<String>("mod.id") != "template"
    dependencies["fapi"] = properties.getOrNull<String>("deps.fabric_api") ?: "0"

    replacements {
        string(current.parsed >= "26.1") {
            replace("ResourceLocation", "Identifier")
            replace("classTweaker v2 named", "classTweaker v2 official")
        }
    }
}
