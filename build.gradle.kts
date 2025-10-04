plugins {
    kotlin("jvm") version "2.1.0"
    id("earth.terrarium.cloche") version "0.13.4-patched3"
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
    mavenLocal()
    mavenCentral()
    maven("https://api.modrinth.com/maven")
}

group = "dev.worldgen.lithostitched"
version = "1.5.0"

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

    val shared1211 = common("shared:1.21.1") {
        mixins.from(file("src/shared/1.21.1/main/lithostitched.1211.mixins.json"))
    }
    val shared1219 = common("shared:1.21.9") {
        mixins.from(file("src/shared/1.21.9/main/lithostitched.1219.mixins.json"))
    }

    fabric("fabric:1.21.1") {
        dependsOn(shared1211)

        loaderVersion = "0.17.0"
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

    fabric("fabric:1.21.9") {
        dependsOn(shared1219)

        loaderVersion = "0.17.2"
        minecraftVersion = "1.21.9"
        mixins.from(file("src/fabric/1.21.9/main/lithostitched.fabric.mixins.json"))

        dependencies {
            fabricApi("0.133.14")
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

        loaderVersion = "21.1.206"
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

    neoforge("neoforge:1.21.9") {
        dependsOn(shared1219)

        loaderVersion = "21.9.1-beta"
        minecraftVersion = "1.21.9"
        mixins.from(file("src/neoforge/1.21.9/main/lithostitched.neoforge.mixins.json"))

        runs {
            client()
            server()
        }
    }
}