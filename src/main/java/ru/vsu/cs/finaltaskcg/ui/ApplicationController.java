package ru.vsu.cs.finaltaskcg.ui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.File;
//import ru.vsu.cs.finaltaskcg.scene.SceneManager;
//import model.Model;
//import objreader.ObjReader;

public class ApplicationController {
    private MainWindow mainWindow;
   // private SceneManager sceneManager;

    public ApplicationController() {
      //  sceneManager = new SceneManager();
        mainWindow = new MainWindow();
        setupListeners();
    }

    private void setupListeners() {
        ModelListPanel modelListPanel = mainWindow.getModelListPanel();
        PropertiesPanel propertiesPanel = mainWindow.getPropertiesPanel();

        modelListPanel.getAddButton().addActionListener(e -> loadModel());

        modelListPanel.getRemoveButton().addActionListener(e -> {
            int[] indices = modelListPanel.getSelectedIndices();
            if (indices.length > 0) {
                int confirm = JOptionPane.showConfirmDialog(mainWindow,
                        "Удалить выбранные модели?", "Подтверждение",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    modelListPanel.removeSelectedModels();
                }
            }
        });

        modelListPanel.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = modelListPanel.getModelList().getSelectedIndex();
                if (selectedIndex != -1) {
                    String modelName = modelListPanel.getModelList().getModel()
                            .getElementAt(selectedIndex);
                    propertiesPanel.updateProperties(modelName, 1500, 3000);
                }
            }
        });

        setupMenuListeners();
    }

    private void setupMenuListeners() {
        JMenuBar menuBar = mainWindow.getJMenuBar();
        if (menuBar == null) return;

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if ("Файл".equals(menu.getText())) {
                for (int j = 0; j < menu.getItemCount(); j++) {
                    JMenuItem item = menu.getItem(j);
                    if (item != null) {
                        if ("Открыть модель".equals(item.getText())) {
                            item.addActionListener(e -> loadModel());
                        }
                        if ("Сохранить модель".equals(item.getText())) {
                            item.addActionListener(e -> saveModel());
                        }
                    }
                }
            }
        }
    }

    private void loadModel() {
        File file = FileLoader.showOpenDialog(mainWindow);
        if (file != null) {
            try {
               // Model model = ObjReader.read(file.getAbsolutePath());
                //sceneManager.addModel(model);
                mainWindow.getModelListPanel().addModel(file.getName());

                //JOptionPane.showMessageDialog(mainWindow,
                        //"Модель успешно загружена!\nВершин: " + model.vertices.size() +
                                //"\nПолигонов: " + //model.polygons.size(),
                        //"Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                ErrorHandler.showErrorDialog(mainWindow, "Ошибка загрузки",
                        "Не удалось загрузить модель: " + file.getName(), e);
            }
        }
    }

    private void saveModel() {
        File file = FileLoader.showSaveDialog(mainWindow);
        if (file != null) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Функция сохранения будет реализована после получения ObjWriter",
                    "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void show() {
        mainWindow.setVisible(true);
    }
}