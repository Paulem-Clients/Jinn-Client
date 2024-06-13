package io.github.paulem.launchermc.ui.panels.pages.content;

import io.github.paulem.launchermc.ui.panel.Panel;
import io.github.paulem.launchermc.utils.DirectFadeTransition;
import javafx.animation.FadeTransition;

public abstract class ContentPanel extends Panel {
    @Override
    public void onShow() {
        FadeTransition transition = DirectFadeTransition.getFadeTransition(this);
        transition.setAutoReverse(false);
        transition.play();
    }
}
