package io.github.paulem.launchermc;

import javafx.application.Application;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("javafx.application.Application");
        } catch (ClassNotFoundException e) {
            // Mauvaise version de java
            JOptionPane.showMessageDialog(
                    null,
                    "Votre version de Java ne semble pas être la bonne !",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        Application.launch(Launcher.class, args);
    }
}
