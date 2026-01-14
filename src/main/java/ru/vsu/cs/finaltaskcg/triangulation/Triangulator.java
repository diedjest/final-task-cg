package ru.vsu.cs.finaltaskcg.triangulation;



import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;

import java.util.ArrayList;

public class Triangulator {

    public static void triangulate(Model model) {
        ArrayList<Polygon> triangulatedPolygons = new ArrayList<>();

        for (Polygon polygon : model.polygons) {
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> textureIndices = polygon.getTextureVertexIndices();
            ArrayList<Integer> normalIndices = polygon.getNormalIndices();

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

                if (!textureIndices.isEmpty()) {
                    ArrayList<Integer> triTextures = new ArrayList<>();
                    triTextures.add(textureIndices.get(0));
                    triTextures.add(textureIndices.get(i));
                    triTextures.add(textureIndices.get(i + 1));
                    triangle.setTextureVertexIndices(triTextures);
                }

                if (!normalIndices.isEmpty()) {
                    ArrayList<Integer> triNormals = new ArrayList<>();
                    triNormals.add(normalIndices.get(0));
                    triNormals.add(normalIndices.get(i));
                    triNormals.add(normalIndices.get(i + 1));
                    triangle.setNormalIndices(triNormals);
                }

                triangulatedPolygons.add(triangle);
            }
        }

        model.polygons = triangulatedPolygons;
    }
}