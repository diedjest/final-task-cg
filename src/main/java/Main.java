import ui.ApplicationController;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ApplicationController controller = new ApplicationController();
            controller.show();
        });
    }
}