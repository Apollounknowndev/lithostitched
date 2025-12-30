plugins {
    kotlin("jvm") version "2.1.0"
    id("earth.terrarium.cloche") version "0.17.1"
}

repositories {
    cloche.librariesMinecraft()
    mavenCentral()
    cloche {
        main()
        mavenNeoforgedMeta()
        mavenNeoforged()
        mavenFabric()
    }
}

group = "dev.worldgen.lithostitched"
version = "1.5.7"

cloche {
    targets.all {
        mappings {
            official()
            custom(minecraftVersion.map {
                project.dependencies.create(files("mappings/$it.tiny"))
            })
        }
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

    val sharedOld = common("shared:1.21.1") {
        mixins.from(file("src/shared/1.21.1/main/lithostitched.1211.mixins.json"))
    }
    val sharedNew = common("shared:1.21.11") {
        mixins.from(file("src/shared/1.21.11/main/lithostitched.12111.mixins.json"))
    }

    fabric("fabric:1.21.1") {
        dependsOn(sharedOld)

        loaderVersion = "0.18.2"
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

    fabric("fabric:1.21.11") {
        dependsOn(sharedNew)

        loaderVersion = "0.18.2"
        minecraftVersion = "1.21.11"
        mixins.from(file("src/fabric/1.21.11/main/lithostitched.fabric.mixins.json"))

        dependencies {
            fabricApi("0.139.4")
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
        dependsOn(sharedOld)

        loaderVersion = "21.1.217"
        minecraftVersion = "1.21.1"
        mixins.from(file("src/neoforge/1.21.1/main/lithostitched.neoforge.mixins.json"))

        runs {
            client()
            server()
        }
    }

    neoforge("neoforge:1.21.11") {
        dependsOn(sharedNew)

        loaderVersion = "21.11.12-beta"
        minecraftVersion = "1.21.11"
        mixins.from(file("src/neoforge/1.21.11/main/lithostitched.neoforge.mixins.json"))

        runs {
            client()
            server()
        }
    }
}