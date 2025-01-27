package ovh.paulem.jinnclient.game.minecraft;

import fr.flowarg.flowupdater.versions.fabric.FabricVersion;

public class MinecraftVersion {
    /*public static final QuiltVersion GAME = new QuiltVersion.QuiltVersionBuilder()
            .withQuiltVersion(MinecraftInfos.MODLOADER_VERSION)
            .withMods(Mod.getModsFromJson(MinecraftInfos.MODS_LIST_URL))
            .withFileDeleter(new ModFileDeleter(true))
            .build();*/

    // ----- POUR FABRIC -----
    public static FabricVersion GAME;


    /* ----- POUR FORGE -----
    new ForgeVersionBuilder(MinecraftInfos.FORGE_VERSION_TYPE)
            .withForgeVersion(MinecraftInfos.FORGE_VERSION)
            .withMods(Mod.getModsFromJson(MinecraftInfos.MODS_LIST_URL))
            .withFileDeleter(new ModFileDeleter(true))
            .build();
    */
}
