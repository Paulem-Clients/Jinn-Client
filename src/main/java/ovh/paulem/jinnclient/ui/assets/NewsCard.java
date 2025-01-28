package ovh.paulem.jinnclient.ui.assets;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class NewsCard {
    private final GridPane gridPane;

    public NewsCard(String title, String content) {
        gridPane = new GridPane();
        gridPane.getStyleClass().add("news-card");
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        // gridPane.setMaxWidth((Launcher.getInstance().panelManager.getStage().getWidth()-350-40) / 4);

        Label newsTitle = new Label(title);
        newsTitle.setWrapText(true);
        newsTitle.getStyleClass().add("news-title");
        gridPane.add(newsTitle, 0, 0);

        Label newsContent = new Label(content);
        newsContent.setWrapText(true);
        newsContent.getStyleClass().add("news-content");
        gridPane.add(newsContent, 0, 1);
    }

    public GridPane getGridPane() {
        return gridPane;
    }
}