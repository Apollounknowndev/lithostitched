plugins {
    kotlin("jvm") version "2.1.0"
    id("earth.terrarium.cloche") version "0.11.20"
}

repositories {
    cloche {
        mavenNeoforgedMeta()
        mavenNeoforged()
        mavenForge()
        mavenFabric()
        mavenParchment()
        librariesMinecraft()
        main()
    }
    mavenCentral()
    maven("https://api.modrinth.com/maven")
}

group = "dev.worldgen.lithostitched"
version = "1.5.0+beta2"

cloche {
    mappings {
        official()
    }

    metadata {
        modId = "lithostitched"
        name = "Lithostitched"
        description = "A library mod with new configurability and compatibility enhancements for worldgen"
        license = "MIT"
        icon = "pack.png"

        author("Apollo")
    }

    common {
        mixins.from(file("src/common/main/lithostitched.mixins.json"))
        accessWideners.from(file("src/common/main/lithostitched.accesswidener"))

        dependencies {
            compileOnly("org.spongepowered:mixin:0.8.3")
        }
    }

    val shared1211 = common("shared:1.21.1") {}
    val shared1218 = common("shared:1.21.8") {}

    fabric("fabric:1.21.1") {
        dependsOn(shared1211)

        loaderVersion = "0.16.13"
        minecraftVersion = "1.21.1"
        mixins.from(file("src/fabric/1.21.1/main/lithostitched.fabric.mixins.json"))

        dependencies {
            fabricApi("0.116.1")
        }

        includedClient()
        runs {
            client()
            server()
        }

        metadata {
            entrypoint("main") {
                value = "dev.worldgen.lithostitched.LithostitchedFabric"
            }
        }
    }

    fabric("fabric:1.21.8") {
        dependsOn(shared1218)

        loaderVersion = "0.16.13"
        minecraftVersion = "1.21.8"
        mixins.from(file("src/fabric/1.21.8/main/lithostitched.fabric.mixins.json"))

        dependencies {
            fabricApi("0.129.0")
        }

        includedClient()
        runs {
            client()
            server()
        }

        metadata {
            entrypoint("main") {
                value = "dev.worldgen.lithostitched.LithostitchedFabric"
            }
        }
    }

    neoforge("neoforge:1.21.1") {
        dependsOn(shared1211)

        loaderVersion = "21.1.26"
        minecraftVersion = "1.21.1"
        mixins.from(file("src/neoforge/1.21.1/main/lithostitched.neoforge.mixins.json"))

        runs {
            client()
            server()
        }

        metadata {
            dependencies {
            }
        }
    }

    neoforge("neoforge:1.21.8") {
        dependsOn(shared1218)

        loaderVersion = "21.8.4-beta"
        minecraftVersion = "1.21.8"
        mixins.from(file("src/neoforge/1.21.8/main/lithostitched.neoforge.mixins.json"))

        runs {
            client()
            server()
        }
    }
}