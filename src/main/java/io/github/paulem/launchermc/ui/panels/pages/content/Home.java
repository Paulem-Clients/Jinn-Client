package io.github.paulem.launchermc.ui.panels.pages.content;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.FabricVersion;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.flowarg.openlauncherlib.NoFramework;
import io.github.paulem.launchermc.Launcher;
import io.github.paulem.launchermc.game.MinecraftVersion;
import io.github.paulem.launchermc.game.MinecraftInfos;
import io.github.paulem.launchermc.ui.PanelManager;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.util.Saver;
import io.github.paulem.launchermc.utils.Errors;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Home extends ContentPanel {
    private final Saver saver = Launcher.getInstance().getSaver();
    private static boolean isDownloadingOrPlaying = false;

    final GridPane boxPane = new GridPane();
    final ProgressBar progressBar = new ProgressBar();
    final Label stepLabel = new Label();
    final Label fileLabel = new Label();

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/home.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setMinHeight(75);
        rowConstraints.setMaxHeight(75);
        this.layout.getRowConstraints().addAll(rowConstraints, new RowConstraints());
        boxPane.getStyleClass().add("box-pane");
        setCanTakeAllSize(boxPane);
        boxPane.setPadding(new Insets(20));
        this.layout.add(boxPane, 0, 0);
        this.layout.getStyleClass().add("home-layout");

        progressBar.getStyleClass().add("download-progress");
        stepLabel.getStyleClass().add("download-status");
        fileLabel.getStyleClass().add("download-status");

        progressBar.setTranslateY(-15);
        setCenterH(progressBar);
        setCanTakeAllWidth(progressBar);

        stepLabel.setTranslateY(5);
        setCenterH(stepLabel);
        setCanTakeAllSize(stepLabel);

        fileLabel.setTranslateY(20);
        setCenterH(fileLabel);
        setCanTakeAllSize(fileLabel);

        this.showPlayButton();
    }

    private void showPlayButton() {
        boxPane.getChildren().clear();
        Button playBtn = new Button("Jouer");
        final var playIcon = new MaterialDesignIconView<>(MaterialDesignIcon.G.GAMEPAD);
        playIcon.getStyleClass().add("play-icon");
        setCanTakeAllSize(playBtn);
        setCenterH(playBtn);
        setCenterV(playBtn);
        playBtn.getStyleClass().add("play-btn");
        playBtn.setGraphic(playIcon);
        playBtn.setOnMouseClicked(e -> this.play());
        boxPane.getChildren().add(playBtn);
    }

    private void play() {
        isDownloadingOrPlaying = true;
        boxPane.getChildren().clear();
        setProgress(0, 0);
        boxPane.getChildren().addAll(progressBar, stepLabel, fileLabel);

        new Thread(this::update).start();
    }

    public void update() {
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
                    .withLogger(Launcher.getInstance().getLogger())
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
                        .filter(file -> new ComparableVersion(getVersionFabricLoader(file.getName())).compareTo(latestVersion) != 0)
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
            Launcher.getInstance().getLogger().printStackTrace(e);
            Launcher.getInstance().getLogger().info("Lancement en mode hors-ligne...");
            Platform.runLater(() -> {
                setProgress(1, 1);
                setStatus(String.format("%s", StepInfo.OFFLINE.getDetails()));
            });
            this.startGame(MinecraftInfos.GAME_VERSION);
            //Platform.runLater(() -> this.panelManager.getStage().show());
        }
    }

    private static @Nullable ComparableVersion getComparableVersion(List<File> fabricloaderFiles) {
        @Nullable ComparableVersion latestVersion = null;
        for(File file : fabricloaderFiles) {
            String versionName = getVersionFabricLoader(file.getName());

            ComparableVersion version = new ComparableVersion(versionName);

            if(latestVersion == null) latestVersion = version;
            else if(version.compareTo(latestVersion) > 0) latestVersion = version;
        }

        return latestVersion;
    }

    private static String getVersionFabricLoader(String fileName){
        return fileName
                .replace("fabric-loader-", "")
                .replace("-" + MinecraftInfos.GAME_VERSION + ".json", "");
    }

    public void startGame(String gameVersion) {
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

            new Thread(() -> checkStopped(p)).start();
        } catch (Exception e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            JOptionPane.showMessageDialog(
                    null,
                    "Une erreur est survenue ! Merci d'envoyer ceci à paulem :\n" + Errors.getStackTrace("Une erreur est survenue :", e),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void checkStopped(Process p) {
        try {
            p.waitFor();
            isDownloadingOrPlaying = false;
            Platform.runLater(this::showPlayButton);
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

    public boolean isDownloadingOrPlaying() {
        return isDownloadingOrPlaying;
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