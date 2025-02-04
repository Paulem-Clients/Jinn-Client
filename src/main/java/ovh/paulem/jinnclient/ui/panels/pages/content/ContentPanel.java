package ovh.paulem.jinnclient.ui.panels.pages.content;

import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.RowConstraints;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;
import org.kordamp.ikonli.javafx.FontIcon;
import ovh.paulem.jinnclient.Launcher;
import ovh.paulem.jinnclient.game.Launch;
import ovh.paulem.jinnclient.ui.assets.UserPane;
import ovh.paulem.jinnclient.ui.panels.Panel;
import javafx.scene.layout.GridPane;
import ovh.paulem.jinnclient.ui.panels.PanelManager;
import ovh.paulem.jinnclient.ui.panels.pages.SideBar;
import ovh.paulem.jinnclient.utils.Constants;

/**
 * This class represent all pages that has content, useful to make animations on their open/close
 */
public abstract class ContentPanel extends Panel {
    protected Launch launch;
    protected boolean isDownloadingOrPlaying = false;
    protected final ProgressBar progressBar = new ProgressBar();
    protected final Label stepLabel = new Label();
    protected final Label fileLabel = new Label();

    protected final GridPane contentPane = new GridPane();

    protected final GridPane content = new GridPane();

    @Override
    public void init(PanelManager panelManager, @Nullable SideBar sideBar) {
        super.init(panelManager, sideBar);

        content.getStyleClass().add("content-pane");
        setCanTakeAllSize(content);
        content.setPadding(new Insets(20));
        this.layout.add(content, 0, 1);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setMinHeight(75);
        rowConstraints.setMaxHeight(75);
        this.layout.getRowConstraints().addAll(rowConstraints, new RowConstraints());
        contentPane.getStyleClass().add("box-pane");
        setCanTakeAllSize(contentPane);
        contentPane.setPadding(new Insets(10));
        this.layout.add(contentPane, 0, 0);
        this.layout.getStyleClass().add("layout");

        launch = new Launch(this, saver, logger, contentPane, progressBar, stepLabel, fileLabel);

        Launcher.getInstance().getDiscordRPC().editPresence(Constants.RPC_LAUNCHER);

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
    }

    public void showPlayButton() {
        contentPane.getChildren().clear();
        Button playBtn = new Button("Lancer");
        FontIcon playIcon = FontIcon.of(FluentUiFilledMZ.PLAY_24);
        playIcon.getStyleClass().add("play-icon");
        setCanTakeAllSize(playBtn);
        setCenterH(playBtn);
        setCenterV(playBtn);
        playBtn.getStyleClass().add("play-btn");
        playBtn.setGraphic(playIcon);
        playBtn.setOnMouseClicked(e -> launch.play());
        contentPane.getChildren().add(playBtn);

        if (Launcher.getInstance().getAuthInfos() != null) {
            new UserPane(this, contentPane, this).show();
        }
    }

    public boolean isDownloadingOrPlaying() {
        return isDownloadingOrPlaying;
    }

    public void setDownloadingOrPlaying(boolean downloadingOrPlaying) {
        isDownloadingOrPlaying = downloadingOrPlaying;
    }
}
