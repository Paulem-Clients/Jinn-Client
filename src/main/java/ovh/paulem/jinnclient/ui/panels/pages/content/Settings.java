package ovh.paulem.jinnclient.ui.panels.pages.content;

import com.sun.management.OperatingSystemMXBean;
import org.kordamp.ikonli.fluentui.FluentUiFilledAL;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;
import org.kordamp.ikonli.javafx.FontIcon;
import ovh.paulem.jinnclient.ui.panels.PanelManager;
import ovh.paulem.jinnclient.ui.panels.pages.SideBar;
import ovh.paulem.jinnclient.utils.Constants;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.lang.management.ManagementFactory;

public class Settings extends ContentPanel {
    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/settings.css";
    }

    @Override
    public void init(PanelManager panelManager, SideBar sideBar) {
        super.init(panelManager, sideBar);

        // Titre
        Label title = new Label("Paramètres");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("settings-title");
        setLeft(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.LEFT);
        content.add(title, 0, 0);

        // RAM
        Label ramLabel = new Label("Mémoire max");
        ramLabel.getStyleClass().add("settings-labels");
        setLeft(ramLabel);
        setCanTakeAllSize(ramLabel);
        setTop(ramLabel);
        ramLabel.setTextAlignment(TextAlignment.LEFT);
        content.add(ramLabel, 0, 1);

        // RAM Chooser

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("ram-selector");

        long totalMemorySize = ((OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean()).getTotalMemorySize();
        for(int i = 512; i <= Math.ceil(totalMemorySize / Math.pow(1024, 2)); i+=512) {
            comboBox.getItems().add(i/1024.0 + " GB");
        }

        int defaultRamAmount = 1024;
        String maxRam = saver.get(Constants.CONFIG_MAXRAM);
        try {
            if (maxRam != null) {
                defaultRamAmount = Integer.parseInt(maxRam);
            } else {
                throw new NumberFormatException("maxRam doesn't exist !");
            }
        } catch (NumberFormatException error) {
            saver.set(Constants.CONFIG_MAXRAM, String.valueOf(defaultRamAmount));
            saver.save();
        }

        if (comboBox.getItems().contains(defaultRamAmount/1024.0+" GB")) {
            comboBox.setValue(defaultRamAmount / 1024.0 + " GB");
        } else {
            comboBox.setValue("1.0 GB");
        }

        setLeft(comboBox);
        setCanTakeAllSize(comboBox);
        setTop(comboBox);
        content.add(comboBox, 1, 1);

        /*
         * Save Button
         */
        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("save-btn");
        FontIcon iconView = FontIcon.of(FluentUiRegularMZ.SAVE_24);
        iconView.getStyleClass().add("save-icon");
        FontIcon iconCheck = FontIcon.of(FluentUiFilledAL.CHECKMARK_24);
        iconCheck.getStyleClass().add("save-icon");
        saveBtn.setGraphic(iconView);
        setCanTakeAllSize(saveBtn);
        setBottom(saveBtn);
        setCenterH(saveBtn);

        saveBtn.setOnMouseClicked(e -> {
            double comboBoxRamValue = Double.parseDouble(comboBox.getValue().replace(" GB", ""));
            comboBoxRamValue *= 1024;
            saver.set(Constants.CONFIG_MAXRAM, String.valueOf((int) comboBoxRamValue));
            
            saveBtn.setGraphic(iconCheck);
            saveBtn.setText("Enregistré");
            
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
                saveBtn.setGraphic(iconView);
                saveBtn.setText("Enregistrer");
            }));
            timeline.play();
        });
        content.add(saveBtn, 0, 2, 1, 2);

        this.showPlayButton();
    }
}
