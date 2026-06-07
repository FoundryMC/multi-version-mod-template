import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("multiloader-loader")
    id("net.neoforged.moddev")
}

neoForge {
    version = property("deps.neoforge") as String

    if (sc.current.parsed < "26.1") {
        parchment {
            minecraftVersion = property("parchment.minecraft") as String
            mappingsVersion = property("parchment.version") as String
        }
    }

    val generatedResourcesPath = project(":common").projectDir.resolve("src/generated/resources").absolutePath

    runs {
        register("client") {
            client()
        }

        register("server") {
            server()
            programArgument("--nogui")
        }

        // This run config launches GameTestServer and runs all registered gametests, then exits.
        // By default, the server will crash when no gametests are provided.
        // The gametest system is also enabled by default for other run configs under the /test command.
        register("gameTestServer") {
            type = "gameTestServer"
        }

        register("data") {
            if (sc.current.parsed >= "1.21.4") {
                clientData()
            } else {
                data()
            }

            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll(
                "--mod", project.property("mod.id") as String,
                "--output", generatedResourcesPath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod.id") as String)
            ideName = "NeoForge ${name.capitalized()} (${project.path})" // Unify the run config names with fabric
            gameDirectory = file("../../runs/${environment}")
        }
    }

    mods {
        register("${project.property("mod.id")}") {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
}

tasks {
    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}
