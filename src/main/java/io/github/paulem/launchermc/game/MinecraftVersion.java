package io.github.paulem.launchermc.game;

import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.FabricVersion;

public class MinecraftVersion {
    /*public static final QuiltVersion GAME = new QuiltVersion.QuiltVersionBuilder()
            .withQuiltVersion(MinecraftInfos.MODLOADER_VERSION)
            .withMods(Mod.getModsFromJson(MinecraftInfos.MODS_LIST_URL))
            .withFileDeleter(new ModFileDeleter(true))
            .build();*/

    // ----- POUR FABRIC -----
    public static final FabricVersion GAME = new FabricVersion.FabricVersionBuilder()
            //.withFabricVersion(MinecraftInfos.MODLOADER_VERSION)
            .withMods(MinecraftInfos.MODS_LIST_URL)
            .withCurseMods(MinecraftInfos.MODS_LIST_URL)
            .withFileDeleter(new ModFileDeleter(true))
            .build();


    /* ----- POUR FORGE -----
    new ForgeVersionBuilder(MinecraftInfos.FORGE_VERSION_TYPE)
            .withForgeVersion(MinecraftInfos.FORGE_VERSION)
            .withMods(Mod.getModsFromJson(MinecraftInfos.MODS_LIST_URL))
            .withFileDeleter(new ModFileDeleter(true))
            .build();
    */
}
