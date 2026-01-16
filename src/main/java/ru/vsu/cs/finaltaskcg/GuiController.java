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
import ru.vsu.cs.finaltaskcg.triangulation.FanTriangulator;
import ru.vsu.cs.finaltaskcg.triangulation.Triangulator;
import ru.vsu.cs.finaltaskcg.lighting.LightCalculator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class GuiController {

    final private double TRANSLATION = 10.0;

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
    private CheckMenuItem triangulationMenuItem;

    @FXML
    private CheckMenuItem smoothShadingMenuItem;

    @FXML
    private CheckMenuItem specularMenuItem;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Label cameraLabel;

    private Model mesh = null;
    private ArrayList<Model> cameraModels = new ArrayList<>();

    private ArrayList<Camera> cameras = new ArrayList<>();
    private int activeCameraIndex = 0;

    private Camera camera = new Camera(
            new Vector3(0, 0, 100),
            new Vector3(0, 0, 0),
            1.0F, 1, 0.1F, 100);

    private Timeline timeline;

    private boolean isMousePressed = false;
    private boolean isMiddleMousePressed = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private final double MOUSE_SENSITIVITY = 0.2;
    private final double PAN_SENSITIVITY = 0.005;
    private final double ZOOM_SENSITIVITY = 0.05;
    private final double ROTATION_SENSITIVITY = 0.5;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setWidth(newValue.doubleValue());
        });
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setHeight(newValue.doubleValue());
        });

        // Инициализация UI элементов
        colorPicker.setValue(Color.LIGHTGRAY);

        // Устанавливаем начальные значения для меню
        wireframeMenuItem.setSelected(true);
        fillMenuItem.setSelected(false);
        textureMenuItem.setSelected(false);
        lightingMenuItem.setSelected(false);
        zBufferMenuItem.setSelected(false);
        triangulationMenuItem.setSelected(true); // Включаем автотриангуляцию по умолчанию
        smoothShadingMenuItem.setSelected(false); // Плоское затенение по умолчанию
        specularMenuItem.setSelected(false); // Зеркальное освещение выключено по умолчанию

        // Устанавливаем начальные параметры в RenderEngine
        RenderEngine.setAutoTriangulate(true);
        RenderEngine.setSmoothShading(false);
        RenderEngine.setSpecularLighting(false);
        RenderEngine.setLightingParameters(0.3, 0.7, 0.3, 16);
        RenderEngine.setShowSceneHelpers(true);

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
                // Устанавливаем цвет заливки
                Color fillColor = colorPicker.getValue();
                RenderEngine.setFillColor(fillColor);

                // Устанавливаем режимы рендеринга
                boolean wireframe = wireframeMenuItem.isSelected();
                boolean fill = fillMenuItem.isSelected();
                boolean texture = textureMenuItem.isSelected();
                boolean lighting = lightingMenuItem.isSelected();
                boolean zBuffer = zBufferMenuItem.isSelected();

                RenderEngine.setRenderMode(wireframe, fill, texture, lighting, zBuffer);

                // Устанавливаем позицию источника света (привязано к активной камере)
                if (lighting && lightingMenuItem.isSelected()) {
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
                canvas.getGraphicsContext2D().fillText("Используйте левую кнопку мыши для панорамирования", 20, 40);
                canvas.getGraphicsContext2D().fillText("Среднюю кнопку для вращения", 20, 60);
                canvas.getGraphicsContext2D().fillText("Колесико для зума", 20, 80);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
        setupMouseHandlers();
    }

    private void setupMouseHandlers() {
        // Обработка нажатия мыши
        anchorPane.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                // Левая кнопка мыши - панорамирование (движение камеры)
                isMousePressed = true;
                lastMouseX = event.getX();
                lastMouseY = event.getY();
                event.consume();
            } else if (event.isMiddleButtonDown()) {
                // Средняя кнопка мыши - вращение
                isMiddleMousePressed = true;
                lastMouseX = event.getX();
                lastMouseY = event.getY();
                event.consume();
            }
        });

        // Обработка перетаскивания мыши
        anchorPane.setOnMouseDragged(event -> {
            if (isMousePressed && !isMiddleMousePressed) {
                // Панорамирование левой кнопкой мыши
                double deltaX = event.getX() - lastMouseX;
                double deltaY = event.getY() - lastMouseY;

                Camera cam = cameras.get(activeCameraIndex);
                cam.pan(deltaX, deltaY, PAN_SENSITIVITY);

                // Обновляем позицию света, если освещение включено
                if (lightingMenuItem.isSelected()) {
                    RenderEngine.setLightPosition(cam.getPosition());
                }

                updateCameraModels();
                lastMouseX = event.getX();
                lastMouseY = event.getY();
                event.consume();

            } else if (isMiddleMousePressed) {
                // Вращение средней кнопкой мыши
                double deltaX = event.getX() - lastMouseX;
                double deltaY = event.getY() - lastMouseY;

                Camera cam = cameras.get(activeCameraIndex);
                cam.rotateAroundTarget((float)deltaX, (float)deltaY, ROTATION_SENSITIVITY);

                // Обновляем позицию света, если освещение включено
                if (lightingMenuItem.isSelected()) {
                    RenderEngine.setLightPosition(cam.getPosition());
                }

                updateCameraModels();
                lastMouseX = event.getX();
                lastMouseY = event.getY();
                event.consume();
            }
        });

        // Обработка отпускания кнопок мыши
        anchorPane.setOnMouseReleased(event -> {
            isMousePressed = false;
            isMiddleMousePressed = false;
        });

        // Обработка колесика мыши
        anchorPane.setOnScroll(event -> {
            Camera cam = cameras.get(activeCameraIndex);

            // Zoom колесиком мыши
            cam.zoom((float)event.getDeltaY(), ZOOM_SENSITIVITY);

            // Обновляем позицию света, если освещение включено
            if (lightingMenuItem.isSelected()) {
                RenderEngine.setLightPosition(cam.getPosition());
            }

            updateCameraModels();
            event.consume();
        });

        // Убираем выделение текста при перетаскивании
        anchorPane.setOnDragDetected(event -> anchorPane.startFullDrag());
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

            // Выводим информацию о загруженной модели
            System.out.println("=== Информация о модели ===");
            System.out.println("Название файла: " + file.getName());
            System.out.println("Вершин: " + mesh.vertices.size());
            System.out.println("Текстурных координат: " + mesh.textureVertices.size());
            System.out.println("Нормалей: " + mesh.normals.size());
            System.out.println("Полигонов: " + mesh.polygons.size());

            // Проверяем, есть ли полигоны с более чем 3 вершинами
            int nonTrianglePolygons = 0;
            for (ru.vsu.cs.finaltaskcg.model.Polygon polygon : mesh.polygons) {
                if (polygon.getVertexIndices().size() > 3) {
                    nonTrianglePolygons++;
                }
            }
            System.out.println("Полигонов с >3 вершинами: " + nonTrianglePolygons);

            // Триангуляция модели (используем веерную триангуляцию)
            Triangulator triangulator = new FanTriangulator();
            if (triangulator.needsTriangulation(mesh)) {
                System.out.println("Триангулируем модель...");
                long startTime = System.currentTimeMillis();
                triangulator.triangulate(mesh);
                long endTime = System.currentTimeMillis();
                System.out.println("Модель триангулирована за " + (endTime - startTime) + " мс");
                System.out.println("Треугольников после триангуляции: " + mesh.polygons.size());
            } else {
                System.out.println("Модель уже состоит из треугольников, триангуляция не требуется");
            }

            // Вычисление нормалей (перезаписываем даже если есть в файле)
            System.out.println("Вычисляем нормали...");
            NormalCalculator.calculateVertexNormals(mesh);
            System.out.println("Нормалей вычислено: " + mesh.normals.size());

            System.out.println("=== Загрузка завершена ===\n");

        } catch (IOException exception) {
            System.err.println("Ошибка чтения файла: " + exception.getMessage());
            showAlert("Ошибка загрузки", "Не удалось загрузить файл: " + exception.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка обработки модели: " + e.getMessage());
            e.printStackTrace();
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
                textureMenuItem.setSelected(true);
                System.out.println("Текстура загружена: " + file.getName());
            } else {
                showAlert("Ошибка загрузки", "Не удалось загрузить текстуру");
            }
        }
    }

    @FXML
    private void onWireframeModeChanged(ActionEvent event) {
        System.out.println("Режим полигональной сетки: " + (wireframeMenuItem.isSelected() ? "ВКЛ" : "ВЫКЛ"));
    }

    @FXML
    private void onFillModeChanged(ActionEvent event) {
        System.out.println("Режим заливки треугольников: " + (fillMenuItem.isSelected() ? "ВКЛ" : "ВЫКЛ"));

        // Если включаем заливку, рекомендуется использовать Z-буфер
        if (fillMenuItem.isSelected() && !zBufferMenuItem.isSelected()) {
            System.out.println("Рекомендуется включить Z-буфер для корректной заливки");
        }
    }

    @FXML
    private void onTextureModeChanged(ActionEvent event) {
        System.out.println("Режим текстуры: " + (textureMenuItem.isSelected() ? "ВКЛ" : "ВЫКЛ"));
        if (textureMenuItem.isSelected() && !RenderEngine.isTextureLoaded()) {
            showAlert("Текстура не загружена",
                    "Пожалуйста, загрузите текстуру через File -> Load Texture\n" +
                            "Или выберите модель с текстурными координатами");
            textureMenuItem.setSelected(false);
        }
    }

    @FXML
    private void onLightingModeChanged(ActionEvent event) {
        System.out.println("Режим освещения: " + (lightingMenuItem.isSelected() ? "ВКЛ" : "ВЫКЛ"));
        if (lightingMenuItem.isSelected()) {
            // Источник света привязывается к позиции активной камеры
            Camera activeCamera = cameras.get(activeCameraIndex);
            RenderEngine.setLightPosition(activeCamera.getPosition());

            // Устанавливаем параметры освещения (улучшенные)
            RenderEngine.setLightingParameters(0.3, 0.7, 0.3, 16);

            System.out.println("Источник света установлен в позицию камеры");
            System.out.println("Параметры освещения: ambient=0.3, diffuse=0.7, specular=0.3, shininess=16");
        }
    }

    @FXML
    private void onZBufferModeChanged(ActionEvent event) {
        System.out.println("Режим Z-буфера: " + (zBufferMenuItem.isSelected() ? "ВКЛ" : "ВЫКЛ"));
    }

    @FXML
    private void onTriangulationModeChanged(ActionEvent event) {
        boolean enabled = triangulationMenuItem.isSelected();
        RenderEngine.setAutoTriangulate(enabled);
        System.out.println("Автотриангуляция: " + (enabled ? "ВКЛ" : "ВЫКЛ"));

        // Если отключаем автотриангуляцию, предупреждаем пользователя
        if (!enabled && mesh != null) {
            // Проверяем, есть ли в модели полигоны с более чем 3 вершинами
            Triangulator triangulator = new FanTriangulator();
            if (triangulator.needsTriangulation(mesh)) {
                showAlert("Предупреждение",
                        "Модель содержит полигоны с более чем 3 вершинами.\n" +
                                "Без триангуляции они будут отображены некорректно.\n" +
                                "Рекомендуется включить автотриангуляцию.");
            }
        }
    }

    @FXML
    private void onSmoothShadingModeChanged(ActionEvent event) {
        boolean enabled = smoothShadingMenuItem.isSelected();
        RenderEngine.setSmoothShading(enabled);
        System.out.println("Плавное затенение: " + (enabled ? "ВКЛ" : "ВЫКЛ"));

        // Если включаем плавное затенение, убедимся что есть нормали
        if (enabled && mesh != null && (mesh.normals.isEmpty() || mesh.normals.size() != mesh.vertices.size())) {
            System.out.println("Для плавного затенения требуются нормали вершин");
            System.out.println("Пересчитываем нормали...");
            NormalCalculator.calculateVertexNormals(mesh);
        }
    }

    @FXML
    private void onSpecularModeChanged(ActionEvent event) {
        boolean enabled = specularMenuItem.isSelected();
        RenderEngine.setSpecularLighting(enabled);
        System.out.println("Зеркальное освещение: " + (enabled ? "ВКЛ" : "ВЫКЛ"));
    }

    @FXML
    private void onColorChanged(ActionEvent event) {
        System.out.println("Цвет заливки изменен на: " + colorPicker.getValue());
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
                1.0F, 1, 0.1F, 100
        );

        cameras.add(newCamera);
        activeCameraIndex = cameras.size() - 1;

        // Обновляем модели камер
        updateCameraModels();
        updateCameraLabel();

        System.out.println("Камера добавлена. Всего камер: " + cameras.size());
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
            System.out.println("Камера удалена. Всего камер: " + cameras.size());
        } else {
            showAlert("Невозможно удалить", "Должна остаться хотя бы одна камера");
        }
    }

    @FXML
    private void onNextCamera(ActionEvent event) {
        activeCameraIndex = (activeCameraIndex + 1) % cameras.size();
        updateCameraLabel();

        // Обновляем позицию источника света при включенном освещении
        if (lightingMenuItem.isSelected()) {
            Camera activeCamera = cameras.get(activeCameraIndex);
            RenderEngine.setLightPosition(activeCamera.getPosition());
            System.out.println("Источник света перемещен к камере " + (activeCameraIndex + 1));
        }

        System.out.println("Переключено на камеру " + (activeCameraIndex + 1));
    }

    private void updateCameraLabel() {
        cameraLabel.setText("Камера " + (activeCameraIndex + 1) + " из " + cameras.size());
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
        Camera cam = cameras.get(activeCameraIndex);
        cam.moveForwardBackward(-TRANSLATION);

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера: вперед");
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.moveForwardBackward(TRANSLATION);

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера: назад");
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.moveRightLeft(TRANSLATION);

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера: влево");
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.moveRightLeft(-TRANSLATION);

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера: вправо");
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.moveUpDown(TRANSLATION);

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера: вверх");
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.moveUpDown(-TRANSLATION);

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера: вниз");
    }

    // Дополнительные методы для управления камерой (вращение)
    @FXML
    public void handleCameraRotateLeft(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.rotateAroundTarget(-5, 0, ROTATION_SENSITIVITY);

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера: вращение влево");
    }

    @FXML
    public void handleCameraRotateRight(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.rotateAroundTarget(5, 0, ROTATION_SENSITIVITY);

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера: вращение вправо");
    }

    @FXML
    public void handleCameraRotateUp(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.rotateAroundTarget(0, -5, ROTATION_SENSITIVITY);

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера: вращение вверх");
    }

    @FXML
    public void handleCameraRotateDown(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.rotateAroundTarget(0, 5, ROTATION_SENSITIVITY);

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера: вращение вниз");
    }

    // Метод для сброса камеры
    @FXML
    public void handleCameraReset(ActionEvent actionEvent) {
        Camera cam = cameras.get(activeCameraIndex);
        cam.setPosition(new Vector3(0, 0, 100));
        cam.setTarget(new Vector3(0, 0, 0));

        // Обновляем позицию света, если освещение включено
        if (lightingMenuItem.isSelected()) {
            RenderEngine.setLightPosition(cam.getPosition());
        }

        updateCameraModels();
        System.out.println("Камера сброшена в начальное положение");
    }

    @FXML
    public void handleExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void handleAbout(ActionEvent event) {
        showAlert("О программе",
                "3D Model Viewer Pro\n" +
                        "Версия 2.0\n" +
                        "\n" +
                        "Функции:\n" +
                        "- Загрузка моделей OBJ\n" +
                        "- Триангуляция полигонов\n" +
                        "- Вычисление нормалей\n" +
                        "- Растеризация треугольников\n" +
                        "- Z-буфер\n" +
                        "- Текстурирование\n" +
                        "- Освещение (плоское и плавное)\n" +
                        "- Зеркальные блики\n" +
                        "- Несколько камер\n" +
                        "\n" +
                        "Управление:\n" +
                        "- Левая кнопка мыши: панорамирование\n" +
                        "- Средняя кнопка: вращение\n" +
                        "- Колесико: зум\n" +
                        "- W/A/S/D/Q/E: перемещение камеры\n" +
                        "\n" +
                        "Режимы отрисовки:\n" +
                        "- Wireframe: полигональная сетка\n" +
                        "- Fill: заливка треугольников\n" +
                        "- Texture: наложение текстуры\n" +
                        "- Lighting: включение освещения\n" +
                        "- Z-Buffer: устранение перекрытий\n" +
                        "- Auto Triangulation: автоматическая триангуляция\n" +
                        "- Smooth Shading: плавное затенение\n" +
                        "- Specular Lighting: зеркальное освещение");
    }
}