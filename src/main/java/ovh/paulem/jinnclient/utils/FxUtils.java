package ovh.paulem.jinnclient.utils;

import javafx.scene.control.Labeled;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class FxUtils {
    public static double getTextSize(Labeled fontHolder, String text) {
        Text t = new Text(text);
        t.setFont(fontHolder.getFont());
        StackPane pane = new StackPane(t);
        pane.layout();
        return t.getLayoutBounds().getWidth();
    }
}
