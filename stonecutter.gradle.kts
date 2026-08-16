plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "neoforge"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    val loader = current.project

    // Makes version- and loader-specific properties apply from `stoncutter.properties.toml`
    properties {
        tags("21.1", loader)
    }

    // Adds constants to Stonecutter comments (i.e. for `//? if fabric {...`)
    constants {
        match(loader, "fabric", "neoforge")
    }

    swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    constants["release"] = properties.get<String>("mod.id") != "template"
    dependencies["fapi"] = properties.getOrNull<String>("deps.fabric_api") ?: "0"
}
