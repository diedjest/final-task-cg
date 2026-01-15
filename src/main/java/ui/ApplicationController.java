package ui;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objreader.ObjReaderException;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.transformations.Axis;
import com.cgvsu.transformations.TransformManager;
import scene.SceneManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class ApplicationController {
    private MainWindow mainWindow;
    private SceneManager sceneManager;
    private TransformManager transformManager;

    public ApplicationController() {
        mainWindow = new MainWindow();
        sceneManager = new SceneManager();
        transformManager = new TransformManager();
        setupListeners();
    }

    private void setupListeners() {
        ModelListPanel modelListPanel = mainWindow.getModelListPanel();

        modelListPanel.getAddButton().addActionListener((ActionEvent e) -> {
            loadModelFromFile();
        });

        modelListPanel.getRemoveButton().addActionListener(e -> {
            int[] selectedIndices = modelListPanel.getSelectedIndices();
            for (int i = selectedIndices.length - 1; i >= 0; i--) {
                int index = selectedIndices[i];
                Model model = sceneManager.getModels().get(index);
                sceneManager.removeModel(model);
            }
            modelListPanel.removeSelectedModels();
            updateRenderPanel();
        });

        modelListPanel.getSelectAllButton().addActionListener(e -> {
            modelListPanel.getModelList().setSelectionInterval(0,
                    modelListPanel.getModelList().getModel().getSize() - 1);
            updateSelectionFromList();
        });

        modelListPanel.getClearSelectionButton().addActionListener(e -> {
            modelListPanel.getModelList().clearSelection();
            sceneManager.clearSelection();
            updatePropertiesPanel(null);
        });

        modelListPanel.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectionFromList();
            }
        });

        mainWindow.getPropertiesPanel().getColorButton().addActionListener(e -> {
            Color newColor = mainWindow.getPropertiesPanel().getCurrentColor();
            mainWindow.getRenderPanel().setModelColor(newColor);
        });

        mainWindow.getMoveButton().addActionListener(e -> {
            showTransformDialog("Перемещение", "dx", "dy", "dz",
                    (dx, dy, dz) -> {
                        Model activeModel = sceneManager.getActiveModel();
                        if (activeModel != null) {
                            transformManager.translate(activeModel, dx, dy, dz);
                            updateRenderPanel();
                            mainWindow.showInfoDialog("Модель перемещена");
                        }
                    });
        });

        mainWindow.getRotateButton().addActionListener(e -> {
            showTransformDialog("Вращение", "Угол X", "Угол Y", "Угол Z",
                    (x, y, z) -> {
                        Model activeModel = sceneManager.getActiveModel();
                        if (activeModel != null) {
                            if (Math.abs(x) > 0.01f) {
                                transformManager.rotate(activeModel, Axis.X, x);
                            }
                            if (Math.abs(y) > 0.01f) {
                                transformManager.rotate(activeModel, Axis.Y, y);
                            }
                            if (Math.abs(z) > 0.01f) {
                                transformManager.rotate(activeModel, Axis.Z, z);
                            }
                            updateRenderPanel();
                            mainWindow.showInfoDialog("Модель повернута");
                        }
                    });
        });

        mainWindow.getScaleButton().addActionListener(e -> {
            showTransformDialog("Масштабирование", "Масштаб X", "Масштаб Y", "Масштаб Z",
                    (sx, sy, sz) -> {
                        Model activeModel = sceneManager.getActiveModel();
                        if (activeModel != null) {
                            transformManager.scale(activeModel, sx, sy, sz);
                            updateRenderPanel();
                            mainWindow.showInfoDialog("Модель масштабирована");
                        }
                    });
        });

        mainWindow.getDeleteButton().addActionListener(e -> {
            showDeleteDialog();
        });

        mainWindow.getOpenItem().addActionListener(e -> loadModelFromFile());

        mainWindow.getSaveItem().addActionListener(e -> saveModelToFile());

        mainWindow.getLightTheme().addActionListener(e -> mainWindow.applyTheme("light"));

        mainWindow.getDarkTheme().addActionListener(e -> mainWindow.applyTheme("dark"));

        mainWindow.getAboutItem().addActionListener(e -> {
            JOptionPane.showMessageDialog(mainWindow,
                    "3D Model Viewer Pro\nВерсия 1.0\nРазработано командой",
                    "О программе",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void showDeleteDialog() {
        Model activeModel = sceneManager.getActiveModel();
        if (activeModel == null) {
            mainWindow.showErrorDialog("Выберите модель для удаления");
            return;
        }

        String[] options = {"Удалить вершину", "Удалить полигон", "Выбрать несколько", "Отмена"};
        int choice = JOptionPane.showOptionDialog(mainWindow,
                "Выберите тип удаления:",
                "Удаление элементов",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                deleteVertex(activeModel);
                break;
            case 1:
                deletePolygon(activeModel);
                break;
            case 2:
                showMultiSelectDialog(activeModel);
                break;
        }
    }

    private void deleteVertex(Model model) {
        String input = JOptionPane.showInputDialog(mainWindow,
                "Введите индекс вершины (0-" + (model.getVertices().size() - 1) + "):");
        try {
            int index = Integer.parseInt(input);
            if (index >= 0 && index < model.getVertices().size()) {
                com.cgvsu.editor.VertexRemover.removeVertexWithDependencies(model, index);
                updatePropertiesPanel(model);
                updateRenderPanel();
                mainWindow.showInfoDialog("Вершина удалена");
            } else {
                mainWindow.showErrorDialog("Некорректный индекс вершины");
            }
        } catch (Exception ex) {
            mainWindow.showErrorDialog("Некорректный ввод");
        }
    }

    private void deletePolygon(Model model) {
        String input = JOptionPane.showInputDialog(mainWindow,
                "Введите индекс полигона (0-" + (model.getPolygons().size() - 1) + "):");
        try {
            int index = Integer.parseInt(input);
            if (index >= 0 && index < model.getPolygons().size()) {
                com.cgvsu.editor.PolygonRemover.removePolygonWithCleanup(model, index);
                updatePropertiesPanel(model);
                updateRenderPanel();
                mainWindow.showInfoDialog("Полигон удален");
            } else {
                mainWindow.showErrorDialog("Некорректный индекс полигона");
            }
        } catch (Exception ex) {
            mainWindow.showErrorDialog("Некорректный ввод");
        }
    }

    private void showMultiSelectDialog(Model model) {
        JPanel panel = new JPanel(new GridLayout(2, 1));

        JButton selectVerticesButton = new JButton("Выбрать вершины для удаления");
        JButton selectPolygonsButton = new JButton("Выбрать полигоны для удаления");

        panel.add(selectVerticesButton);
        panel.add(selectPolygonsButton);

        JOptionPane.showMessageDialog(mainWindow, panel, "Множественное удаление",
                JOptionPane.PLAIN_MESSAGE);

        selectVerticesButton.addActionListener(e -> {
            showVertexSelectionDialog(model);
        });

        selectPolygonsButton.addActionListener(e -> {
            showPolygonSelectionDialog(model);
        });
    }

    private void showVertexSelectionDialog(Model model) {
        JDialog dialog = new JDialog(mainWindow, "Выбор вершин для удаления", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(mainWindow);

        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> vertexList = new JList<>(listModel);

        for (int i = 0; i < model.getVertices().size(); i++) {
            listModel.addElement("Вершина " + i);
        }

        vertexList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(vertexList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton selectAllButton = new JButton("Выбрать все");
        JButton clearButton = new JButton("Сбросить");
        JButton deleteButton = new JButton("Удалить выбранные");
        JButton cancelButton = new JButton("Отмена");

        buttonPanel.add(selectAllButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        selectAllButton.addActionListener(e -> {
            vertexList.setSelectionInterval(0, model.getVertices().size() - 1);
        });

        clearButton.addActionListener(e -> {
            vertexList.clearSelection();
        });

        deleteButton.addActionListener(e -> {
            int[] selectedIndices = vertexList.getSelectedIndices();
            if (selectedIndices.length == 0) {
                mainWindow.showErrorDialog("Выберите хотя бы одну вершину");
                return;
            }

            Set<Integer> vertexIndices = new HashSet<>();
            for (int index : selectedIndices) {
                vertexIndices.add(index);
            }

            com.cgvsu.editor.ModelEditor.removeSelectedVertices(model, vertexIndices);
            updatePropertiesPanel(model);
            updateRenderPanel();
            dialog.dispose();
            mainWindow.showInfoDialog("Удалено вершин: " + selectedIndices.length);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showPolygonSelectionDialog(Model model) {
        JDialog dialog = new JDialog(mainWindow, "Выбор полигонов для удаления", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(mainWindow);

        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> polygonList = new JList<>(listModel);

        for (int i = 0; i < model.getPolygons().size(); i++) {
            listModel.addElement("Полигон " + i + " (вершин: " + model.getPolygons().get(i).getVertexIndices().size() + ")");
        }

        polygonList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(polygonList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton selectAllButton = new JButton("Выбрать все");
        JButton clearButton = new JButton("Сбросить");
        JButton deleteButton = new JButton("Удалить выбранные");
        JButton cancelButton = new JButton("Отмена");

        buttonPanel.add(selectAllButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        selectAllButton.addActionListener(e -> {
            polygonList.setSelectionInterval(0, model.getPolygons().size() - 1);
        });

        clearButton.addActionListener(e -> {
            polygonList.clearSelection();
        });

        deleteButton.addActionListener(e -> {
            int[] selectedIndices = polygonList.getSelectedIndices();
            if (selectedIndices.length == 0) {
                mainWindow.showErrorDialog("Выберите хотя бы один полигон");
                return;
            }

            Set<Integer> polygonIndices = new HashSet<>();
            for (int index : selectedIndices) {
                polygonIndices.add(index);
            }

            com.cgvsu.editor.ModelEditor.removeSelectedPolygons(model, polygonIndices);
            updatePropertiesPanel(model);
            updateRenderPanel();
            dialog.dispose();
            mainWindow.showInfoDialog("Удалено полигонов: " + selectedIndices.length);
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showTransformDialog(String title, String label1, String label2, String label3,
                                     TransformCallback callback) {
        Model activeModel = sceneManager.getActiveModel();
        if (activeModel == null) {
            mainWindow.showErrorDialog("Выберите модель для преобразования");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField field1 = new JTextField("0");
        JTextField field2 = new JTextField("0");
        JTextField field3 = new JTextField("0");

        panel.add(new JLabel(label1 + ":"));
        panel.add(field1);
        panel.add(new JLabel(label2 + ":"));
        panel.add(field2);
        panel.add(new JLabel(label3 + ":"));
        panel.add(field3);

        int result = JOptionPane.showConfirmDialog(mainWindow, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                float val1 = Float.parseFloat(field1.getText());
                float val2 = Float.parseFloat(field2.getText());
                float val3 = Float.parseFloat(field3.getText());
                callback.onTransform(val1, val2, val3);
            } catch (NumberFormatException ex) {
                mainWindow.showErrorDialog("Введите корректные числовые значения");
            }
        }
    }

    private void updateSelectionFromList() {
        int[] selectedIndices = mainWindow.getModelListPanel().getSelectedIndices();
        sceneManager.clearSelection();

        if (selectedIndices.length > 0) {
            for (int index : selectedIndices) {
                Model model = sceneManager.getModels().get(index);
                sceneManager.addToSelection(model);
            }

            Model firstSelected = sceneManager.getModels().get(selectedIndices[0]);
            sceneManager.setActiveModel(firstSelected);

            updatePropertiesPanel(firstSelected);
        } else {
            updatePropertiesPanel(null);
        }
    }

    private void updatePropertiesPanel(Model model) {
        if (model != null) {
            mainWindow.getPropertiesPanel().updateProperties(
                    model.getName(),
                    model.getVertices().size(),
                    model.getPolygons().size()
            );
        } else {
            mainWindow.getPropertiesPanel().updateProperties("", 0, 0);
        }
    }

    private void updateRenderPanel() {
        mainWindow.getRenderPanel().setModels(sceneManager.getModels());
        mainWindow.getRenderPanel().repaint();
    }

    private void loadModelFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите 3D модель (.obj)");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "OBJ Files", "obj"));

        int result = fileChooser.showOpenDialog(mainWindow);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                String fileContent = Files.readString(selectedFile.toPath());

                Model model = ObjReader.read(fileContent);
                model.setName(selectedFile.getName());

                sceneManager.addModel(model);

                mainWindow.getModelListPanel().addModel(model.getName());

                updatePropertiesPanel(model);
                updateRenderPanel();

                mainWindow.showInfoDialog("Модель успешно загружена: " + model.getName());

            } catch (ObjReaderException e) {
                ErrorHandler.showErrorDialog(mainWindow,
                        "Ошибка чтения файла",
                        "Не удалось прочитать файл: " + selectedFile.getName() +
                                "\nОшибка: " + e.getMessage(),
                        e);
            } catch (IOException e) {
                ErrorHandler.showErrorDialog(mainWindow,
                        "Ошибка ввода-вывода",
                        "Не удалось открыть файл: " + selectedFile.getName(),
                        e);
            }
        }
    }

    private void saveModelToFile() {
        Model activeModel = sceneManager.getActiveModel();
        if (activeModel == null) {
            ErrorHandler.showErrorDialog(mainWindow, "Ошибка",
                    "Нет активной модели для сохранения", null);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить модель как...");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "OBJ Files", "obj"));

        int result = fileChooser.showSaveDialog(mainWindow);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".obj")) {
                file = new File(file.getAbsolutePath() + ".obj");
            }

            try {
                ObjWriter.write(activeModel, file.getAbsolutePath());

                mainWindow.showInfoDialog("Модель успешно сохранена:\n" + file.getAbsolutePath());

            } catch (IOException e) {
                ErrorHandler.showErrorDialog(mainWindow, "Ошибка сохранения",
                        "Не удалось сохранить файл: " + e.getMessage(), e);
            } catch (Exception e) {
                ErrorHandler.showErrorDialog(mainWindow, "Ошибка",
                        "Произошла ошибка при сохранении: " + e.getMessage(), e);
            }
        }
    }

    public void show() {
        mainWindow.setVisible(true);
    }

    private interface TransformCallback {
        void onTransform(float val1, float val2, float val3);
    }
}