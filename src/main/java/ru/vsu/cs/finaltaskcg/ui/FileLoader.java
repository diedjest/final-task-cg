package ru.vsu.cs.finaltaskcg.ui;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class FileLoader {
    public static File showOpenDialog(Window owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть 3D модель");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ Files", "*.obj"));
        return fileChooser.showOpenDialog(owner);
    }

    public static File showSaveDialog(Window owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить модель");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ Files", "*.obj"));
        return fileChooser.showSaveDialog(owner);
    }
}