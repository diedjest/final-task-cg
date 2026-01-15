package ru.vsu.cs.finaltaskcg.ui;

import javax.swing.*;
import java.io.File;

public class FileLoader {
    public static File showOpenDialog(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Открыть 3D модель");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "3D Model Files (*.obj)", "obj"));

        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static File showSaveDialog(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить модель");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "OBJ Files (*.obj)", "obj"));

        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".obj")) {
                file = new File(file.getAbsolutePath() + ".obj");
            }
            return file;
        }
        return null;
    }
}