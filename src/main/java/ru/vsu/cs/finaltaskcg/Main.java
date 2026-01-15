package ru.vsu.cs.finaltaskcg;

import ru.vsu.cs.finaltaskcg.ui.ApplicationController;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ApplicationController controller = new ApplicationController();
            controller.show();
        });
    }
}