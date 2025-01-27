package ovh.paulem.jinnclient.ui.panels;

import fr.theshark34.openlauncherlib.util.Saver;
import org.jetbrains.annotations.Nullable;
import ovh.paulem.jinnclient.Launcher;
import fr.flowarg.flowlogger.ILogger;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import ovh.paulem.jinnclient.ui.panels.pages.SideBar;

public abstract class Panel {
    protected final Saver saver = Launcher.getInstance().getSaver();
    protected final ILogger logger;
    protected final GridPane layout = new GridPane();
    protected PanelManager panelManager;

    public Panel() {
        this.logger = Launcher.getInstance().getLogger();
    }

    public void init(PanelManager panelManager, @Nullable SideBar sideBar) {
        this.panelManager = panelManager;
        setCanTakeAllSize(this.layout);
    }

    public void setCanTakeAllSize(Node node) {
        GridPane.setHgrow(node, Priority.ALWAYS);
        GridPane.setVgrow(node, Priority.ALWAYS);
    }

    public void setCanTakeAllWidth(Node... nodes) {
        for (Node n : nodes) {
            GridPane.setHgrow(n, Priority.ALWAYS);
        }
    }

    public GridPane getLayout() {
        return layout;
    }

    public PanelManager getPanelManager() {
        return panelManager;
    }

    public void onShow() {
    }

    public abstract String getName();

    public String getStylesheetPath() {
        return null;
    }

    public void setLeft(Node node) {
        GridPane.setHalignment(node, HPos.LEFT);
    }

    public void setRight(Node node) {
        GridPane.setHalignment(node, HPos.RIGHT);
    }

    public void setTop(Node node) {
        GridPane.setValignment(node, VPos.TOP);
    }

    public void setBottom(Node node) {
        GridPane.setValignment(node, VPos.BOTTOM);
    }

    public void setBaseLine(Node node) {
        GridPane.setValignment(node, VPos.BASELINE);
    }

    public void setCenterH(Node node) {
        GridPane.setHalignment(node, HPos.CENTER);
    }

    public void setCenterV(Node node) {
        GridPane.setValignment(node, VPos.CENTER);
    }
}
