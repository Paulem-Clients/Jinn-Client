plugins {
    id("idea")
    id("io.github.goooler.shadow") version "8.+"
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.+"
}
apply(plugin = "org.openjfx.javafxplugin")

group = "io.github.paulem.launchermc"
version = "1.0.6"

repositories {
    mavenCentral()
    mavenLocal()

    maven {
        url = uri("https://jitpack.io")
        name = "JitPack"
    }
    maven {
        url = uri("https://litarvan.github.io/maven")
        name = "LitarvanMaven"
    }
    maven {
        url = uri("https://maven.scijava.org/content/repositories/public/")
        name = "ClubMinnced"
    }
}

dependencies {
    implementation("fr.litarvan:openauth:1.+")
    implementation("fr.flowarg:materialdesignfontfx:7.+")

    implementation("fr.flowarg:flowupdater:1+")
    implementation("fr.flowarg:openlauncherlib:3+")

    implementation("club.minnced:java-discord-rpc:2.0.2")

    implementation("com.google.code.gson:gson:2.10")
    implementation("org.jetbrains:annotations:24.1.0")
}

tasks.jar {
    dependsOn(tasks.shadowJar)
}

application {
    mainClass.set("io.github.paulem.launchermc.Main")
}

javafx {
    version = "22.+"
    modules("javafx.controls")
}

tasks.compileJava {
    project.properties["JAVA_VERSION"].toString().also {
        sourceCompatibility = it
        targetCompatibility = it
    }
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}