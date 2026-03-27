plugins {
    id("maven-publish")
    alias(libs.plugins.fabric.loom)
}

group = "one.oktw"
version = libs.versions.project.get()

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)

    // Require necessary Fabric API modules
    implementation(fabricApi.module("fabric-networking-api-v1", libs.versions.fabric.api.get()))
    implementation(fabricApi.module("fabric-api-base", libs.versions.fabric.api.get()))

    implementation(libs.toml4j)
    include(libs.toml4j)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
    withSourcesJar()
}

tasks.named<Jar>("jar") {
    from("LICENSE")
}

tasks.named<ProcessResources>("processResources") {
    filteringCharset = "UTF-8"

    val props = mapOf(
        "version" to libs.versions.project.get(),
        "java" to libs.versions.java.get(),
        "minecraft" to libs.versions.minecraft.get(),
        "fabric_loader" to libs.versions.fabric.loader.get(),
        "fabric_api" to libs.versions.fabric.api.get()
    )

    props.forEach { (key, value) ->
        inputs.property(key, value)
    }

    filesMatching("fabric.mod.json") {
        expand(props)
    }
}