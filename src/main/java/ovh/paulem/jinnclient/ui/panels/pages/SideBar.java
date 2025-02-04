package ovh.paulem.jinnclient.ui.panels.pages;

import javafx.scene.layout.*;
import org.kordamp.ikonli.fluentui.FluentUiFilledAL;
import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;
import org.kordamp.ikonli.javafx.FontIcon;
import ovh.paulem.jinnclient.ui.panels.PanelManager;
import ovh.paulem.jinnclient.ui.panels.Panel;
import ovh.paulem.jinnclient.ui.panels.pages.content.ContentPanel;
import ovh.paulem.jinnclient.ui.panels.pages.content.Home;
import ovh.paulem.jinnclient.ui.panels.pages.content.Settings;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class SideBar extends Panel {
    private final GridPane sidemenu = new GridPane();
    private final GridPane navContent = new GridPane();

    private Node activeLink = null;
    private ContentPanel currentPage = null;

    private Button homeBtn, storeBtn, settingsBtn;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/sidebar.css";
    }

    @Override
    public void init(PanelManager panelManager, SideBar sideBar) {
        super.init(panelManager, sideBar);

        // Background
        this.layout.getStyleClass().add("app-layout");
        setCanTakeAllSize(this.layout);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(120);
        columnConstraints.setMaxWidth(120);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());

        // Side menu
        this.layout.add(sidemenu, 0, 0);
        sidemenu.getStyleClass().add("sidemenu");
        setLeft(sidemenu);
        setCenterH(sidemenu);
        setCenterV(sidemenu);

        // Background Image
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 1, 0);

        // Nav content
        this.layout.add(navContent, 1, 0);
        navContent.getStyleClass().add("nav-content");
        setLeft(navContent);
        setCenterH(navContent);
        setCenterV(navContent);

        /*
         * Side menu
         */
        VBox icons = new VBox(25);
        icons.setTranslateY(75d);

        // Navigation
        homeBtn = new Button();
        homeBtn.getStyleClass().add("sidemenu-nav-btn");
        FontIcon homeIcon = FontIcon.of(FluentUiFilledAL.GAMES_24);
        homeIcon.getStyleClass().add("sidemenu-nav-btn-icon");
        homeBtn.setGraphic(homeIcon);
        homeBtn.setOnMouseClicked(e -> setPage(new Home(), homeBtn));

        storeBtn = new Button();
        storeBtn.getStyleClass().add("sidemenu-nav-btn");
        FontIcon storeIcon = FontIcon.of(FluentUiFilledAL.CART_24);
        storeIcon.getStyleClass().add("sidemenu-nav-btn-icon");
        storeBtn.setGraphic(storeIcon);
        storeBtn.setOnMouseClicked(e -> {
            try {
                Desktop.getDesktop().browse(URI.create("https://example.com"));
            } catch (IOException ex) {
                logger.printStackTrace(ex);
            }
        });

        settingsBtn = new Button();
        settingsBtn.getStyleClass().add("sidemenu-nav-btn");
        FontIcon settingsIcon = FontIcon.of(FluentUiFilledMZ.SETTINGS_24);
        settingsIcon.getStyleClass().add("sidemenu-nav-btn-icon");
        settingsBtn.setGraphic(settingsIcon);
        settingsBtn.setOnMouseClicked(e -> setPage(new Settings(), settingsBtn));

        icons.getChildren().addAll(homeBtn, storeBtn, settingsBtn);
        sidemenu.getChildren().add(icons);
    }

    @Override
    public void onShow() {
        super.onShow();
        setPage(new Home(), homeBtn);
    }

    public void setPage(ContentPanel panel, Node navButton) {
        if (currentPage instanceof Home && currentPage.isDownloadingOrPlaying()) {
            return;
        }
        if (activeLink != null)
            activeLink.getStyleClass().remove("active");
        activeLink = navButton;
        activeLink.getStyleClass().add("active");

        this.navContent.getChildren().clear();
        if (panel != null) {
            this.navContent.getChildren().add(panel.getLayout());
            currentPage = panel;
            if (panel.getStylesheetPath() != null) {
                this.panelManager.getStage().getScene().getStylesheets().clear();
                this.panelManager.getStage().getScene().getStylesheets().addAll(
                        this.getStylesheetPath(),
                        "css/content/content.css",
                        panel.getStylesheetPath()
                );
                PanelManager.setBackground(this.panelManager.getStage().getScene(), panel);
            }
            panel.init(this.panelManager, this);
            panel.onShow();
        }
    }
}
