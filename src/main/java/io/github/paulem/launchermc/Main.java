package io.github.paulem.launchermc;

import javafx.application.Application;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        new Thread(() -> {
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
        }).start();
    }
}
