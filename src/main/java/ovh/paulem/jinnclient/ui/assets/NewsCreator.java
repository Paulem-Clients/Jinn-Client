package ovh.paulem.jinnclient.ui.assets;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import ovh.paulem.jinnclient.ui.panels.pages.SideBar;

import java.util.ArrayList;
import java.util.List;

public class NewsCreator {
    private static final int SPACING = 8;

    private final List<NewsCard[]> cards = new ArrayList<>();

    private final Pane newsContainer;
    private final SideBar sideBar;

    public NewsCreator(Pane newsContainer, SideBar sideBar) {
        this.newsContainer = newsContainer;
        this.sideBar = sideBar;
    }

    public void addNewsCards(NewsCard... newsCards) {
        for (NewsCard newsCard : newsCards) {
            addNewsCard(newsCard);
        }
    }

    public void addNewsCard(NewsCard newsCard) {
        if (cards.isEmpty() || cards.getLast()[3] != null) {
            cards.add(new NewsCard[4]);
        }
        NewsCard[] lastRow = cards.getLast();
        for (int i = 0; i < lastRow.length; i++) {
            if (lastRow[i] == null) {
                lastRow[i] = newsCard;
                break;
            }
        }
    }

    public void showCards() {
        // Calculate width reducers for the cards to fit in the container (sideBar width + padding)
        Insets paddings = ((Region) newsContainer.getParent()).getPadding();
        ObservableList<ColumnConstraints> columnConstraints = sideBar.getLayout().getColumnConstraints();
        double widthReducers = columnConstraints.getFirst().getMaxWidth() + paddings.getLeft() + paddings.getRight();

        // Create the cards
        for (NewsCard[] newsCards : cards) {
            HBox cardsBox = new HBox(SPACING);
            List<GridPane> list = new ArrayList<>();
            for (NewsCard newsCard : newsCards) {
                if (newsCard != null) {
                    GridPane gridPane = newsCard.getGridPane();
                    gridPane.setMaxWidth((sideBar.getPanelManager().getStage().getWidth()-widthReducers) / newsCards.length);
                    list.add(gridPane);
                }
            }
            cardsBox.getChildren().addAll(
                    list
            );
            newsContainer.getChildren().add(cardsBox);
        }
    }
}