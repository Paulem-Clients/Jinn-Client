package ovh.paulem.jinnclient.utils;

import javafx.concurrent.Task;
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

    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException ignored) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }
}
