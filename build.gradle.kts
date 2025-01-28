import org.gradle.internal.jvm.Jvm
import org.panteleyev.jpackage.ImageType
import org.panteleyev.jpackage.JPackageTask

plugins {
    id("idea")
    id("com.gradleup.shadow") version "8.+"
    id("java")
    id("application")
    id("org.panteleyev.jpackageplugin") version "1.6.0"
}

group = "ovh.paulem.jinnclient"
version = "0.1"

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
    maven {
        name = "paulemReleases"
        url = uri("https://maven.paulem.ovh/releases")
    }
}

dependencies {
    implementation("fr.litarvan:openauth:1.+")
    implementation("fr.flowarg:materialdesignfontfx:7.+")

    implementation("fr.flowarg:flowupdater:1.9.2")
    implementation("fr.flowarg:openlauncherlib:3+")

    implementation("club.minnced:java-discord-rpc:2.0.2")

    implementation("com.google.code.gson:gson:2.+")
    implementation("org.jetbrains:annotations:24.+")
}

application {
    mainClass.set("$group.Main")
}

tasks.withType<JavaCompile>().configureEach {
    JavaVersion.VERSION_21.toString().also {
        sourceCompatibility = it
        targetCompatibility = it
    }
    options.encoding = "UTF-8"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.BELLSOFT
    }
}

val jvmOpts = listOf("-Dfile.encoding=UTF-8", "--add-exports=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED", "--add-opens=javafx.graphics/javafx.scene.layout=ALL-UNNAMED")

tasks.withType<JPackageTask>().configureEach {
    dependsOn(tasks.shadowJar)

    appName = rootProject.name
    appVersion = project.version.toString()
    vendor = "Paulem"
    copyright = "Copyright (c) 2025 Paulem"
    runtimeImage = Jvm.current().javaHome.toString()
    destination = "dist"
    input = "build/libs"
    mainJar = tasks.shadowJar.get().archiveFileName.get()
    mainClass = application.mainClass.get()
    javaOptions = jvmOpts
}

var infra = ""
tasks.register<JPackageTask>("zipjpackage") {
    group = tasks.jpackage.get().group

    type = ImageType.APP_IMAGE

    linux {
        infra = "linux"
    }

    mac {
        icon = "icons/icons.icns"
        infra = "macos"
    }

    windows {
        icon = "icons/icons.ico"

        winConsole = true
        infra = "windows"
    }

    finalizedBy("renameZip")
}

tasks.register<Zip>("renameZip") {
    group = tasks.jpackage.get().group
    archiveFileName.set(infra + "-" + rootProject.name + "-" + project.version + ".zip")
    destinationDirectory.set(layout.projectDirectory.dir("dist"))

    from(layout.projectDirectory.dir("dist/" + rootProject.name))
}

tasks.jpackage {
    linux {
        type = ImageType.DEB
    }

    mac {
        icon = "icons/icons.icns"

        type = ImageType.DMG
    }

    windows {
        icon = "icons/icons.ico"

        type = ImageType.MSI

        winConsole = true
        if(type == ImageType.EXE || type == ImageType.MSI) {
            winMenu = true
            winDirChooser = true
            winPerUserInstall = true
            winShortcut = true
            winShortcutPrompt = true
            // winUpdateUrl can be interesting for auto-updates
        }
    }
}

tasks.clean {
    dependsOn("deleteDist")
}

tasks.jar {
    finalizedBy(tasks.shadowJar)
}

tasks.shadowJar {
    mustRunAfter(tasks.distZip)
    mustRunAfter(tasks.distTar)
    mustRunAfter(tasks.startScripts)

    archiveVersion.set("")
    archiveClassifier.set("")
}

tasks.register<Delete>("deleteDist") {
    delete("dist")
}

tasks.register<JavaExec>("runShadowJar") {
    val javaPath = Jvm.current().javaExecutable.toString()

    group = "application"
    description = "Builds and runs the shadow jar using the specified Java path"

    dependsOn(tasks.shadowJar)

    classpath = files(tasks.shadowJar.get().archiveFile)
    setExecutable(javaPath)
    jvmArgs(jvmOpts)

    //finalizedBy(tasks.clean)
}