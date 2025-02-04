package ovh.paulem.jinnclient.ui.panels;

import ovh.paulem.jinnclient.Launcher;
import ovh.paulem.jinnclient.ui.panels.pages.content.ContentPanel;
import ovh.paulem.jinnclient.ui.panels.pages.content.Settings;
import ovh.paulem.jinnclient.utils.Background;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class PanelManager {
    private final Launcher launcher;
    private final Stage stage;
    private final GridPane contentPane = new GridPane();

    public PanelManager(Launcher launcher, Stage stage) {
        this.launcher = launcher;
        this.stage = stage;

        this.stage.resizableProperty().setValue(Boolean.FALSE);
    }

    public void init() {
        this.stage.setTitle("Launcher");
        this.stage.setFullScreen(false);
        this.stage.setMinWidth(854);
        this.stage.setMinHeight(480);
        this.stage.setWidth(1280);
        this.stage.setHeight(720);
        this.stage.centerOnScreen();
        this.stage.getIcons().add(new Image("images/icon.png"));

        GridPane layout = new GridPane();
        layout.getStyleClass().add("main-layout");

        Scene scene = new Scene(layout);
        this.stage.setScene(scene);

        layout.add(this.contentPane, 0, 1);
        GridPane.setVgrow(this.contentPane, Priority.ALWAYS);
        GridPane.setHgrow(this.contentPane, Priority.ALWAYS);

        this.stage.show();
    }

    public void showPanel(Panel panel) {
        this.contentPane.getChildren().clear();
        this.contentPane.getChildren().add(panel.getLayout());
        if (panel.getStylesheetPath() != null) {
            this.stage.getScene().getStylesheets().clear();
            this.stage.getScene().getStylesheets().add("css/main.css");
            this.stage.getScene().getStylesheets().add(panel.getStylesheetPath());
            setBackground(this.stage.getScene(), null);
        }
        panel.init(this, null);
        panel.onShow();
    }

    public static void setBackground(Scene scene, @Nullable Panel actualPanel) {
        if(actualPanel instanceof Settings) return;

        Background[] backgroundsValues = Background.values();
        int index = ThreadLocalRandom.current().nextInt(backgroundsValues.length);
        Background background = backgroundsValues[index];

        String sessionBackground = "css/backgrounds/" + background.getName() + ".css";
        scene.getStylesheets().add(sessionBackground);
    }

    public Stage getStage() {
        return stage;
    }

    public Launcher getLauncher() {
        return launcher;
    }
}
