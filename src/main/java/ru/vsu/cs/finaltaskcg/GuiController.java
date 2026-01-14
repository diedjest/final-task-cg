package ru.vsu.cs.finaltaskcg;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import ru.vsu.cs.finaltaskcg.model.CameraModel;
import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.normals.NormalCalculator;
import ru.vsu.cs.finaltaskcg.objreader.ObjReader;
import ru.vsu.cs.finaltaskcg.render_engine.Camera;
import ru.vsu.cs.finaltaskcg.render_engine.RenderEngine;
import ru.vsu.cs.finaltaskcg.triangulation.Triangulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class GuiController {

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private CheckMenuItem wireframeMenuItem;

    @FXML
    private CheckMenuItem fillMenuItem;

    @FXML
    private CheckMenuItem textureMenuItem;

    @FXML
    private CheckMenuItem lightingMenuItem;

    @FXML
    private CheckMenuItem zBufferMenuItem;

    @FXML
    private ColorPicker colorPicker; // Этот элемент может быть null, если не найден в FXML

    @FXML
    private Label cameraLabel;

    private Model mesh = null;
    private ArrayList<Model> cameraModels = new ArrayList<>();

    private ArrayList<Camera> cameras = new ArrayList<>();
    private int activeCameraIndex = 0;

    private Camera camera = new Camera(
            new Vector3(0, 0, 100),
            new Vector3(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setWidth(newValue.doubleValue());
        });
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setHeight(newValue.doubleValue());
        });

        // Инициализация UI элементов (проверяем на null)
        if (colorPicker != null) {
            colorPicker.setValue(Color.LIGHTGRAY);
        } else {
            System.err.println("Warning: colorPicker is null in FXML");
        }

        if (wireframeMenuItem != null) {
            wireframeMenuItem.setSelected(true);
        }
        if (fillMenuItem != null) {
            fillMenuItem.setSelected(false);
        }
        if (textureMenuItem != null) {
            textureMenuItem.setSelected(false);
        }
        if (lightingMenuItem != null) {
            lightingMenuItem.setSelected(false);
        }
        if (zBufferMenuItem != null) {
            zBufferMenuItem.setSelected(false);
        }

        // Инициализируем камеры
        cameras.add(camera);
        updateCameraLabel();

        // Создаем модели для камер (визуализация)
        updateCameraModels();

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            // Очищаем холст
            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);

            // Устанавливаем соотношение сторон для активной камеры
            Camera activeCamera = cameras.get(activeCameraIndex);
            activeCamera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                // Устанавливаем цвет заливки (если colorPicker доступен)
                Color fillColor = Color.LIGHTGRAY;
                if (colorPicker != null) {
                    fillColor = colorPicker.getValue();
                }
                RenderEngine.setFillColor(fillColor);

                // Устанавливаем режимы рендеринга (проверяем на null)
                boolean wireframe = wireframeMenuItem != null && wireframeMenuItem.isSelected();
                boolean fill = fillMenuItem != null && fillMenuItem.isSelected();
                boolean texture = textureMenuItem != null && textureMenuItem.isSelected();
                boolean lighting = lightingMenuItem != null && lightingMenuItem.isSelected();
                boolean zBuffer = zBufferMenuItem != null && zBufferMenuItem.isSelected();

                RenderEngine.setRenderMode(wireframe, fill, texture, lighting, zBuffer);

                // Устанавливаем позицию источника света (привязано к активной камере)
                if (lighting && lightingMenuItem != null && lightingMenuItem.isSelected()) {
                    RenderEngine.setLightPosition(activeCamera.getPosition());
                }

                // Рендерим основную модель
                RenderEngine.render(
                        canvas.getGraphicsContext2D(),
                        activeCamera,
                        mesh,
                        (int) width,
                        (int) height
                );

                // Рендерим модели камер (кроме активной)
                for (int i = 0; i < cameras.size(); i++) {
                    if (i != activeCameraIndex && i < cameraModels.size()) {
                        Model cameraModel = cameraModels.get(i);
                        if (cameraModel != null) {
                            RenderEngine.renderCameraModel(
                                    canvas.getGraphicsContext2D(),
                                    cameras.get(i),
                                    cameraModel,
                                    activeCamera,
                                    (int) width,
                                    (int) height
                            );
                        }
                    }
                }
            } else {
                // Если модель не загружена, показываем инструкцию
                canvas.getGraphicsContext2D().fillText("Загрузите модель через File -> Open Model", 20, 20);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);

            // Триангуляция модели
            Triangulator.triangulate(mesh);

            // Вычисление нормалей (перезаписываем даже если есть в файле)
            NormalCalculator.calculateVertexNormals(mesh);

            System.out.println("Model loaded successfully: " + file.getName());
            System.out.println("Vertices: " + mesh.vertices.size());
            System.out.println("Polygons (triangles): " + mesh.polygons.size());
            System.out.println("Normals: " + mesh.normals.size());

        } catch (IOException exception) {
            System.err.println("Error reading file: " + exception.getMessage());
            showAlert("Ошибка загрузки", "Не удалось загрузить файл: " + exception.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing model: " + e.getMessage());
            showAlert("Ошибка обработки", "Ошибка при обработке модели: " + e.getMessage());
        }
    }

    @FXML
    private void onOpenTextureMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        fileChooser.setTitle("Load Texture");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file != null) {
            if (RenderEngine.loadTexture(file.getAbsolutePath())) {
                if (textureMenuItem != null) {
                    textureMenuItem.setSelected(true);
                }
                System.out.println("Texture loaded: " + file.getName());
            } else {
                showAlert("Ошибка загрузки", "Не удалось загрузить текстуру");
            }
        }
    }

    @FXML
    private void onWireframeModeChanged(ActionEvent event) {
        System.out.println("Wireframe mode: " + (wireframeMenuItem != null && wireframeMenuItem.isSelected()));
    }

    @FXML
    private void onFillModeChanged(ActionEvent event) {
        System.out.println("Fill mode: " + (fillMenuItem != null && fillMenuItem.isSelected()));
    }

    @FXML
    private void onTextureModeChanged(ActionEvent event) {
        System.out.println("Texture mode: " + (textureMenuItem != null && textureMenuItem.isSelected()));
        if (textureMenuItem != null && textureMenuItem.isSelected() && !RenderEngine.isTextureLoaded()) {
            showAlert("Текстура не загружена", "Пожалуйста, загрузите текстуру через File -> Load Texture");
            textureMenuItem.setSelected(false);
        }
    }

    @FXML
    private void onLightingModeChanged(ActionEvent event) {
        System.out.println("Lighting mode: " + (lightingMenuItem != null && lightingMenuItem.isSelected()));
        if (lightingMenuItem != null && lightingMenuItem.isSelected()) {
            // Источник света привязывается к позиции активной камеры
            RenderEngine.setLightPosition(cameras.get(activeCameraIndex).getPosition());
        }
    }

    @FXML
    private void onZBufferModeChanged(ActionEvent event) {
        System.out.println("Z-Buffer mode: " + (zBufferMenuItem != null && zBufferMenuItem.isSelected()));
    }

    @FXML
    private void onColorChanged(ActionEvent event) {
        if (colorPicker != null) {
            System.out.println("Color changed to: " + colorPicker.getValue());
        }
    }

    @FXML
    private void onAddCamera(ActionEvent event) {
        // Создаем новую камеру со смещением от текущей
        Camera activeCamera = cameras.get(activeCameraIndex);
        Vector3 position = new Vector3(
                activeCamera.getPosition().getX() + 50,
                activeCamera.getPosition().getY(),
                activeCamera.getPosition().getZ()
        );

        Camera newCamera = new Camera(
                position,
                new Vector3(0, 0, 0),
                1.0F, 1, 0.01F, 100
        );

        cameras.add(newCamera);
        activeCameraIndex = cameras.size() - 1;

        // Обновляем модели камер
        updateCameraModels();
        updateCameraLabel();

        System.out.println("Camera added. Total cameras: " + cameras.size());
    }

    @FXML
    private void onRemoveCamera(ActionEvent event) {
        if (cameras.size() > 1) {
            cameras.remove(activeCameraIndex);
            if (activeCameraIndex < cameraModels.size()) {
                cameraModels.remove(activeCameraIndex);
            }

            // Переключаемся на предыдущую камеру
            activeCameraIndex = Math.max(0, activeCameraIndex - 1);
            if (activeCameraIndex >= cameras.size()) {
                activeCameraIndex = cameras.size() - 1;
            }

            updateCameraLabel();
            System.out.println("Camera removed. Total cameras: " + cameras.size());
        } else {
            showAlert("Невозможно удалить", "Должна остаться хотя бы одна камера");
        }
    }

    @FXML
    private void onNextCamera(ActionEvent event) {
        activeCameraIndex = (activeCameraIndex + 1) % cameras.size();
        updateCameraLabel();

        // Обновляем позицию источника света при включенном освещении
        if (lightingMenuItem != null && lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cameras.get(activeCameraIndex).getPosition());
        }

        System.out.println("Switched to camera " + (activeCameraIndex + 1));
    }

    private void updateCameraLabel() {
        if (cameraLabel != null) {
            cameraLabel.setText("Camera " + (activeCameraIndex + 1) + " of " + cameras.size());
        }
    }

    private void updateCameraModels() {
        cameraModels.clear();
        for (Camera cam : cameras) {
            Model cameraModel = CameraModel.createCameraModel(
                    cam.getPosition(),
                    cam.getTarget()
            );
            cameraModels.add(cameraModel);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        cameras.get(activeCameraIndex).movePosition(new Vector3(0, 0, -TRANSLATION));
        updateCameraModels();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        cameras.get(activeCameraIndex).movePosition(new Vector3(0, 0, TRANSLATION));
        updateCameraModels();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        cameras.get(activeCameraIndex).movePosition(new Vector3(TRANSLATION, 0, 0));
        updateCameraModels();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        cameras.get(activeCameraIndex).movePosition(new Vector3(-TRANSLATION, 0, 0));
        updateCameraModels();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        cameras.get(activeCameraIndex).movePosition(new Vector3(0, TRANSLATION, 0));
        updateCameraModels();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        cameras.get(activeCameraIndex).movePosition(new Vector3(0, -TRANSLATION, 0));
        updateCameraModels();
    }

    // Дополнительные методы для управления камерой (вращение)
    @FXML
    public void handleCameraRotateLeft(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        Vector3 target = cam.getTarget();
        target = new Vector3(target.getX() + TRANSLATION * 5, target.getY(), target.getZ());
        cam.setTarget(target);
        updateCameraModels();
    }

    @FXML
    public void handleCameraRotateRight(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        Vector3 target = cam.getTarget();
        target = new Vector3(target.getX() - TRANSLATION * 5, target.getY(), target.getZ());
        cam.setTarget(target);
        updateCameraModels();
    }

    @FXML
    public void handleCameraRotateUp(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        Vector3 target = cam.getTarget();
        target = new Vector3(target.getX(), target.getY() + TRANSLATION * 5, target.getZ());
        cam.setTarget(target);
        updateCameraModels();
    }

    @FXML
    public void handleCameraRotateDown(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        Vector3 target = cam.getTarget();
        target = new Vector3(target.getX(), target.getY() - TRANSLATION * 5, target.getZ());
        cam.setTarget(target);
        updateCameraModels();
    }

    // Метод для сброса камеры
    @FXML
    public void handleCameraReset(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.setPosition(new Vector3(0, 0, 100));
        cam.setTarget(new Vector3(0, 0, 0));
        updateCameraModels();
    }

    @FXML
    public void handleExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void handleAbout(ActionEvent event) {
        showAlert("О программе", "Simple3DViewer\nВерсия 2.0\n3D просмотрщик моделей");
    }
}