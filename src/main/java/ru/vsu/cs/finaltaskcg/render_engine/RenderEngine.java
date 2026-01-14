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

    // Текущие настройки
    private static Color fillColor = Color.LIGHTGRAY;
    private static TextureLoader textureLoader = new TextureLoader();
    private static Vector3 lightPosition = new Vector3(0, 100, 100);
    private static Color ambientColor = Color.rgb(50, 50, 50);
    private static Color diffuseColor = Color.rgb(200, 200, 200);

    // Z-буфер
    private static ZBuffer zBuffer;

    // Вспомогательные классы
    private static Triangulator triangulator = new Triangulator();

    // Статистика
    private static int trianglesRendered = 0;
    private static int pixelsRendered = 0;

    // Кэш для преобразованных вершин
    private static Map<Integer, Vector3> transformedVerticesCache = new HashMap<>();

    public static void setRenderMode(boolean wireframe, boolean fill,
                                     boolean texture, boolean lighting,
                                     boolean zBufferMode) {
        drawWireframe = wireframe;
        fillTriangles = fill;
        useTexture = texture;
        useLighting = lighting;
        useZBuffer = zBufferMode;

        System.out.println("Render mode set: Wireframe=" + wireframe +
                ", Fill=" + fill + ", Texture=" + texture +
                ", Lighting=" + lighting + ", ZBuffer=" + zBufferMode);
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

        // Проверка входных данных
        if (mesh == null || mesh.vertices.isEmpty() || mesh.polygons.isEmpty()) {
            System.out.println("RenderEngine: Mesh is empty");
            return;
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
        final int nPolygons = mesh.polygons.size();
        trianglesRendered = 0;

        for (int polygonIndex = 0; polygonIndex < nPolygons; polygonIndex++) {
            Polygon polygon = mesh.polygons.get(polygonIndex);
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
                    Vector3 originalVertex = mesh.vertices.get(vertexIndex);
                    vertex = GraphicConveyor.multiplyMatrix4ByVector3(modelViewProjectionMatrix, originalVertex);
                    transformedVerticesCache.put(vertexIndex, vertex);
                }

                vertices[i] = vertex;
                screenPoints[i] = GraphicConveyor.vertexToPoint(vertex, width, height);

                // Сохраняем мировые координаты для освещения
                worldVertices[i] = GraphicConveyor.multiplyMatrix4ByVector3(modelViewMatrix, mesh.vertices.get(vertexIndex));
            }

            // Триангулируем полигон, если нужно (на случай, если полигон не треугольник)
            if (vertexIndices.size() > 3) {
                renderTriangulatedPolygon(graphicsContext, polygon, mesh,
                        modelViewProjectionMatrix, modelViewMatrix,
                        width, height);
            } else {
                // Рендерим треугольник
                renderTriangle(graphicsContext, vertices, screenPoints, worldVertices,
                        polygon, mesh, width, height);
                trianglesRendered++;
            }
        }

        // Рисуем статистику
        if (drawWireframe) {
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillText("Triangles: " + trianglesRendered, 10, 20);
            graphicsContext.fillText("Z-Buffer: " + (useZBuffer ? "ON" : "OFF"), 10, 40);
            graphicsContext.fillText("Lighting: " + (useLighting ? "ON" : "OFF"), 10, 60);
            graphicsContext.fillText("Texture: " + (useTexture && textureLoader.isLoaded() ? "ON" : "OFF"), 10, 80);
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

        // Проверяем, находится ли треугольник в поле зрения
        if (!isTriangleVisible(screenPoints, width, height)) {
            return;
        }

        // Заливка треугольника (если включена)
        if (fillTriangles) {
            Color triangleColor = getTriangleColor(polygon, mesh, worldVertices);

            if (useZBuffer) {
                fillTriangleZBuffer(graphicsContext, vertices, screenPoints, triangleColor);
            } else {
                fillTriangleSimple(graphicsContext, screenPoints, triangleColor);
            }
        }

        // Отрисовка полигональной сетки (если включена)
        if (drawWireframe) {
            drawWireframe(graphicsContext, screenPoints);
        }
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

        // Триангуляция веером
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
        if (useLighting && !polygon.getNormalIndices().isEmpty()) {
            color = applyLighting(polygon, mesh, worldVertices, color);
        }

        return color;
    }

    private static Color getTextureColor(Polygon polygon, Model mesh) {
        try {
            // Простая реализация: берем цвет из первой текстуры вершины
            if (!polygon.getTextureVertexIndices().isEmpty()) {
                int texIndex = polygon.getTextureVertexIndices().get(0);
                if (texIndex >= 0 && texIndex < mesh.textureVertices.size()) {
                    Vector2 texCoord = mesh.textureVertices.get(texIndex);
                    return textureLoader.getColor((float) texCoord.getX(), (float) texCoord.getY());
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting texture color: " + e.getMessage());
        }

        return fillColor;
    }

    private static Color applyLighting(Polygon polygon, Model mesh,
                                       Vector3[] worldVertices, Color baseColor) {
        try {
            // Вычисляем нормаль треугольника
            Vector3 v0 = worldVertices[0];
            Vector3 v1 = worldVertices[1];
            Vector3 v2 = worldVertices[2];

            Vector3 edge1 = v1.sub(v0);
            Vector3 edge2 = v2.sub(v0);
            Vector3 normal = edge1.cross(edge2).normalize();

            // Вектор от треугольника к источнику света
            Vector3 lightDir = lightPosition.sub(v0).normalize();

            // Диффузное освещение (косинус угла между нормалью и направлением света)
            double diff = Math.max(normal.dot(lightDir), 0.0f);

            // Фоновое освещение
            float ambient = 0.2f;

            // Итоговая интенсивность
            double intensity = ambient + diff * 0.8f;
            intensity = Math.min(intensity, 1.0f);

            // Применяем освещение к цвету
            return new Color(
                    Math.min(baseColor.getRed() * intensity, 1.0),
                    Math.min(baseColor.getGreen() * intensity, 1.0),
                    Math.min(baseColor.getBlue() * intensity, 1.0),
                    baseColor.getOpacity()
            );

        } catch (Exception e) {
            System.err.println("Error applying lighting: " + e.getMessage());
            return baseColor;
        }
    }

    private static void fillTriangleSimple(GraphicsContext gc, Vector2[] points, Color color) {
        double[] xPoints = {points[0].getX(), points[1].getX(), points[2].getX()};
        double[] yPoints = {points[0].getY(), points[1].getY(), points[2].getY()};

        gc.setFill(color);
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    private static void fillTriangleZBuffer(GraphicsContext gc,
                                            Vector3[] vertices,
                                            Vector2[] screenPoints,
                                            Color color) {

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

        if (area == 0) {
            return;
        }

        PixelWriter pixelWriter = gc.getPixelWriter();

        // Проходим по всем пикселям в ограничивающем прямоугольнике
        for (int y = (int)minY; y <= maxY; y++) {
            for (int x = (int)minX; x <= maxX; x++) {
                // Вычисляем барицентрические координаты
                double w0 = edgeFunction(screenPoints[1], screenPoints[2], x, y);
                double w1 = edgeFunction(screenPoints[2], screenPoints[0], x, y);
                double w2 = edgeFunction(screenPoints[0], screenPoints[1], x, y);

                // Если точка внутри треугольника
                if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                    // Нормализуем барицентрические координаты
                    w0 /= area;
                    w1 /= area;
                    w2 /= area;

                    // Интерполируем Z-координату
                    double z = w0 * vertices[0].getZ() + w1 * vertices[1].getZ() + w2 * vertices[2].getZ();

                    // Проверяем Z-буфер
                    if (zBuffer.testAndSet(x, y, z)) {
                        pixelWriter.setColor(x, y, color);
                        pixelsRendered++;
                    }
                }
            }
        }
    }

    private static double edgeFunction(Vector2 a, Vector2 b, Vector2 c) {
        return (b.getX() - a.getX()) * (c.getY() - a.getY() - (b.getY() - a.getY() * (c.getX() - a.getX())));
    }

    private static double edgeFunction(Vector2 a, Vector2 b, int px, int py) {
        return (b.getX() - a.getX()) * (py - a.getY() - (b.getY() - a.getY() * (px - a.getX())));
    }

    private static void drawWireframe(GraphicsContext gc, Vector2[] points) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        // Рисуем линии между вершинами
        for (int i = 0; i < points.length; i++) {
            int next = (i + 1) % points.length;
            gc.strokeLine(
                    points[i].getX(), points[i].getY(),
                    points[next].getX(), points[next].getY()
            );
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

    // Вспомогательный класс для триангуляции
    private static class Triangulator {
        public void triangulate(Model model) {
            ArrayList<Polygon> triangulatedPolygons = new ArrayList<>();

            for (Polygon polygon : model.polygons) {
                ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

                // Если полигон уже треугольник - оставляем как есть
                if (vertexIndices.size() == 3) {
                    triangulatedPolygons.add(polygon);
                    continue;
                }

                // Триангуляция веером от первой вершины
                for (int i = 1; i < vertexIndices.size() - 1; i++) {
                    Polygon triangle = new Polygon();

                    ArrayList<Integer> triVertices = new ArrayList<>();
                    triVertices.add(vertexIndices.get(0));
                    triVertices.add(vertexIndices.get(i));
                    triVertices.add(vertexIndices.get(i + 1));
                    triangle.setVertexIndices(triVertices);

                    // Копируем текстуры и нормали, если они есть
                    if (!polygon.getTextureVertexIndices().isEmpty()) {
                        ArrayList<Integer> triTextures = new ArrayList<>();
                        triTextures.add(polygon.getTextureVertexIndices().get(0));
                        triTextures.add(polygon.getTextureVertexIndices().get(i));
                        triTextures.add(polygon.getTextureVertexIndices().get(i + 1));
                        triangle.setTextureVertexIndices(triTextures);
                    }

                    if (!polygon.getNormalIndices().isEmpty()) {
                        ArrayList<Integer> triNormals = new ArrayList<>();
                        triNormals.add(polygon.getNormalIndices().get(0));
                        triNormals.add(polygon.getNormalIndices().get(i));
                        triNormals.add(polygon.getNormalIndices().get(i + 1));
                        triangle.setNormalIndices(triNormals);
                    }

                    triangulatedPolygons.add(triangle);
                }
            }

            model.polygons = triangulatedPolygons;
        }
    }
}