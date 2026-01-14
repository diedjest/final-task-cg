package ru.vsu.cs.finaltaskcg.normals;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;

public class NormalCalculator {

    public static void calculateVertexNormals(Model model) {

        model.normals.clear();

        for (int i = 0; i < model.vertices.size(); i++) {
            model.normals.add(new Vector3f(0, 0, 0));
        }

        for (Polygon polygon : model.polygons) {

            if (polygon.getVertexIndices().size() < 3) continue;

            Vector3f v0 = model.vertices.get(polygon.getVertexIndices().get(0));
            Vector3f v1 = model.vertices.get(polygon.getVertexIndices().get(1));
            Vector3f v2 = model.vertices.get(polygon.getVertexIndices().get(2));

            Vector3f normal;
            try {
                normal = v1.subtract(v0)
                        .cross(v2.subtract(v0))
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
            Vector3f n = model.normals.get(i);
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