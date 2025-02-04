package ovh.paulem.jinnclient.ui.assets;

import animatefx.animation.AnimationFX;
import animatefx.animation.FadeInUpBig;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;
import ovh.paulem.jinnclient.ui.panels.pages.SideBar;
import ovh.paulem.jinnclient.utils.FxUtils;

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
            List<GridPane> list = getGridPanes(newsCards, widthReducers);
            cardsBox.getChildren().addAll(list);
            newsContainer.getChildren().add(cardsBox);
        }

        FxUtils.delay(200, () -> {
            // Play FadeIn animation sequentially
            playFadeInAnimation(newsContainer.getChildren()
                    .stream()
                    .map(child -> (HBox) child)
                    .map(Pane::getChildren)
                    .flatMap(nodes -> nodes
                            .stream()
                            .map(node -> (GridPane) node))
                    .toList(), 0);
        });
    }

    private @NotNull List<GridPane> getGridPanes(NewsCard[] newsCards, double widthReducers) {
        List<GridPane> list = new ArrayList<>();
        for (NewsCard newsCard : newsCards) {
            if (newsCard != null) {
                GridPane gridPane = newsCard.getGridPane();
                double width = (sideBar.getPanelManager().getStage().getWidth() - widthReducers - newsCard.getGridPane().getPadding().getRight() - newsCard.getGridPane().getPadding().getLeft()) / newsCards.length;
                gridPane.setMinWidth(width);
                gridPane.setMaxWidth(width);
                gridPane.setOpacity(0);
                list.add(gridPane);
            }
        }
        return list;
    }

    private void playFadeInAnimation(List<GridPane> gridPanes, int index) {
        if (index < gridPanes.size()) {
            GridPane actual = gridPanes.get(index);

            AnimationFX animation = new FadeInUpBig(actual);
            animation.setOnFinished(event -> {
                actual.setOpacity(1);
                playFadeInAnimation(gridPanes, index + 1);
            });
            animation.setSpeed(4);
            animation.play();
        }
    }
}