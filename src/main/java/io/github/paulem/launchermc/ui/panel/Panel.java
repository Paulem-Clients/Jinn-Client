package io.github.paulem.launchermc.ui.panel;

import io.github.paulem.launchermc.Launcher;
import io.github.paulem.launchermc.ui.PanelManager;
import fr.flowarg.flowlogger.ILogger;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public abstract class Panel {
    protected final ILogger logger;
    protected final GridPane layout = new GridPane();
    protected PanelManager panelManager;

    public Panel() {
        this.logger = Launcher.getInstance().getLogger();
    }

    public void init(PanelManager panelManager) {
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
