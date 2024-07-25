package io.github.paulem.launchermc.game;

import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.FabricVersion;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.util.Saver;
import io.github.paulem.launchermc.Launcher;
import io.github.paulem.launchermc.game.minecraft.MinecraftInfos;
import io.github.paulem.launchermc.game.minecraft.MinecraftVersion;
import io.github.paulem.launchermc.ui.panels.pages.content.Home;
import io.github.paulem.launchermc.utils.Constants;
import io.github.paulem.launchermc.utils.Errors;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record Launch(Home home, Saver saver, ILogger logger, GridPane boxPane, ProgressBar progressBar, Label stepLabel, Label fileLabel) {
    public void play() {
        home.setDownloadingOrPlaying(true);
        boxPane.getChildren().clear();
        setProgress(0, 0);
        boxPane.getChildren().addAll(progressBar, stepLabel, fileLabel);

        new Thread(this::update).start();
    }

    private void update() {
        IProgressCallback callback = new IProgressCallback() {
            private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
            private String stepTxt = "";
            private String percentTxt = "0.0%";

            @Override
            public void step(Step step) {
                Platform.runLater(() -> {
                    StepInfo stepInfo = StepInfo.valueOf(step.name());
                    stepTxt = stepInfo.getDetails();
                    if(stepInfo == StepInfo.END) setStatus(String.format("%s", stepTxt));
                    else if(Objects.equals(percentTxt, "100%")) setStatus(String.format("%s", StepInfo.END.getDetails()));
                    else setStatus(String.format("%s (%s)", stepTxt, percentTxt));
                });
            }

            @Override
            public void update(DownloadList.DownloadInfo info) {
                Platform.runLater(() -> {
                    percentTxt = decimalFormat.format(info.getDownloadedBytes() * 100.d / info.getTotalToDownloadBytes());
                    setProgress(info.getDownloadedBytes(), info.getTotalToDownloadBytes());
                    if(!percentTxt.matches(".*\\d.*")) {
                        percentTxt = "100";
                        setProgress(1, 1);
                    }
                    percentTxt += "%";
                    setStatus(String.format("%s (%s)", stepTxt, percentTxt));
                });
            }

            @Override
            public void onFileDownloaded(Path path) {
                Platform.runLater(() -> {
                    String p = path.toString();
                    fileLabel.setText("..." + p.replace(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath(), ""));
                });
            }
        };

        try {
            Launcher.getInstance().getDiscordRPC().editPresence(Constants.RPC_UPDATE);

            MinecraftVersion.GAME = new FabricVersion.FabricVersionBuilder()
                    .withFabricVersion(MinecraftInfos.MODLOADER_VERSION)
                    .withMods(MinecraftInfos.MODS_LIST_URL)
                    .withCurseMods(MinecraftInfos.MODS_LIST_URL)
                    .withFileDeleter(new ModFileDeleter(true))
                    .build();

            final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                    .withName(MinecraftInfos.GAME_VERSION)
                    .withSnapshot(false)
                    .build();

            final FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                    .withVanillaVersion(vanillaVersion)
                    .withModLoaderVersion(MinecraftVersion.GAME)
                    .withLogger(this.logger)
                    .withProgressCallback(callback)
                    .build();

            updater.update(Launcher.getInstance().getLauncherDir());

            // Remove old fabric loader
            File launcherDir = Launcher.getInstance().getLauncherDir().toFile();
            File[] launcherFiles = launcherDir.listFiles();

            if(launcherDir.isDirectory() && launcherFiles != null) {
                List<File> fabricloaderFiles = Arrays.stream(launcherFiles)
                        // Check if it is a fabric-loader-version-MCVERSION.json
                        .filter(file -> file.getName().startsWith("fabric-loader-") && file.getName().endsWith("-" + MinecraftInfos.GAME_VERSION + ".json"))
                        .toList();

                ComparableVersion latestVersion = getComparableVersion(fabricloaderFiles);

                List<File> filesToRemove = fabricloaderFiles.stream()
                        .filter(file -> latestVersion != null && new ComparableVersion(getVersionFabricLoader(file.getName())).compareTo(latestVersion) != 0)
                        .toList();

                for(File file : filesToRemove) {
                    logger.info("Removing " + file.getName() +  "...");
                    file.delete();
                }

                if(latestVersion == null) logger.err("Error finding latest version ! It's now automatic");
                else logger.info("Latest found version : " + latestVersion);
            }

            this.startGame(updater.getVanillaVersion().getName());
        } catch (Exception e) {
            this.logger.printStackTrace(e);
            this.logger.info("Lancement en mode hors-ligne...");
            Platform.runLater(() -> {
                setProgress(1, 1);
                setStatus(String.format("%s", StepInfo.OFFLINE.getDetails()));
            });
            this.startGame(MinecraftInfos.GAME_VERSION);
        }
    }

    private @Nullable ComparableVersion getComparableVersion(List<File> fabricloaderFiles) {
        @Nullable ComparableVersion latestVersion = null;
        for(File file : fabricloaderFiles) {
            String versionName = getVersionFabricLoader(file.getName());

            ComparableVersion version = new ComparableVersion(versionName);

            if(latestVersion == null) latestVersion = version;
            else if(version.compareTo(latestVersion) > 0) latestVersion = version;
        }

        return latestVersion;
    }

    private String getVersionFabricLoader(String fileName){
        return fileName
                .replace("fabric-loader-", "")
                .replace("-" + MinecraftInfos.GAME_VERSION + ".json", "");
    }

    private void startGame(String gameVersion) {
        try {
            NoFramework noFramework = new NoFramework(
                    Launcher.getInstance().getLauncherDir(),
                    Launcher.getInstance().getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );

            noFramework.getAdditionalVmArgs().add(this.getRamArgsFromSaver());
            Process p = noFramework.launch(gameVersion,
                    MinecraftInfos.MODLOADER_VERSION.split("-").length >= 2 ? MinecraftInfos.MODLOADER_VERSION.split("-")[1] : MinecraftInfos.MODLOADER_VERSION,
                    MinecraftInfos.MODLOADER);

            Launcher.getInstance().getDiscordRPC().editPresence(Constants.RPC_CONNECTED);

            new Thread(() -> checkStopped(p)).start();
        } catch (Exception e) {
            this.logger.printStackTrace(e);
            JOptionPane.showMessageDialog(
                    null,
                    "Une erreur est survenue ! Merci d'envoyer ceci à paulem :\n" + Errors.getStackTrace("Une erreur est survenue :", e),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void checkStopped(Process p) {
        try {
            p.waitFor();
            home.setDownloadingOrPlaying(false);
            Platform.runLater(home::showPlayButton);

            Launcher.getInstance().getDiscordRPC().editPresence(Constants.RPC_LAUNCHER);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRamArgsFromSaver() {
        int val = 1024;
        try {
            if (saver.get("maxRam") != null) {
                val = Integer.parseInt(saver.get("maxRam"));
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException error) {
            saver.set("maxRam", String.valueOf(val));
            saver.save();
        }

        return "-Xmx" + val + "M";
    }

    public void setStatus(String status) {
        this.stepLabel.setText(status);
    }

    public void setProgress(double current, double max) {
        this.progressBar.setProgress(current / max);
    }

    @SuppressWarnings("unused")
    public enum StepInfo {
        READ("Lecture du fichier json..."),
        DL_LIBS("Téléchargement des libraries..."),
        DL_ASSETS("Téléchargement des ressources..."),
        EXTRACT_NATIVES("Extraction des natives..."),
        FORGE("Installation de forge..."),
        FABRIC("Installation de fabric..."),
        MODS("Téléchargement des mods..."),
        EXTERNAL_FILES("Téléchargement des fichier externes..."),
        POST_EXECUTIONS("Exécution post-installation..."),
        MOD_LOADER("Installation du mod loader..."),
        INTEGRATION("Intégration des mods..."),
        END("Terminé ! Lancement du jeu en cours..."),
        OFFLINE("Lancement du jeu en mode hors-ligne...");

        final String details;

        StepInfo(String details) {
            this.details = details;
        }

        public String getDetails() {
            return details;
        }
    }
}
