package ru.vsu.cs.finaltaskcg.render_engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;
import ru.vsu.cs.finaltaskcg.math.vector.Vector2;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;
import ru.vsu.cs.finaltaskcg.texture.TextureLoader;
import ru.vsu.cs.finaltaskcg.rasterization.Rasterization;
import ru.vsu.cs.finaltaskcg.triangulation.FanTriangulator;
import ru.vsu.cs.finaltaskcg.triangulation.Triangulator;
import ru.vsu.cs.finaltaskcg.lighting.LightCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RenderEngine {

    // Режимы рендеринга
    private static boolean drawWireframe = true;
    private static boolean fillTriangles = false;
    private static boolean useTexture = false;
    private static boolean useLighting = false;
    private static boolean useZBuffer = false;
    private static boolean smoothShading = false;  // Новый режим: плавное затенение
    private static boolean useSpecular = false;    // Зеркальное освещение
    private static boolean showSceneHelpers = true;

    // Триангуляция
    private static boolean autoTriangulate = true;
    private static Triangulator triangulator = new FanTriangulator();

    // Текущие настройки
    private static Color fillColor = Color.LIGHTGRAY;
    private static TextureLoader textureLoader = new TextureLoader();
    private static Vector3 lightPosition = new Vector3(0, 100, 100);

    // Позиция камеры для освещения
    private static Vector3 cameraPosition = new Vector3(0, 0, 100);

    // Z-буфер
    private static ZBuffer zBuffer;

    // Статистика
    private static int trianglesRendered = 0;
    private static int pixelsRendered = 0;

    // Кэш для преобразованных вершин
    private static Map<Integer, Vector3> transformedVerticesCache = new HashMap<>();

    // Методы управления
    public static void setShowSceneHelpers(boolean show) {
        showSceneHelpers = show;
    }

    public static void toggleSceneHelpers() {
        showSceneHelpers = !showSceneHelpers;
    }

    // Методы для управления освещением
    public static void setSmoothShading(boolean enabled) {
        smoothShading = enabled;
    }

    public static void setSpecularLighting(boolean enabled) {
        useSpecular = enabled;
        LightCalculator.setLightingComponents(true, true, enabled);
    }

    public static void setLightingParameters(double ambient, double diffuse, double specular, int shininess) {
        LightCalculator.setLightingParameters(ambient, diffuse, specular, shininess);
    }

    public static void setLightingMode(String mode) {
        // mode can be "flat", "smooth", "simple"
        if ("smooth".equals(mode)) {
            setSmoothShading(true);
        } else {
            setSmoothShading(false);
        }
    }

    // Методы для управления триангуляцией
    public static void setAutoTriangulate(boolean enable) {
        autoTriangulate = enable;
    }

    public static void setTriangulator(Triangulator triangulator) {
        RenderEngine.triangulator = triangulator;
    }

    public static boolean isAutoTriangulate() {
        return autoTriangulate;
    }

    public static void setRenderMode(boolean wireframe, boolean fill,
                                     boolean texture, boolean lighting,
                                     boolean zBufferMode) {
        drawWireframe = wireframe;
        fillTriangles = fill;
        useTexture = texture;
        useLighting = lighting;
        useZBuffer = zBufferMode;

    }

    public static void setFillColor(Color color) {
        fillColor = color;
    }

    public static void setLightPosition(Vector3 position) {
        lightPosition = position;
    }

    public static boolean loadTexture(String filePath) {
        return textureLoader.loadTexture(filePath);
    }

    public static boolean isTextureLoaded() {
        return textureLoader.isLoaded();
    }

    public static void clearCache() {
        transformedVerticesCache.clear();
    }

    public static int getTrianglesRendered() {
        return trianglesRendered;
    }

    public static int getPixelsRendered() {
        return pixelsRendered;
    }

    public static void resetStats() {
        trianglesRendered = 0;
        pixelsRendered = 0;
    }

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) {

        // Сбрасываем статистику
        resetStats();
        clearCache();

        // Сохраняем позицию камеры для освещения
        cameraPosition = camera.getPosition();

        // Устанавливаем параметры освещения
        LightCalculator.setLightingParameters(0.3, 0.7, 0.3, 16);
        LightCalculator.setLightingComponents(true, true, useSpecular);

        // Проверка входных данных
        if (mesh == null || mesh.vertices.isEmpty() || mesh.polygons.isEmpty()) {
            return;
        }

        // Автоматическая триангуляция, если включена и модель нуждается в ней
        Model renderModel = mesh;
        if (autoTriangulate && triangulator.needsTriangulation(mesh)) {
            renderModel = triangulator.createTriangulatedModel(mesh);
        }

        // Инициализация Z-буфера если нужно
        if (useZBuffer) {
            if (zBuffer == null || zBuffer.getWidth() != width || zBuffer.getHeight() != height) {
                zBuffer = new ZBuffer(width, height);
            }
            zBuffer.clear();
        }

        // Получаем матрицы преобразования
        Matrix4 modelMatrix = GraphicConveyor.rotateScaleTranslate();
        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();

        // Комбинированная матрица: Model * View * Projection
        Matrix4 modelViewProjectionMatrix = modelMatrix.mul(viewMatrix).mul(projectionMatrix);

        // Получаем матрицу ModelView для освещения (без проекции)
        Matrix4 modelViewMatrix = modelMatrix.mul(viewMatrix);

        // Рендерим все полигоны
        final int nPolygons = renderModel.polygons.size();
        trianglesRendered = 0;

        for (int polygonIndex = 0; polygonIndex < nPolygons; polygonIndex++) {
            Polygon polygon = renderModel.polygons.get(polygonIndex);
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

            // Пропускаем полигоны с менее чем 3 вершинами
            if (vertexIndices.size() < 3) {
                continue;
            }

            // Получаем преобразованные вершины
            Vector3[] vertices = new Vector3[vertexIndices.size()];
            Vector2[] screenPoints = new Vector2[vertexIndices.size()];
            Vector3[] worldVertices = new Vector3[vertexIndices.size()];

            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i);

                // Используем кэш для преобразованных вершин
                Vector3 vertex;
                if (transformedVerticesCache.containsKey(vertexIndex)) {
                    vertex = transformedVerticesCache.get(vertexIndex);
                } else {
                    Vector3 originalVertex = renderModel.vertices.get(vertexIndex);
                    vertex = GraphicConveyor.multiplyMatrix4ByVector3(modelViewProjectionMatrix, originalVertex);
                    transformedVerticesCache.put(vertexIndex, vertex);
                }

                vertices[i] = vertex;
                screenPoints[i] = GraphicConveyor.vertexToPoint(vertex, width, height);

                // Сохраняем мировые координаты для освещения
                worldVertices[i] = GraphicConveyor.multiplyMatrix4ByVector3(modelViewMatrix, renderModel.vertices.get(vertexIndex));
            }

            // После триангуляции все полигоны должны быть треугольниками,
            // но на всякий случай оставляем проверку
            if (vertexIndices.size() > 3) {
                renderTriangulatedPolygon(graphicsContext, polygon, renderModel,
                        modelViewProjectionMatrix, modelViewMatrix,
                        width, height);
            } else {
                // Рендерим треугольник
                renderTriangle(graphicsContext, vertices, screenPoints, worldVertices,
                        polygon, renderModel, width, height);
                trianglesRendered++;
            }
        }

        // Рисуем статистику
        if (drawWireframe) {
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillText("Triangles: " + trianglesRendered, 10, 20);
            graphicsContext.fillText("Z-Buffer: " + (useZBuffer ? "ON" : "OFF"), 10, 40);
            graphicsContext.fillText("Lighting: " + (useLighting ? "ON" : "OFF"), 10, 60);
            graphicsContext.fillText("Shading: " + (smoothShading ? "Smooth" : "Flat"), 10, 80);
            graphicsContext.fillText("Specular: " + (useSpecular ? "ON" : "OFF"), 10, 100);
            graphicsContext.fillText("Texture: " + (useTexture && textureLoader.isLoaded() ? "ON" : "OFF"), 10, 120);
            graphicsContext.fillText("Triangulation: " + (autoTriangulate ? "ON" : "OFF"), 10, 140);
        }
    }

    private static void renderTriangle(
            final GraphicsContext graphicsContext,
            final Vector3[] vertices,
            final Vector2[] screenPoints,
            final Vector3[] worldVertices,
            final Polygon polygon,
            final Model mesh,
            final int width,
            final int height) {

        // 1. Проверяем, находится ли треугольник в поле зрения камеры
        if (!isTriangleInFrustum(vertices)) {
            return;
        }

        // 2. Проверяем экранную видимость (уже есть, но можно оставить)
        if (!isTriangleVisible(screenPoints, width, height)) {
            return;
        }

        // 3. Проверяем back-face culling (отсечение нелицевых граней)
        if (shouldCullBackFace(vertices)) {
            return;
        }

        Vector2[] textureCoords = null;
        if (useTexture && textureLoader.isLoaded() &&
                !polygon.getTextureVertexIndices().isEmpty()) {

            textureCoords = new Vector2[3];
            ArrayList<Integer> texIndices = polygon.getTextureVertexIndices();

            for (int i = 0; i < 3; i++) {
                if (texIndices.get(i) >= 0 && texIndices.get(i) < mesh.textureVertices.size()) {
                    textureCoords[i] = mesh.textureVertices.get(texIndices.get(i));
                } else {
                    textureCoords = null; // Если что-то не так, отключаем текстурирование
                    break;
                }
            }
        }

        Color triangleColor = getTriangleColor(polygon, mesh, worldVertices);

        // Заливка треугольника (если включена)
        if (fillTriangles) {
            if (useZBuffer) {
                // Передаем textureCoords
                fillTriangleZBuffer(graphicsContext, vertices, screenPoints,
                        textureCoords, triangleColor, polygon, mesh,
                        width, height);
            } else {
                fillTriangleSimple(graphicsContext, screenPoints, triangleColor);
            }
        }

        // Отрисовка полигональной сетки (если включена)
        if (drawWireframe) {
            drawWireframe(graphicsContext, screenPoints);
        }
    }

    private static boolean shouldCullBackFace(Vector3[] vertices) {
        // Вычисляем нормаль треугольника в экранных координатах
        // Если нормаль направлена от камеры (z > 0), отсекаем
        Vector3 v0 = vertices[0];
        Vector3 v1 = vertices[1];
        Vector3 v2 = vertices[2];

        Vector3 edge1 = v1.sub(v0);
        Vector3 edge2 = v2.sub(v0);
        Vector3 normal = edge1.cross(edge2);

        // В NDC камера смотрит по -z, так что если нормаль.z > 0,
        // треугольник направлен от камеры
        return normal.getZ() > 0;
    }

    private static void renderTriangulatedPolygon(
            final GraphicsContext graphicsContext,
            final Polygon polygon,
            final Model mesh,
            final Matrix4 modelViewProjectionMatrix,
            final Matrix4 modelViewMatrix,
            final int width,
            final int height) {

        ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

        // Триангуляция веером (на случай, если автотриангуляция отключена)
        for (int i = 1; i < vertexIndices.size() - 1; i++) {
            int[] triIndices = {0, i, i + 1};
            Vector3[] triVertices = new Vector3[3];
            Vector2[] triScreenPoints = new Vector2[3];
            Vector3[] triWorldVertices = new Vector3[3];

            for (int j = 0; j < 3; j++) {
                int vertexIndex = vertexIndices.get(triIndices[j]);
                Vector3 originalVertex = mesh.vertices.get(vertexIndex);

                triVertices[j] = GraphicConveyor.multiplyMatrix4ByVector3(modelViewProjectionMatrix, originalVertex);
                triScreenPoints[j] = GraphicConveyor.vertexToPoint(triVertices[j], width, height);
                triWorldVertices[j] = GraphicConveyor.multiplyMatrix4ByVector3(modelViewMatrix, originalVertex);
            }

            // Создаем временный полигон для треугольника
            Polygon tempPolygon = new Polygon();
            ArrayList<Integer> tempIndices = new ArrayList<>();
            for (int index : triIndices) {
                tempIndices.add(vertexIndices.get(index));
            }
            tempPolygon.setVertexIndices(tempIndices);

            // Копируем текстуры и нормали, если они есть
            if (!polygon.getTextureVertexIndices().isEmpty()) {
                ArrayList<Integer> tempTexIndices = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    tempTexIndices.add(polygon.getTextureVertexIndices().get(triIndices[j]));
                }
                tempPolygon.setTextureVertexIndices(tempTexIndices);
            }

            if (!polygon.getNormalIndices().isEmpty()) {
                ArrayList<Integer> tempNormIndices = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    tempNormIndices.add(polygon.getNormalIndices().get(triIndices[j]));
                }
                tempPolygon.setNormalIndices(tempNormIndices);
            }

            renderTriangle(graphicsContext, triVertices, triScreenPoints,
                    triWorldVertices, tempPolygon, mesh, width, height);
            trianglesRendered++;
        }
    }

    private static boolean isTriangleVisible(Vector2[] points, int width, int height) {
        // Проверяем, находится ли хоть одна точка в пределах экрана
        for (Vector2 point : points) {
            if (point.getX() >= 0 && point.getX() <= width && point.getY() >= 0 && point.getY() <= height) {
                return true;
            }
        }
        return false;
    }

    private static Color getTriangleColor(Polygon polygon, Model mesh, Vector3[] worldVertices) {
        Color color = fillColor;

        // Если есть текстура и она загружена
        if (useTexture && textureLoader.isLoaded() && !polygon.getTextureVertexIndices().isEmpty()) {
            color = getTextureColor(polygon, mesh);
        }

        // Если включено освещение
        if (useLighting) {
            color = applyLighting(polygon, mesh, worldVertices, color);
        }

        return color;
    }

    private static Color getTextureColor(Polygon polygon, Model mesh) {
        try {
            // Проверяем наличие текстурных координат
            if (polygon.getTextureVertexIndices().isEmpty()) {
                return fillColor;
            }

            // Берем текстурные координаты из СРЕДНЕЙ точки треугольника
            // (Позже нужно будет интерполировать для каждого пикселя)
            int texIndex = polygon.getTextureVertexIndices().get(0);
            if (texIndex >= 0 && texIndex < mesh.textureVertices.size()) {
                Vector2 texCoord = mesh.textureVertices.get(texIndex);
                return textureLoader.getColor(texCoord.getX(), texCoord.getY());
            } else {
                return fillColor;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return fillColor;
        }
    }

    private static Color applyLighting(Polygon polygon, Model mesh,
                                       Vector3[] worldVertices, Color baseColor) {
        try {
            // Если включено плавное затенение и у полигона есть нормали вершин
            if (smoothShading && !polygon.getNormalIndices().isEmpty()) {
                return applySmoothLighting(polygon, mesh, worldVertices, baseColor);
            } else {
                // Плоское затенение (используем нормаль треугольника)
                return applyFlatLighting(polygon, mesh, worldVertices, baseColor);
            }

        } catch (Exception e) {
            // Возвращаем цвет с минимальной освещенностью
            return new Color(
                    Math.min(baseColor.getRed() * 0.3, 1.0),
                    Math.min(baseColor.getGreen() * 0.3, 1.0),
                    Math.min(baseColor.getBlue() * 0.3, 1.0),
                    baseColor.getOpacity()
            );
        }
    }

    private static Color applyFlatLighting(Polygon polygon, Model mesh,
                                           Vector3[] worldVertices, Color baseColor) {
        // Получаем вершины треугольника
        Vector3 v0 = worldVertices[0];
        Vector3 v1 = worldVertices[1];
        Vector3 v2 = worldVertices[2];

        // Проверяем видимость треугольника
        if (!LightCalculator.isTriangleVisible(v0, v1, v2, cameraPosition)) {
            // Если треугольник не виден, возвращаем темный цвет
            return new Color(
                    Math.min(baseColor.getRed() * 0.1, 1.0),
                    Math.min(baseColor.getGreen() * 0.1, 1.0),
                    Math.min(baseColor.getBlue() * 0.1, 1.0),
                    baseColor.getOpacity()
            );
        }

        // Используем LightCalculator для расчета освещения
        return LightCalculator.calculateLightForTriangle(
                v0, v1, v2, lightPosition, cameraPosition, baseColor);
    }

    private static Color applySmoothLighting(Polygon polygon, Model mesh,
                                             Vector3[] worldVertices, Color baseColor) {
        try {
            // Для плавного затенения используем нормали вершин
            ArrayList<Integer> normalIndices = polygon.getNormalIndices();

            if (normalIndices.size() >= 3) {
                // Получаем нормали вершин в мировых координатах
                Vector3[] vertexNormals = new Vector3[3];
                for (int i = 0; i < 3; i++) {
                    int normalIndex = normalIndices.get(i);
                    if (normalIndex >= 0 && normalIndex < mesh.normals.size()) {
                        // Для простоты используем ту же нормаль
                        vertexNormals[i] = mesh.normals.get(normalIndex);
                    } else {
                        // Если нормали нет, используем нормаль треугольника
                        return applyFlatLighting(polygon, mesh, worldVertices, baseColor);
                    }
                }

                // Рассчитываем цвета для каждой вершины и усредняем
                Color[] vertexColors = new Color[3];
                for (int i = 0; i < 3; i++) {
                    vertexColors[i] = LightCalculator.calculateSimpleLight(
                            worldVertices[i], vertexNormals[i], lightPosition, cameraPosition, baseColor);
                }

                // Усредняем цвета вершин
                double r = (vertexColors[0].getRed() + vertexColors[1].getRed() + vertexColors[2].getRed()) / 3.0;
                double g = (vertexColors[0].getGreen() + vertexColors[1].getGreen() + vertexColors[2].getGreen()) / 3.0;
                double b = (vertexColors[0].getBlue() + vertexColors[1].getBlue() + vertexColors[2].getBlue()) / 3.0;

                return new Color(r, g, b, baseColor.getOpacity());
            }

        } catch (Exception e) {
        }

        // В случае ошибки используем плоское затенение
        return applyFlatLighting(polygon, mesh, worldVertices, baseColor);
    }

    private static void fillTriangleSimple(GraphicsContext gc, Vector2[] points, Color color) {
        // Преобразуем Vector2 в целочисленные координаты
        int x1 = (int) Math.round(points[0].getX());
        int y1 = (int) Math.round(points[0].getY());
        int x2 = (int) Math.round(points[1].getX());
        int y2 = (int) Math.round(points[1].getY());
        int x3 = (int) Math.round(points[2].getX());
        int y3 = (int) Math.round(points[2].getY());

        // Используем Rasterization для заливки треугольника
        Rasterization.fillTriangle(gc, x1, y1, x2, y2, x3, y3, color);
    }

    private static void fillTriangleZBuffer(GraphicsContext gc,
                                            Vector3[] vertices,
                                            Vector2[] screenPoints,
                                            Vector2[] textureCoords,  // ← ДОБАВЬТЕ ЭТОТ ПАРАМЕТР
                                            Color baseColor,
                                            Polygon polygon,
                                            Model mesh,
                                            int width, int height) {

        // Предварительная проверка: все ли вершины находятся перед камерой?
        // В NDC видимые точки имеют z в [-1, 1], но обычно видимые z < 1
        boolean allBehindCamera = true;
        for (Vector3 vertex : vertices) {
            if (vertex.getZ() < 1.0) { // z < 1 означает перед дальней плоскостью
                allBehindCamera = false;
                break;
            }
        }
        if (allBehindCamera) {
            return;
        }

        // Находим ограничивающий прямоугольник
        double minX = Math.max(0, Math.min(screenPoints[0].getX(),
                Math.min(screenPoints[1].getX(), screenPoints[2].getX())));
        double maxX = Math.min(zBuffer.getWidth() - 1, Math.max(screenPoints[0].getX(),
                Math.max(screenPoints[1].getX(), screenPoints[2].getX())));
        double minY = Math.max(0, Math.min(screenPoints[0].getY(),
                Math.min(screenPoints[1].getY(), screenPoints[2].getY())));
        double maxY = Math.min(zBuffer.getHeight() - 1, Math.max(screenPoints[0].getY(),
                Math.max(screenPoints[1].getY(), screenPoints[2].getY())));

        if (minX > maxX || minY > maxY) {
            return;
        }

        // Вычисляем площадь треугольника
        double area = edgeFunction(screenPoints[0], screenPoints[1], screenPoints[2]);
        if (Math.abs(area) < 1e-10) {
            return;
        }

        PixelWriter pixelWriter = gc.getPixelWriter();
        double invArea = 1.0 / area;

        for (int y = (int)minY; y <= maxY; y++) {
            for (int x = (int)minX; x <= maxX; x++) {
                double w0 = edgeFunction(screenPoints[1], screenPoints[2], x, y);
                double w1 = edgeFunction(screenPoints[2], screenPoints[0], x, y);
                double w2 = edgeFunction(screenPoints[0], screenPoints[1], x, y);

                if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                    w0 /= area;
                    w1 /= area;
                    w2 /= area;

                    double z = w0 * vertices[0].getZ() + w1 * vertices[1].getZ() + w2 * vertices[2].getZ();

                    if (z >= -1.0 && z <= 1.0) {
                        if (zBuffer.testAndSet(x, y, z)) {
                            // ИНТЕРПОЛЯЦИЯ ТЕКСТУРНЫХ КООРДИНАТ!
                            Color pixelColor;
                            if (useTexture && textureLoader.isLoaded() && textureCoords != null) {
                                // Интерполируем UV-координаты
                                double u = w0 * textureCoords[0].getX() +
                                        w1 * textureCoords[1].getX() +
                                        w2 * textureCoords[2].getX();
                                double v = w0 * textureCoords[0].getY() +
                                        w1 * textureCoords[1].getY() +
                                        w2 * textureCoords[2].getY();
                                pixelColor = textureLoader.getColor(u, v);
                            } else {
                                pixelColor = baseColor;
                            }

                            pixelWriter.setColor(x, y, pixelColor);
                            pixelsRendered++;
                        }
                    }
                }
            }
        }
    }

    private static double edgeFunction(Vector2 a, Vector2 b, Vector2 c) {
        return (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());
    }

    private static double edgeFunction(Vector2 a, Vector2 b, int px, int py) {
        return (b.getX() - a.getX()) * (py - a.getY()) - (b.getY() - a.getY()) * (px - a.getX());
    }

    private static void drawWireframe(GraphicsContext gc, Vector2[] points) {
        // Рисуем линии между вершинами с помощью Rasterization
        for (int i = 0; i < points.length; i++) {
            int next = (i + 1) % points.length;

            int x1 = (int) Math.round(points[i].getX());
            int y1 = (int) Math.round(points[i].getY());
            int x2 = (int) Math.round(points[next].getX());
            int y2 = (int) Math.round(points[next].getY());

            Rasterization.drawLine(gc, x1, y1, x2, y2, Color.BLACK);
        }
    }

    public static void renderCameraModel(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model cameraModel,
            final Camera activeCamera,
            final int width,
            final int height) {

        // Не рендерим активную камеру
        if (camera == activeCamera) {
            return;
        }

        Color cameraColor = Color.RED;

        // Получаем матрицы преобразования
        Matrix4 modelMatrix = GraphicConveyor.rotateScaleTranslate();
        Matrix4 viewMatrix = activeCamera.getViewMatrix();
        Matrix4 projectionMatrix = activeCamera.getProjectionMatrix();

        Matrix4 modelViewProjectionMatrix = modelMatrix.mul(viewMatrix).mul(projectionMatrix);

        // Рендерим модель камеры
        for (Polygon polygon : cameraModel.polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

            ArrayList<Vector2> resultPoints = new ArrayList<>();
            for (Integer vertexIndex : vertexIndices) {
                Vector3 vertex = cameraModel.vertices.get(vertexIndex);
                Vector3 transformed = GraphicConveyor.multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertex);
                Vector2 resultPoint = GraphicConveyor.vertexToPoint(transformed, width, height);
                resultPoints.add(resultPoint);
            }

            // Рисуем полигональную сетку для модели камеры
            graphicsContext.setStroke(cameraColor);
            graphicsContext.setLineWidth(2);

            for (int i = 0; i < resultPoints.size(); i++) {
                int next = (i + 1) % resultPoints.size();
                graphicsContext.strokeLine(
                        resultPoints.get(i).getX(), resultPoints.get(i).getY(),
                        resultPoints.get(next).getX(), resultPoints.get(next).getY()
                );
            }
        }
    }

    private static boolean isPointInFrustum(Vector3 point) {
        // NDC: x,y,z в диапазоне [-1, 1] для видимых точек
        return point.getX() >= -1 && point.getX() <= 1 &&
                point.getY() >= -1 && point.getY() <= 1 &&
                point.getZ() >= -1 && point.getZ() <= 1;
    }

    private static boolean isTriangleInFrustum(Vector3[] vertices) {
        // Если все три вершины вне frustum, треугольник невидим
        // Но если хоть одна внутри - рисуем (можно улучшить до точного отсечения)
        for (Vector3 vertex : vertices) {
            if (isPointInFrustum(vertex)) {
                return true;
            }
        }
        return false;
    }
}