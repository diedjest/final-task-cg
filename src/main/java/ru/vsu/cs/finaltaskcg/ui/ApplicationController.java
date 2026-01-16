package ru.vsu.cs.finaltaskcg.ui;

import javafx.application.Platform;
import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;
import ru.vsu.cs.finaltaskcg.objreader.ObjReader;
import ru.vsu.cs.finaltaskcg.objwriter.ObjWriter;
import ru.vsu.cs.finaltaskcg.normals.NormalCalculator;
import ru.vsu.cs.finaltaskcg.render_engine.RenderEngine;
import ru.vsu.cs.finaltaskcg.render_engine.Camera;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ApplicationController {

    @FXML private BorderPane rootPane;
    @FXML private Canvas canvas;
    @FXML private VBox leftPanel;
    @FXML private VBox rightPanel;
    @FXML private ListView<String> modelListView;
    @FXML private TextField modelNameField;
    @FXML private Label vertexCountLabel;
    @FXML private Label polygonCountLabel;
    @FXML private ColorPicker modelColorPicker;
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem lightThemeMenuItem;
    @FXML private MenuItem darkThemeMenuItem;
    @FXML private MenuItem aboutMenuItem;
    @FXML private Button moveButton;
    @FXML private Button rotateButton;
    @FXML private Button scaleButton;
    @FXML private Button deleteButton;
    @FXML private Button addModelButton;
    @FXML private Button removeModelButton;
    @FXML private Button selectAllButton;
    @FXML private Button clearSelectionButton;
    @FXML private Button importButton;
    @FXML private Button exportButton;
    @FXML private Button resetButton;
    @FXML private Button quickDeleteButton;  // ДОБАВЛЕНО
    @FXML private VBox canvasContainer;
    @FXML private Label statusLabel;
    @FXML private CheckBox wireframeCheckBox;
    @FXML private CheckBox fillCheckBox;
    @FXML private CheckBox lightingCheckBox;
    @FXML private CheckBox zbufferCheckBox;
    @FXML private Button rotateLeftButton;
    @FXML private Button rotateRightButton;
    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;
    @FXML private Button resetCameraButton;
    @FXML private CheckMenuItem textureMenuItem;
    @FXML private MenuItem openTextureMenuItem;
    @FXML private CheckBox triangulationCheckBox;    // ДОБАВЛЕНО
    @FXML private CheckBox smoothShadingCheckBox;    // ДОБАВЛЕНО
    @FXML private CheckBox specularCheckBox;         // ДОБАВЛЕНО

    private ObservableList<String> modelListItems;
    private ArrayList<Model> models;
    private Model activeModel;
    private Camera camera;
    private AnimationTimer timer;
    private boolean isDarkTheme = false;
    private boolean isRendering = false;

    private boolean isMousePressed = false;
    private boolean isMiddleMousePressed = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private final double MOUSE_SENSITIVITY = 0.2;
    private final double PAN_SENSITIVITY = 0.05;
    private final double ZOOM_SENSITIVITY = 0.05;
    private final double ROTATION_SENSITIVITY = 0.1;

    @FXML
    private void initialize() {
        models = new ArrayList<>();
        modelListItems = FXCollections.observableArrayList();
        modelListView.setItems(modelListItems);
        modelColorPicker.setValue(Color.rgb(100, 149, 237));

        setupCamera();
        setupModernUI();
        applyLightTheme();
        setupListeners();
        setupRenderLoop();
        setupMouseHandlers();

        statusLabel.setText("Готов к работе");

        RenderEngine.setAutoTriangulate(true);
        RenderEngine.setSmoothShading(false);
        RenderEngine.setSpecularLighting(false);
        RenderEngine.setLightingParameters(0.3, 0.7, 0.3, 16);
    }

    private void setupCamera() {
        camera = new Camera(
                new Vector3(0, 0, 1000),
                new Vector3(0, 0, 0),
                1.0F,
                1.0,
                0.1F,
                10000.0f
        );
    }

    private void setupMouseHandlers() {
        canvas.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                isMousePressed = true;
                lastMouseX = event.getX();
                lastMouseY = event.getY();
                event.consume();
            } else if (event.isMiddleButtonDown()) {
                isMiddleMousePressed = true;
                lastMouseX = event.getX();
                lastMouseY = event.getY();
                event.consume();
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (isMousePressed && !isMiddleMousePressed) {
                double deltaX = event.getX() - lastMouseX;
                double deltaY = event.getY() - lastMouseY;

                camera.pan(deltaX, deltaY, PAN_SENSITIVITY);

                lastMouseX = event.getX();
                lastMouseY = event.getY();
                event.consume();
                isRendering = true;
            } else if (isMiddleMousePressed) {
                double deltaX = event.getX() - lastMouseX;
                double deltaY = event.getY() - lastMouseY;

                camera.rotateAroundTarget(deltaX, deltaY, ROTATION_SENSITIVITY);

                lastMouseX = event.getX();
                lastMouseY = event.getY();
                event.consume();
                isRendering = true;
            }
        });

        canvas.setOnMouseReleased(event -> {
            isMousePressed = false;
            isMiddleMousePressed = false;
        });

        canvas.setOnScroll(event -> {
            camera.zoom((float)event.getDeltaY(), ZOOM_SENSITIVITY);
            event.consume();
            isRendering = true;
        });
    }

    private void setupRenderLoop() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isRendering) {
                    render();
                    isRendering = false;
                }
            }
        };
        timer.start();
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (models.isEmpty()) {
            drawNoModelMessage(gc);
            return;
        }

        camera.setAspectRatio((float) canvas.getWidth() / (float) canvas.getHeight());

        boolean wireframe = wireframeCheckBox.isSelected();
        boolean fill = fillCheckBox.isSelected();
        boolean lighting = lightingCheckBox.isSelected();
        boolean zbuffer = zbufferCheckBox.isSelected();
        boolean texture = textureMenuItem != null && textureMenuItem.isSelected();
        boolean triangulation = triangulationCheckBox != null && triangulationCheckBox.isSelected();
        boolean smoothShading = smoothShadingCheckBox != null && smoothShadingCheckBox.isSelected();
        boolean specular = specularCheckBox != null && specularCheckBox.isSelected();

        RenderEngine.setRenderMode(wireframe, fill, texture, lighting, zbuffer);
        RenderEngine.setAutoTriangulate(triangulation);
        RenderEngine.setSmoothShading(smoothShading);
        RenderEngine.setSpecularLighting(specular);

        if (lighting) {
            RenderEngine.setLightPosition(camera.getPosition());
        }

        for (Model model : models) {
            Color currentColor = (model == activeModel) ? modelColorPicker.getValue() : Color.LIGHTGRAY;
            RenderEngine.setFillColor(currentColor);

            try {
                ru.vsu.cs.finaltaskcg.managers.TransformManager tempTransformManager = new ru.vsu.cs.finaltaskcg.managers.TransformManager();
                tempTransformManager.addModel(model);

                RenderEngine.render(
                        gc,
                        camera,
                        model,
                        tempTransformManager,
                        (int) canvas.getWidth(),
                        (int) canvas.getHeight()
                );
            } catch (Exception e) {
                System.err.println("Ошибка рендеринга модели: " + e.getMessage());
                e.printStackTrace();
            }
        }

        updateStats();
    }

    private void drawNoModelMessage(GraphicsContext gc) {
        gc.setFill(isDarkTheme ? Color.WHITE : Color.BLACK);
        gc.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 24));
        String message = "Загрузите 3D модель";
        double textWidth = gc.getFont().getSize() * message.length() / 2;
        gc.fillText(message, canvas.getWidth() / 2 - textWidth / 2, canvas.getHeight() / 2 - 20);

        gc.setFont(Font.font("Segoe UI", 14));
        message = "Используйте кнопку 'Импорт' или меню Файл → Открыть";
        textWidth = gc.getFont().getSize() * message.length() / 2;
        gc.fillText(message, canvas.getWidth() / 2 - textWidth / 2, canvas.getHeight() / 2 + 20);
    }

    private void updateStats() {
        if (activeModel != null) {
            vertexCountLabel.setText("Вершин: " + activeModel.vertices.size());
            polygonCountLabel.setText("Полигонов: " + activeModel.polygons.size());
        } else if (!models.isEmpty()) {
            vertexCountLabel.setText("Вершин: " + models.get(0).vertices.size());
            polygonCountLabel.setText("Полигонов: " + models.get(0).polygons.size());
        } else {
            vertexCountLabel.setText("Вершин: 0");
            polygonCountLabel.setText("Полигонов: 0");
        }
    }

    private void setupModernUI() {
        Font buttonFont = Font.font("Segoe UI", FontWeight.MEDIUM, 13);
        Font titleFont = Font.font("Segoe UI", FontWeight.BOLD, 14);
        Font labelFont = Font.font("Segoe UI", 13);

        for (Node node : leftPanel.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setFont(buttonFont);
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setPadding(new Insets(10, 15, 10, 15));
            } else if (node instanceof Label) {
                ((Label) node).setFont(titleFont);
            } else if (node instanceof CheckBox) {
                ((CheckBox) node).setFont(labelFont);
            }
        }

        for (Node node : rightPanel.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setFont(buttonFont);
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setPadding(new Insets(10, 15, 10, 15));
            } else if (node instanceof Label) {
                ((Label) node).setFont(labelFont);
            }
        }

        modelListView.setPadding(new Insets(10));
        canvasContainer.setPadding(new Insets(10));

        moveButton.setGraphic(createIcon("M"));
        rotateButton.setGraphic(createIcon("R"));
        scaleButton.setGraphic(createIcon("S"));
        deleteButton.setGraphic(createIcon("D"));
        importButton.setGraphic(createIcon("I"));
        exportButton.setGraphic(createIcon("E"));
        resetButton.setGraphic(createIcon("↺"));
        rotateLeftButton.setGraphic(createIcon("↶"));
        rotateRightButton.setGraphic(createIcon("↷"));
        zoomInButton.setGraphic(createIcon("+"));
        zoomOutButton.setGraphic(createIcon("-"));
        resetCameraButton.setGraphic(createIcon("⟲"));

        if (quickDeleteButton != null) {
            quickDeleteButton.setGraphic(createIcon("X"));
        }

        moveButton.setContentDisplay(ContentDisplay.LEFT);
        rotateButton.setContentDisplay(ContentDisplay.LEFT);
        scaleButton.setContentDisplay(ContentDisplay.LEFT);
        deleteButton.setContentDisplay(ContentDisplay.LEFT);
        importButton.setContentDisplay(ContentDisplay.LEFT);
        exportButton.setContentDisplay(ContentDisplay.LEFT);
        resetButton.setContentDisplay(ContentDisplay.LEFT);
        rotateLeftButton.setContentDisplay(ContentDisplay.LEFT);
        rotateRightButton.setContentDisplay(ContentDisplay.LEFT);
        zoomInButton.setContentDisplay(ContentDisplay.LEFT);
        zoomOutButton.setContentDisplay(ContentDisplay.LEFT);
        resetCameraButton.setContentDisplay(ContentDisplay.LEFT);

        if (quickDeleteButton != null) {
            quickDeleteButton.setContentDisplay(ContentDisplay.LEFT);
        }

        leftPanel.setSpacing(10);
        rightPanel.setSpacing(15);

        canvas.setWidth(800);
        canvas.setHeight(600);
    }

    private Label createIcon(String text) {
        Label icon = new Label(text);
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        icon.setPadding(new Insets(0, 8, 0, 0));
        return icon;
    }

    private void setupListeners() {
        openMenuItem.setOnAction(e -> loadModelFromFile());
        saveMenuItem.setOnAction(e -> saveModelToFile());
        lightThemeMenuItem.setOnAction(e -> applyLightTheme());
        darkThemeMenuItem.setOnAction(e -> applyDarkTheme());
        aboutMenuItem.setOnAction(e -> showAboutDialog());

        if (openTextureMenuItem != null) {
            openTextureMenuItem.setOnAction(e -> loadTexture());
        }

        if (textureMenuItem != null) {
            textureMenuItem.setOnAction(e -> {
                if (textureMenuItem.isSelected() && !RenderEngine.isTextureLoaded()) {
                    showAlert("Текстура не загружена", "Пожалуйста, загрузите текстуру через File -> Load Texture");
                    textureMenuItem.setSelected(false);
                }
            });
        }

        addModelButton.setOnAction(e -> loadModelFromFile());

        removeModelButton.setOnAction(e -> {
            int selectedIndex = modelListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Model model = models.get(selectedIndex);
                models.remove(selectedIndex);
                modelListItems.remove(selectedIndex);
                if (activeModel == model) {
                    activeModel = models.isEmpty() ? null : models.get(0);
                }
                isRendering = !models.isEmpty();
                statusLabel.setText("Модель удалена");
            }
        });

        if (quickDeleteButton != null) {
            quickDeleteButton.setOnAction(e -> showDeleteDialog());
        }

        selectAllButton.setOnAction(e -> {
            modelListView.getSelectionModel().selectAll();
        });

        clearSelectionButton.setOnAction(e -> {
            modelListView.getSelectionModel().clearSelection();
        });

        moveButton.setOnAction(e -> showTransformDialog("Перемещение", "dx", "dy", "dz"));
        rotateButton.setOnAction(e -> showTransformDialog("Вращение", "Угол X", "Угол Y", "Угол Z"));
        scaleButton.setOnAction(e -> showTransformDialog("Масштабирование", "Масштаб X", "Масштаб Y", "Масштаб Z"));
        deleteButton.setOnAction(e -> showDeleteDialog());
        importButton.setOnAction(e -> loadModelFromFile());
        exportButton.setOnAction(e -> saveModelToFile());
        resetButton.setOnAction(e -> resetTransformations());

        rotateLeftButton.setOnAction(e -> {
            camera.rotateAroundTarget(-5, 0, ROTATION_SENSITIVITY);
            isRendering = true;
            statusLabel.setText("Камера повернута влево");
        });

        rotateRightButton.setOnAction(e -> {
            camera.rotateAroundTarget(5, 0, ROTATION_SENSITIVITY);
            isRendering = true;
            statusLabel.setText("Камера повернута вправо");
        });

        zoomInButton.setOnAction(e -> {
            camera.zoom(50, ZOOM_SENSITIVITY);
            isRendering = true;
            statusLabel.setText("Приближение");
        });

        zoomOutButton.setOnAction(e -> {
            camera.zoom(-50, ZOOM_SENSITIVITY);
            isRendering = true;
            statusLabel.setText("Отдаление");
        });

        resetCameraButton.setOnAction(e -> {
            camera.setPosition(new Vector3(0, 0, 1000));
            camera.setTarget(new Vector3(0, 0, 0));
            isRendering = true;
            statusLabel.setText("Камера сброшена");
        });

        wireframeCheckBox.setOnAction(e -> isRendering = true);
        fillCheckBox.setOnAction(e -> isRendering = true);
        lightingCheckBox.setOnAction(e -> isRendering = true);
        zbufferCheckBox.setOnAction(e -> isRendering = true);

        if (triangulationCheckBox != null) {
            triangulationCheckBox.setOnAction(e -> isRendering = true);
        }

        if (smoothShadingCheckBox != null) {
            smoothShadingCheckBox.setOnAction(e -> isRendering = true);
        }

        if (specularCheckBox != null) {
            specularCheckBox.setOnAction(e -> isRendering = true);
        }

        modelListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        int selectedIndex = modelListView.getSelectionModel().getSelectedIndex();
                        if (selectedIndex >= 0 && selectedIndex < models.size()) {
                            activeModel = models.get(selectedIndex);
                            updatePropertiesPanel(activeModel);
                            isRendering = true;
                            statusLabel.setText("Выбрана модель: " + getModelName(activeModel));
                        }
                    }
                });

        modelColorPicker.setOnAction(e -> isRendering = true);
    }

    private String getModelName(Model model) {
        return "Model " + (models.indexOf(model) + 1);
    }

    private void loadTexture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите текстуру");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg", "*.bmp"));

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file != null) {
            if (RenderEngine.loadTexture(file.getAbsolutePath())) {
                if (textureMenuItem != null) {
                    textureMenuItem.setSelected(true);
                }
                statusLabel.setText("Текстура загружена: " + file.getName());
                isRendering = true;
            } else {
                showAlert("Ошибка загрузки", "Не удалось загрузить текстуру");
                statusLabel.setText("Ошибка загрузки текстуры");
            }
        }
    }

    private void applyDarkTheme() {
        isDarkTheme = true;

        String darkPrimary = "#1a1a1a";
        String darkSecondary = "#2d2d2d";
        String darkTertiary = "#3d3d3d";
        String darkAccent = "#4a90e2";
        String darkText = "#f0f0f0";
        String darkTextSecondary = "#b0b0b0";
        String darkBorder = "#444444";
        String darkHover = "#3a3a3a";

        rootPane.setStyle("-fx-background-color: " + darkPrimary + ";");
        leftPanel.setStyle("-fx-background-color: " + darkSecondary + "; -fx-border-color: " + darkBorder + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        rightPanel.setStyle("-fx-background-color: " + darkSecondary + "; -fx-border-color: " + darkBorder + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        canvasContainer.setStyle("-fx-background-color: " + darkTertiary + "; -fx-border-color: " + darkBorder + "; -fx-border-radius: 8; -fx-background-radius: 8;");

        canvas.setStyle("-fx-background-color: " + darkTertiary + ";");

        String buttonStyle = "-fx-background-color: " + darkTertiary + "; " +
                "-fx-text-fill: " + darkText + "; " +
                "-fx-border-color: " + darkBorder + "; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand;";

        String buttonHoverStyle = "-fx-background-color: " + darkHover + "; " +
                "-fx-text-fill: " + darkText + "; " +
                "-fx-border-color: " + darkAccent + "; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand;";

        applyButtonStyle(moveButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(rotateButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(scaleButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(deleteButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(addModelButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(removeModelButton, buttonStyle, buttonHoverStyle);
        if (quickDeleteButton != null) applyButtonStyle(quickDeleteButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(selectAllButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(clearSelectionButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(importButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(exportButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(resetButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(rotateLeftButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(rotateRightButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(zoomInButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(zoomOutButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(resetCameraButton, buttonStyle, buttonHoverStyle);

        String labelStyle = "-fx-text-fill: " + darkText + ";";
        String textFieldStyle = "-fx-background-color: " + darkTertiary + "; " +
                "-fx-text-fill: " + darkText + "; " +
                "-fx-border-color: " + darkBorder + "; " +
                "-fx-border-radius: 4; " +
                "-fx-prompt-text-fill: " + darkTextSecondary + ";";

        String listViewStyle = "-fx-background-color: " + darkTertiary + "; " +
                "-fx-text-fill: " + darkText + "; " +
                "-fx-border-color: " + darkBorder + "; " +
                "-fx-border-radius: 4; " +
                "-fx-control-inner-background: " + darkTertiary + ";";

        String checkboxStyle = "-fx-text-fill: " + darkText + ";";

        applyStyleToLabels(labelStyle);
        modelNameField.setStyle(textFieldStyle);
        vertexCountLabel.setStyle(labelStyle);
        polygonCountLabel.setStyle(labelStyle);
        modelColorPicker.setStyle("-fx-background-color: " + darkTertiary + "; -fx-text-fill: " + darkText + ";");
        modelListView.setStyle(listViewStyle);

        if (wireframeCheckBox != null) wireframeCheckBox.setStyle(checkboxStyle);
        if (fillCheckBox != null) fillCheckBox.setStyle(checkboxStyle);
        if (lightingCheckBox != null) lightingCheckBox.setStyle(checkboxStyle);
        if (zbufferCheckBox != null) zbufferCheckBox.setStyle(checkboxStyle);
        if (triangulationCheckBox != null) triangulationCheckBox.setStyle(checkboxStyle);
        if (smoothShadingCheckBox != null) smoothShadingCheckBox.setStyle(checkboxStyle);
        if (specularCheckBox != null) specularCheckBox.setStyle(checkboxStyle);

        statusLabel.setStyle("-fx-text-fill: " + darkAccent + "; -fx-font-weight: bold;");

        isRendering = !models.isEmpty();
    }

    private void applyLightTheme() {
        isDarkTheme = false;

        String lightPrimary = "#f8f9fa";
        String lightSecondary = "#ffffff";
        String lightTertiary = "#f1f3f5";
        String lightAccent = "#4263eb";
        String lightText = "#212529";
        String lightTextSecondary = "#495057";
        String lightBorder = "#dee2e6";
        String lightHover = "#e9ecef";

        rootPane.setStyle("-fx-background-color: " + lightPrimary + ";");
        leftPanel.setStyle("-fx-background-color: " + lightSecondary + "; -fx-border-color: " + lightBorder + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        rightPanel.setStyle("-fx-background-color: " + lightSecondary + "; -fx-border-color: " + lightBorder + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        canvasContainer.setStyle("-fx-background-color: " + lightSecondary + "; -fx-border-color: " + lightBorder + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        canvas.setStyle("-fx-background-color: " + lightTertiary + ";");

        String buttonStyle = "-fx-background-color: linear-gradient(to bottom, " + lightTertiary + ", #e9ecef); " +
                "-fx-text-fill: " + lightText + "; " +
                "-fx-border-color: " + lightBorder + "; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 3, 0, 0, 1);";

        String buttonHoverStyle = "-fx-background-color: linear-gradient(to bottom, #e9ecef, " + lightHover + "); " +
                "-fx-text-fill: " + lightText + "; " +
                "-fx-border-color: " + lightAccent + "; " +
                "-fx-border-radius: 6; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(66,99,235,0.2), 5, 0, 0, 2);";

        applyButtonStyle(moveButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(rotateButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(scaleButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(deleteButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(addModelButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(removeModelButton, buttonStyle, buttonHoverStyle);
        if (quickDeleteButton != null) applyButtonStyle(quickDeleteButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(selectAllButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(clearSelectionButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(importButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(exportButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(resetButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(rotateLeftButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(rotateRightButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(zoomInButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(zoomOutButton, buttonStyle, buttonHoverStyle);
        applyButtonStyle(resetCameraButton, buttonStyle, buttonHoverStyle);

        String labelStyle = "-fx-text-fill: " + lightText + ";";
        String textFieldStyle = "-fx-background-color: " + lightTertiary + "; " +
                "-fx-text-fill: " + lightText + "; " +
                "-fx-border-color: " + lightBorder + "; " +
                "-fx-border-radius: 4; " +
                "-fx-prompt-text-fill: " + lightTextSecondary + ";";

        String listViewStyle = "-fx-background-color: " + lightTertiary + "; " +
                "-fx-text-fill: " + lightText + "; " +
                "-fx-border-color: " + lightBorder + "; " +
                "-fx-border-radius: 4; " +
                "-fx-control-inner-background: " + lightTertiary + ";";

        String checkboxStyle = "-fx-text-fill: " + lightText + ";";

        applyStyleToLabels(labelStyle);
        modelNameField.setStyle(textFieldStyle);
        vertexCountLabel.setStyle(labelStyle);
        polygonCountLabel.setStyle(labelStyle);
        modelColorPicker.setStyle("-fx-background-color: " + lightTertiary + "; -fx-text-fill: " + lightText + ";");
        modelListView.setStyle(listViewStyle);

        if (wireframeCheckBox != null) wireframeCheckBox.setStyle(checkboxStyle);
        if (fillCheckBox != null) fillCheckBox.setStyle(checkboxStyle);
        if (lightingCheckBox != null) lightingCheckBox.setStyle(checkboxStyle);
        if (zbufferCheckBox != null) zbufferCheckBox.setStyle(checkboxStyle);
        if (triangulationCheckBox != null) triangulationCheckBox.setStyle(checkboxStyle);
        if (smoothShadingCheckBox != null) smoothShadingCheckBox.setStyle(checkboxStyle);
        if (specularCheckBox != null) specularCheckBox.setStyle(checkboxStyle);

        statusLabel.setStyle("-fx-text-fill: " + lightAccent + "; -fx-font-weight: bold;");

        isRendering = !models.isEmpty();
    }

    private void applyButtonStyle(Button button, String style, String hoverStyle) {
        button.setStyle(style);

        button.setOnMouseEntered(e -> {
            if (!button.styleProperty().isBound()) {
                button.setStyle(hoverStyle);
            }
        });

        button.setOnMouseExited(e -> {
            if (!button.styleProperty().isBound()) {
                button.setStyle(style);
            }
        });
    }

    private void applyStyleToLabels(String style) {
        for (Node node : leftPanel.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle(style);
            }
        }
        for (Node node : rightPanel.getChildren()) {
            if (node instanceof Label && !(node.getParent() instanceof Button)) {
                ((Label) node).setStyle(style);
            }
        }
    }

    private void updatePropertiesPanel(Model model) {
        if (model != null) {
            modelNameField.setText(getModelName(model));
        } else {
            modelNameField.setText("");
        }
    }

    private void loadModelFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите 3D модель");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("3D Files", "*.obj"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Все файлы", "*.*"));

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file != null) {
            try {
                String fileContent = Files.readString(file.toPath());
                Model model = ObjReader.read(fileContent);

                if (model.vertices.isEmpty()) {
                    showAlert("Ошибка", "Модель не содержит вершин");
                    statusLabel.setText("Ошибка: пустая модель");
                    return;
                }

                System.out.println("Загружено вершин: " + model.vertices.size());
                System.out.println("Загружено полигонов: " + model.polygons.size());
                System.out.println("Загружено нормалей: " + model.normals.size());
                System.out.println("Загружено текстурных координат: " + model.textureVertices.size());

                if (model.normals.isEmpty()) {
                    NormalCalculator.calculateVertexNormals(model);
                }

                int validPolygons = 0;
                for (Polygon polygon : model.polygons) {
                    if (polygon.getVertexIndices().size() >= 3) {
                        validPolygons++;
                    }
                }

                models.add(model);
                modelListItems.add("Модель " + models.size());

                if (activeModel == null) {
                    activeModel = model;
                    modelListView.getSelectionModel().select(0);
                }

                updatePropertiesPanel(model);
                isRendering = true;

                statusLabel.setText("Модель загружена: " + file.getName() +
                        " (" + model.vertices.size() + " вершин, " +
                        model.polygons.size() + " полигонов)");

                Platform.runLater(() -> {
                    isRendering = true;
                    render();
                });

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка загрузки", "Не удалось загрузить модель: " + e.getMessage());
                statusLabel.setText("Ошибка загрузки файла");
            }
        }
    }

    private void saveModelToFile() {
        if (activeModel == null) {
            showAlert("Ошибка", "Нет активной модели для сохранения");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить модель");
        fileChooser.setInitialFileName("model.obj");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ Files", "*.obj"));

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
        if (file != null) {
            try {
                ObjWriter.write(activeModel, file.getAbsolutePath());
                statusLabel.setText("Модель сохранена: " + file.getName());
            } catch (IOException e) {
                showAlert("Ошибка сохранения", "Не удалось сохранить файл: " + e.getMessage());
                statusLabel.setText("Ошибка сохранения");
            }
        }
    }

    private void resetTransformations() {
        if (activeModel != null) {
            activeModel.resetTransform();
            isRendering = true;
            statusLabel.setText("Преобразования сброшены");
        }
    }

    private void showTransformDialog(String title, String labelX, String labelY, String labelZ) {
        if (activeModel == null) {
            showAlert("Ошибка", "Выберите модель для преобразования");
            return;
        }

        Dialog<Float[]> dialog = new Dialog<>();
        dialog.setTitle(title);

        ButtonType applyButtonType = new ButtonType("Применить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField xField = new TextField("0");
        TextField yField = new TextField("0");
        TextField zField = new TextField("0");

        xField.setPrefWidth(80);
        yField.setPrefWidth(80);
        zField.setPrefWidth(80);

        grid.add(new Label(labelX + ":"), 0, 0);
        grid.add(xField, 1, 0);
        grid.add(new Label(labelY + ":"), 0, 1);
        grid.add(yField, 1, 1);
        grid.add(new Label(labelZ + ":"), 0, 2);
        grid.add(zField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButtonType) {
                try {
                    float x = Float.parseFloat(xField.getText());
                    float y = Float.parseFloat(yField.getText());
                    float z = Float.parseFloat(zField.getText());
                    return new Float[]{x, y, z};
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            applyTransformation(title, result[0], result[1], result[2]);
        });
    }

    private void applyTransformation(String type, float x, float y, float z) {
        if (activeModel == null) return;

        if (type.equals("Перемещение")) {
            activeModel.affineBuilder.translate(x, y, z);
            statusLabel.setText("Модель перемещена");
        } else if (type.equals("Вращение")) {
            activeModel.affineBuilder.rotateX(Math.toRadians(x))
                    .rotateY(Math.toRadians(y))
                    .rotateZ(Math.toRadians(z));
            statusLabel.setText("Модель повернута");
        } else if (type.equals("Масштабирование")) {
            activeModel.affineBuilder.scale(x, y, z);
            statusLabel.setText("Модель масштабирована");
        }

        isRendering = true;
    }

    private void showDeleteDialog() {
        if (activeModel == null) {
            showAlert("Ошибка", "Выберите модель для удаления");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Удаление элементов");
        dialog.setHeaderText("Выберите элементы для удаления:");

        ButtonType deleteButtonType = new ButtonType("Удалить выбранное", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, cancelButtonType);

        TabPane tabPane = new TabPane();

        Tab verticesTab = new Tab("Вершины");
        ListView<String> verticesList = new ListView<>();
        for (int i = 0; i < activeModel.vertices.size(); i++) {
            verticesList.getItems().add("Вершина " + i);
        }
        verticesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        verticesTab.setContent(verticesList);

        Tab polygonsTab = new Tab("Полигоны");
        ListView<String> polygonsList = new ListView<>();
        for (int i = 0; i < activeModel.polygons.size(); i++) {
            polygonsList.getItems().add("Полигон " + i + " (" +
                    activeModel.polygons.get(i).getVertexIndices().size() + " вершин)");
        }
        polygonsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        polygonsTab.setContent(polygonsList);

        tabPane.getTabs().addAll(verticesTab, polygonsTab);

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Выберите элементы для удаления:"),
                tabPane,
                new Label("Примечание: удаление вершин удалит все полигоны, содержащие эти вершины")
        );
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteButtonType) {
                Set<Integer> selectedVertices = new HashSet<>();
                for (int index : verticesList.getSelectionModel().getSelectedIndices()) {
                    selectedVertices.add(index);
                }

                Set<Integer> selectedPolygons = new HashSet<>();
                for (int index : polygonsList.getSelectionModel().getSelectedIndices()) {
                    selectedPolygons.add(index);
                }

                if (!selectedVertices.isEmpty() || !selectedPolygons.isEmpty()) {
                    deleteSelectedElements(selectedVertices, selectedPolygons);
                    return "deleted:" + selectedVertices.size() + " вершин, " +
                            selectedPolygons.size() + " полигонов";
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result.startsWith("deleted:")) {
                statusLabel.setText("Удалено: " + result.substring(8));
            }
        });
    }

    private void deleteSelectedElements(Set<Integer> vertices, Set<Integer> polygons) {
        if (activeModel == null) return;

        try {
            if (!vertices.isEmpty()) {
                deleteSelectedVertices(vertices);
            }

            if (!polygons.isEmpty()) {
                deleteSelectedPolygons(polygons);
            }

            if (!vertices.isEmpty()) {
                NormalCalculator.calculateVertexNormals(activeModel);
            }

            isRendering = true;

        } catch (Exception e) {
            showAlert("Ошибка удаления", "Не удалось удалить элементы: " + e.getMessage());
        }
    }

    private void deleteSelectedVertices(Set<Integer> verticesToRemove) {
        ArrayList<ru.vsu.cs.finaltaskcg.math.vector.Vector3> newVertices = new ArrayList<>();
        ArrayList<ru.vsu.cs.finaltaskcg.math.vector.Vector3> newNormals = new ArrayList<>();
        ArrayList<ru.vsu.cs.finaltaskcg.math.vector.Vector2> newTextureVertices = new ArrayList<>();

        Map<Integer, Integer> vertexMapping = new HashMap<>();
        int newIndex = 0;

        for (int i = 0; i < activeModel.vertices.size(); i++) {
            if (!verticesToRemove.contains(i)) {
                vertexMapping.put(i, newIndex);
                newVertices.add(activeModel.vertices.get(i));
                if (i < activeModel.normals.size()) {
                    newNormals.add(activeModel.normals.get(i));
                }
                newIndex++;
            }
        }

        if (!activeModel.textureVertices.isEmpty()) {
            for (int i = 0; i < activeModel.textureVertices.size(); i++) {
                newTextureVertices.add(activeModel.textureVertices.get(i));
            }
        }

        ArrayList<Polygon> newPolygons = new ArrayList<>();

        for (Polygon polygon : activeModel.polygons) {
            ArrayList<Integer> newVertexIndices = new ArrayList<>();
            ArrayList<Integer> newNormalIndices = new ArrayList<>();
            ArrayList<Integer> newTextureIndices = new ArrayList<>();

            boolean keepPolygon = true;

            for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
                int vertexIndex = polygon.getVertexIndices().get(i);

                if (verticesToRemove.contains(vertexIndex)) {
                    keepPolygon = false;
                    break;
                }

                if (vertexMapping.containsKey(vertexIndex)) {
                    newVertexIndices.add(vertexMapping.get(vertexIndex));

                    if (i < polygon.getNormalIndices().size()) {
                        newNormalIndices.add(polygon.getNormalIndices().get(i));
                    }

                    if (i < polygon.getTextureVertexIndices().size()) {
                        newTextureIndices.add(polygon.getTextureVertexIndices().get(i));
                    }
                }
            }

            if (keepPolygon && newVertexIndices.size() >= 3) {
                Polygon newPolygon = new Polygon();
                newPolygon.setVertexIndices(newVertexIndices);

                if (!newNormalIndices.isEmpty()) {
                    newPolygon.setNormalIndices(newNormalIndices);
                }

                if (!newTextureIndices.isEmpty()) {
                    newPolygon.setTextureVertexIndices(newTextureIndices);
                }

                newPolygons.add(newPolygon);
            }
        }

        activeModel.vertices = newVertices;
        activeModel.normals = newNormals;
        activeModel.textureVertices = newTextureVertices;
        activeModel.polygons = newPolygons;
    }

    private void deleteSelectedPolygons(Set<Integer> polygonsToRemove) {
        ArrayList<Polygon> newPolygons = new ArrayList<>();

        for (int i = 0; i < activeModel.polygons.size(); i++) {
            if (!polygonsToRemove.contains(i)) {
                newPolygons.add(activeModel.polygons.get(i));
            }
        }

        activeModel.polygons = newPolygons;
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("3D Model Viewer Pro");
        alert.setContentText("Версия 2.0\nСовременный просмотрщик 3D моделей\n\nФункции:\n• Загрузка/сохранение OBJ моделей\n• Преобразования (перемещение, вращение, масштаб)\n• Удаление вершин и полигонов\n• Темная/светлая тема\n• Современный интерфейс\n• Триангуляция\n• Освещение\n• Z-буфер\n• Текстурирование\n\nРазработано командой");
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
}