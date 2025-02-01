package ovh.paulem.jinnclient.ui.assets;

import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.kordamp.ikonli.ionicons4.Ionicons4IOS;
import org.kordamp.ikonli.javafx.FontIcon;
import ovh.paulem.jinnclient.Launcher;
import ovh.paulem.jinnclient.ui.panels.Panel;
import ovh.paulem.jinnclient.ui.panels.pages.Login;
import ovh.paulem.jinnclient.ui.panels.pages.content.ContentPanel;
import ovh.paulem.jinnclient.ui.panels.pages.content.Home;
import ovh.paulem.jinnclient.utils.FxUtils;

public class UserPane {
    protected final Saver saver = Launcher.getInstance().getSaver();

    private final Panel panel;
    private final GridPane parent;
    private final ContentPanel currentPage;

    public UserPane(Panel panel, GridPane parent, ContentPanel currentPage) {
        this.panel = panel;
        this.parent = parent;
        this.currentPage = currentPage;
    }

    public void show() {
        // Pseudo + avatar
        GridPane userPane = new GridPane();
        //panel.setCanTakeAllWidth(userPane);
        //userPane.set(new Insets(20, 20, 20, 20));
        userPane.setMaxHeight(75);
        userPane.setMinWidth(80);
        panel.setRight(userPane);
        userPane.getStyleClass().add("user-pane");

        String avatarUrl = "https://minotar.net/avatar/" + (
                saver.get("offline-username") != null ?
                        "MHF_Steve.png" :
                        Launcher.getInstance().getAuthInfos().getUuid() + ".png"
        );
        ImageView avatarView = new ImageView();
        Image avatarImg = new Image(avatarUrl);
        avatarView.setImage(avatarImg);
        avatarView.setPreserveRatio(true);
        avatarView.setFitHeight(40d);
        panel.setCenterV(avatarView);
        panel.setCanTakeAllSize(avatarView);
        panel.setLeft(avatarView);
        avatarView.setTranslateX(15d);
        userPane.getChildren().add(avatarView);

        Label usernameLabel = new Label(Launcher.getInstance().getAuthInfos().getUsername());
        usernameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        panel.setCanTakeAllSize(usernameLabel);
        panel.setCenterV(usernameLabel);
        panel.setLeft(usernameLabel);
        usernameLabel.getStyleClass().add("username-label");
        usernameLabel.setTranslateX(75d);
        panel.setCanTakeAllWidth(usernameLabel);
        userPane.getChildren().add(usernameLabel);

        Button logoutBtn = new Button();
        FontIcon logoutIcon = FontIcon.of(Ionicons4IOS.LOG_OUT);
        logoutIcon.getStyleClass().add("logout-icon");
        panel.setCanTakeAllSize(logoutBtn);
        panel.setCenterV(logoutBtn);
        panel.setRight(logoutBtn);
        logoutBtn.getStyleClass().add("logout-btn");
        logoutBtn.setGraphic(logoutIcon);
        logoutBtn.setOnMouseClicked(e -> {
            if (currentPage instanceof Home home && home.isDownloadingOrPlaying()) {
                return;
            }
            saver.remove("accessToken");
            saver.remove("clientToken");
            saver.remove("offline-username");
            saver.remove("msAccessToken");
            saver.remove("msRefreshToken");
            saver.save();
            Launcher.getInstance().setAuthInfos(null);
            panel.getPanelManager().showPanel(new Login());
        });
        userPane.getChildren().add(logoutBtn);

        double width = FxUtils.getTextSize(usernameLabel, usernameLabel.getText());
        System.out.println(width);
        userPane.setMaxWidth(40d + 20d + width + (30d*2));
        parent.getChildren().add(userPane);
    }
}
