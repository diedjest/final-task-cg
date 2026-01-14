package ru.vsu.cs.finaltaskcg.normals;

import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;

import java.util.ArrayList;

public class NormalCalculator {

    public static void calculateVertexNormals(Model model) {

        model.normals.clear();

        for (int i = 0; i < model.vertices.size(); i++) {
            model.normals.add(new Vector3(0, 0, 0));
        }

        for (Polygon polygon : model.polygons) {

            if (polygon.getVertexIndices().size() < 3) continue;

            Vector3 v0 = model.vertices.get(polygon.getVertexIndices().get(0));
            Vector3 v1 = model.vertices.get(polygon.getVertexIndices().get(1));
            Vector3 v2 = model.vertices.get(polygon.getVertexIndices().get(2));

            Vector3 normal;
            try {
                normal = v1.sub(v0)
                        .cross(v2.sub(v0))
                        .normalize();
            } catch (ArithmeticException e) {
                continue;
            }

            for (int index : polygon.getVertexIndices()) {
                model.normals.set(
                        index,
                        model.normals.get(index).add(normal)
                );
            }
        }

        for (int i = 0; i < model.normals.size(); i++) {
            Vector3 n = model.normals.get(i);
            if (n.length() == 0) continue;
            model.normals.set(i, n.normalize());
        }

        for (Polygon polygon : model.polygons) {
            polygon.getNormalIndices().clear();
            ArrayList<Integer> newNormalIndices = new ArrayList<>();
            for (int vertexIndex : polygon.getVertexIndices()) {
                newNormalIndices.add(vertexIndex);
            }
            polygon.setNormalIndices(newNormalIndices);
        }
    }
}