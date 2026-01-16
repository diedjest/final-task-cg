package ru.vsu.cs.finaltaskcg.render_engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import ru.vsu.cs.finaltaskcg.math.Vector3f;
import ru.vsu.cs.finaltaskcg.math.Vector2f;
import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;
import ru.vsu.cs.finaltaskcg.lighting.LightCalculator;
import ru.vsu.cs.finaltaskcg.render_engine.TextureLoader;
import java.util.ArrayList;

public class RenderEngine {

    private static boolean drawWireframe = true;
    private static boolean fillTriangles = true;
    private static boolean useLighting = true;
    private static boolean useTexture = false;
    private static boolean useZBuffer = false;
    private static Color fillColor = Color.LIGHTGRAY;

    private static Vector3 lightPosition = new Vector3(0, 100, 100);

    private static TextureLoader textureLoader = new TextureLoader();
    private static ImagePattern texturePattern = null;

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
        boolean loaded = textureLoader.loadTexture(filePath);
        if (loaded) {
            Image textureImage = getTextureImage();
            if (textureImage != null) {
                texturePattern = new ImagePattern(textureImage);
            }
        }
        return loaded;
    }

    public static boolean isTextureLoaded() {
        return textureLoader.isLoaded();
    }

    private static Image getTextureImage() {
        try {
            java.lang.reflect.Field field = textureLoader.getClass().getDeclaredField("textureImage");
            field.setAccessible(true);
            return (Image) field.get(textureLoader);
        } catch (Exception e) {
            return null;
        }
    }

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) {

        if (mesh == null || mesh.getVertices().isEmpty()) {
            return;
        }

        graphicsContext.clearRect(0, 0, width, height);

        Matrix4 viewMatrix = camera.getViewMatrix();
        Matrix4 projectionMatrix = camera.getProjectionMatrix();
        Matrix4 mvpMatrix = viewMatrix.mul(projectionMatrix);

        Matrix4 modelViewMatrix = viewMatrix;

        for (Polygon polygon : mesh.getPolygons()) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

            if (vertexIndices.size() < 3) continue;

            ArrayList<Vector2> screenPoints = new ArrayList<>();
            ArrayList<Vector3> worldVertices = new ArrayList<>();
            ArrayList<Vector3> normals = new ArrayList<>();
            ArrayList<Vector2> textureCoords = new ArrayList<>();

            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i);
                Vector3f vertex = mesh.getVertices().get(vertexIndex);
                Vector3 vec3 = new Vector3(vertex.x, vertex.y, vertex.z);
                Vector3 transformed = GraphicConveyor.multiplyMatrix4ByVector3(mvpMatrix, vec3);
                Vector2 screenPoint = GraphicConveyor.vertexToPoint(transformed, width, height);
                screenPoints.add(screenPoint);

                Vector3 worldPos = GraphicConveyor.multiplyMatrix4ByVector3(modelViewMatrix, vec3);
                worldVertices.add(worldPos);

                if (useLighting && !mesh.getNormals().isEmpty() &&
                        i < polygon.getNormalIndices().size()) {
                    int normalIndex = polygon.getNormalIndices().get(i);
                    if (normalIndex >= 0 && normalIndex < mesh.getNormals().size()) {
                        Vector3f normalVec = mesh.getNormals().get(normalIndex);
                        normals.add(new Vector3(normalVec.x, normalVec.y, normalVec.z));
                    }
                }

                if (useTexture && textureLoader.isLoaded() &&
                        i < polygon.getTextureVertexIndices().size()) {
                    int texIndex = polygon.getTextureVertexIndices().get(i);
                    if (texIndex >= 0 && texIndex < mesh.getTextureVertices().size()) {
                        Vector2f texCoord = mesh.getTextureVertices().get(texIndex);
                        textureCoords.add(new Vector2(texCoord.x, texCoord.y));
                    }
                }
            }

            if (fillTriangles) {
                drawTriangleWithTexture(graphicsContext, screenPoints, textureCoords, polygon, mesh, worldVertices, normals);
            }

            if (drawWireframe) {
                drawWireframe(graphicsContext, screenPoints);
            }
        }
    }

    private static void drawTriangleWithTexture(GraphicsContext gc, ArrayList<Vector2> screenPoints,
                                                ArrayList<Vector2> textureCoords, Polygon polygon,
                                                Model mesh, ArrayList<Vector3> worldVertices,
                                                ArrayList<Vector3> normals) {

        if (screenPoints.size() < 3) return;

        double[] xPoints = new double[screenPoints.size()];
        double[] yPoints = new double[screenPoints.size()];

        for (int i = 0; i < screenPoints.size(); i++) {
            xPoints[i] = screenPoints.get(i).getX();
            yPoints[i] = screenPoints.get(i).getY();
        }

        Color color = fillColor;

        if (useTexture && textureLoader.isLoaded() && texturePattern != null &&
                !textureCoords.isEmpty()) {
            gc.setFill(texturePattern);
        } else {
            if (useLighting && !normals.isEmpty() && !worldVertices.isEmpty()) {
                color = applyLighting(polygon, mesh, worldVertices, normals, color);
            }
            gc.setFill(color);
        }

        gc.fillPolygon(xPoints, yPoints, screenPoints.size());
    }

    private static Color applyLighting(Polygon polygon, Model mesh,
                                       ArrayList<Vector3> worldVertices,
                                       ArrayList<Vector3> normals,
                                       Color baseColor) {
        try {
            if (worldVertices.isEmpty() || normals.isEmpty()) {
                return baseColor;
            }

            Vector3 vertex = worldVertices.get(0);
            Vector3 normal = normals.get(0);

            return LightCalculator.calculateLight(vertex, normal, lightPosition, baseColor);

        } catch (Exception e) {
            System.err.println("Error applying lighting: " + e.getMessage());
            return baseColor;
        }
    }

    private static void drawWireframe(GraphicsContext gc, ArrayList<Vector2> points) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        for (int i = 0; i < points.size(); i++) {
            int next = (i + 1) % points.size();
            gc.strokeLine(
                    points.get(i).getX(),
                    points.get(i).getY(),
                    points.get(next).getX(),
                    points.get(next).getY()
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

        if (camera == activeCamera) {
            return;
        }

        Color cameraColor = Color.RED;

        Matrix4 viewMatrix = activeCamera.getViewMatrix();
        Matrix4 projectionMatrix = activeCamera.getProjectionMatrix();
        Matrix4 mvpMatrix = viewMatrix.mul(projectionMatrix);

        for (Polygon polygon : cameraModel.getPolygons()) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

            ArrayList<Vector2> resultPoints = new ArrayList<>();
            for (Integer vertexIndex : vertexIndices) {
                Vector3f vertex = cameraModel.getVertices().get(vertexIndex);
                Vector3 vec3 = new Vector3(vertex.x, vertex.y, vertex.z);
                Vector3 transformed = GraphicConveyor.multiplyMatrix4ByVector3(mvpMatrix, vec3);
                Vector2 resultPoint = GraphicConveyor.vertexToPoint(transformed, width, height);
                resultPoints.add(resultPoint);
            }

            graphicsContext.setStroke(cameraColor);
            graphicsContext.setLineWidth(2);

            for (int i = 0; i < resultPoints.size(); i++) {
                int next = (i + 1) % resultPoints.size();
                graphicsContext.strokeLine(
                        resultPoints.get(i).getX(),
                        resultPoints.get(i).getY(),
                        resultPoints.get(next).getX(),
                        resultPoints.get(next).getY()
                );
            }
        }
    }
}