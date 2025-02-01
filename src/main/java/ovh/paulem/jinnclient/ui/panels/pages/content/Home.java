package ovh.paulem.jinnclient.ui.panels.pages.content;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;
import org.kordamp.ikonli.javafx.FontIcon;
import ovh.paulem.jinnclient.Launcher;
import ovh.paulem.jinnclient.game.Launch;
import ovh.paulem.jinnclient.ui.assets.UserPane;
import ovh.paulem.jinnclient.ui.panels.PanelManager;
import ovh.paulem.jinnclient.ui.panels.pages.SideBar;
import ovh.paulem.jinnclient.ui.assets.NewsCard;
import ovh.paulem.jinnclient.ui.assets.NewsCreator;
import ovh.paulem.jinnclient.utils.Constants;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.RowConstraints;

public class Home extends ContentPanel {
    private Launch launch;

    private final GridPane homeContent = new GridPane();

    private boolean isDownloadingOrPlaying = false;
    private final ProgressBar progressBar = new ProgressBar();
    private final Label stepLabel = new Label();
    private final Label fileLabel = new Label();

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/home.css";
    }

    @Override
    public void init(PanelManager panelManager, SideBar sideBar) {
        super.init(panelManager, sideBar);

        homeContent.getStyleClass().add("content-pane");
        setCanTakeAllSize(homeContent);
        homeContent.setPadding(new Insets(20));
        this.layout.add(homeContent, 0, 1);

        VBox newsBox = new VBox(8);
        homeContent.add(newsBox, 0, 2);

        // News Card
        NewsCard newsCard = new NewsCard("News Title", "This is a brief description of the news contentaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        NewsCard newsCarda = new NewsCard("Newsaa Title", "This is a brief description of the news contentaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        NewsCard newsCardaa = new NewsCard("Newsaa Title", "This is a brief description of the news contentaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        NewsCard newsCarada = new NewsCard("Newsaa Title", "This is a brief description of the news contentaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        NewsCard newsaCarada = new NewsCard("aaaa", "aaaa");

        NewsCreator newsCreator = new NewsCreator(newsBox, sideBar);
        newsCreator.addNewsCards(newsCard, newsCarda, newsCardaa, newsCarada, newsaCarada, new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"));
        newsCreator.showCards();

        launch = new Launch(this, saver, logger, contentPane, progressBar, stepLabel, fileLabel);

        Launcher.getInstance().getDiscordRPC().editPresence(Constants.RPC_LAUNCHER);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setMinHeight(75);
        rowConstraints.setMaxHeight(75);
        this.layout.getRowConstraints().addAll(rowConstraints, new RowConstraints());
        contentPane.getStyleClass().add("box-pane");
        setCanTakeAllSize(contentPane);
        contentPane.setPadding(new Insets(10));
        this.layout.add(contentPane, 0, 0);
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