plugins {
    kotlin("jvm") version "2.1.0"
    id("earth.terrarium.cloche") version "0.18.10"
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
version = "1.7.1"

cloche {
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

        dependencies {
            compileOnly("org.spongepowered:mixin:0.8.3")
        }
    }

    val sharedOld = common("shared:21.1") {
        mixins.from(file("src/shared/21.1/main/lithostitched.21.1.mixins.json"))
        accessWideners.from(file("src/shared/21.1/main/lithostitched.21.1.accesswidener"))
    }
    val sharedNew = common("shared:26.1") {
        mixins.from(file("src/shared/26.1/main/lithostitched.26.1.mixins.json"))
        accessWideners.from(file("src/shared/26.1/main/lithostitched.26.1.accesswidener"))
    }

    fabric("fabric:21.1") {
        dependsOn(sharedOld)

        loaderVersion = "0.18.4"
        minecraftVersion = "1.21.1"
        mixins.from(file("src/fabric/21.1/main/lithostitched.fabric.mixins.json"))

        mappings {
            official()
            custom(minecraftVersion.map {
                project.dependencies.create(files("mappings/$it.tiny"))
            })
        }

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

    fabric("fabric:26.1") {
        dependsOn(sharedNew)

        loaderVersion = "0.18.4"
        minecraftVersion = "26.1"
        mixins.from(file("src/fabric/26.1/main/lithostitched.fabric.mixins.json"))

        dependencies {
            fabricApi("0.144.3")
        }

        includedClient()
        runs {
            client()
            server()
        }

        metadata {
            entrypoint("client") {
                value = "dev.worldgen.lithostitched.client.LithostitchedFabricClient"
            }
            entrypoint("main") {
                value = "dev.worldgen.lithostitched.LithostitchedFabric"
            }
        }
    }

    neoforge("neoforge:21.1") {
        dependsOn(sharedOld)

        loaderVersion = "21.1.222"
        minecraftVersion = "1.21.1"
        mixins.from(file("src/neoforge/21.1/main/lithostitched.neoforge.mixins.json"))

        mappings {
            official()
            custom(minecraftVersion.map {
                project.dependencies.create(files("mappings/$it.tiny"))
            })
        }

        runs {
            client()
            server()
        }
    }

    neoforge("neoforge:26.1") {
        dependsOn(sharedNew)

        loaderVersion = "26.1.0.5-beta"
        minecraftVersion = "26.1"
        mixins.from(file("src/neoforge/26.1/main/lithostitched.neoforge.mixins.json"))

        runs {
            client()
            server()
        }
    }
}