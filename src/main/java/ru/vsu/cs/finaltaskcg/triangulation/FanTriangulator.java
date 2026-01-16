package ru.vsu.cs.finaltaskcg.triangulation;

import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;

import java.util.ArrayList;

/**
 * Простая веерная триангуляция - разбивает полигоны на треугольники от первой вершины
 */
public class FanTriangulator implements Triangulator {

    @Override
    public void triangulate(Model model) {
        if (model == null || model.polygons.isEmpty()) {
            return;
        }

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

                // Копируем текстуры, если они есть
                if (!polygon.getTextureVertexIndices().isEmpty() &&
                        polygon.getTextureVertexIndices().size() == vertexIndices.size()) {
                    ArrayList<Integer> triTextures = new ArrayList<>();
                    triTextures.add(polygon.getTextureVertexIndices().get(0));
                    triTextures.add(polygon.getTextureVertexIndices().get(i));
                    triTextures.add(polygon.getTextureVertexIndices().get(i + 1));
                    triangle.setTextureVertexIndices(triTextures);
                }

                // Копируем нормали, если они есть
                if (!polygon.getNormalIndices().isEmpty() &&
                        polygon.getNormalIndices().size() == vertexIndices.size()) {
                    ArrayList<Integer> triNormals = new ArrayList<>();
                    triNormals.add(polygon.getNormalIndices().get(0));
                    triNormals.add(polygon.getNormalIndices().get(i));
                    triNormals.add(polygon.getNormalIndices().get(i + 1));
                    triangle.setNormalIndices(triNormals);
                }

                triangulatedPolygons.add(triangle);
            }
        }

        // Заменяем полигоны на триангулированные
        model.polygons = triangulatedPolygons;
    }
}