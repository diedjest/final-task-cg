package ru.vsu.cs.finaltaskcg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Simple3DViewer extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Simple3DViewer.class.getResource("/ru.vsu.cs.finaltaskcg.fxml/gui.fxml")
        );
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1400, 900);
        stage.setTitle("3D Model Viewer Pro");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}