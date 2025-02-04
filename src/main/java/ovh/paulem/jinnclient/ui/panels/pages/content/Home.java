package ovh.paulem.jinnclient.ui.panels.pages.content;

import javafx.scene.layout.VBox;
import ovh.paulem.jinnclient.ui.panels.PanelManager;
import ovh.paulem.jinnclient.ui.panels.pages.SideBar;
import ovh.paulem.jinnclient.ui.assets.NewsCard;
import ovh.paulem.jinnclient.ui.assets.NewsCreator;

public class Home extends ContentPanel {
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

        VBox newsBox = new VBox(8);
        content.add(newsBox, 0, 0);

        // News Card
        NewsCard newsCard = new NewsCard("News Title", "This is a brief description of the news contentaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        NewsCard newsCarda = new NewsCard("Newsaa Title", "This is a brief description of the news contentaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        NewsCard newsCardaa = new NewsCard("Newsaa Title", "This is a brief description of the news contentaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        NewsCard newsCarada = new NewsCard("Newsaa Title", "This is a brief description of the news contentaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        NewsCard newsaCarada = new NewsCard("aaaa", "aaaa");

        NewsCreator newsCreator = new NewsCreator(newsBox, sideBar);
        newsCreator.addNewsCards(newsCard, newsCarda, newsCardaa, newsCarada, newsaCarada, new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"), new NewsCard("aaaa", "aaaa"));
        newsCreator.showCards();

        this.showPlayButton();
    }
}