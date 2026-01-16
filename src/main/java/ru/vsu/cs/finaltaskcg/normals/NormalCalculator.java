package ru.vsu.cs.finaltaskcg.normals;

import ru.vsu.cs.finaltaskcg.math.Vector3f;
import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;
import java.util.ArrayList;

public class NormalCalculator {
    public static void calculateVertexNormals(Model model) {
        model.getNormals().clear();

        for (int i = 0; i < model.getVertices().size(); i++) {
            model.getNormals().add(new Vector3f(0, 0, 0));
        }

        for (Polygon polygon : model.getPolygons()) {
            if (polygon.getVertexIndices().size() < 3) continue;

            Vector3f v0 = model.getVertices().get(polygon.getVertexIndices().get(0));
            Vector3f v1 = model.getVertices().get(polygon.getVertexIndices().get(1));
            Vector3f v2 = model.getVertices().get(polygon.getVertexIndices().get(2));

            Vector3f normal = calculateTriangleNormal(v0, v1, v2);

            for (int index : polygon.getVertexIndices()) {
                Vector3f currentNormal = model.getNormals().get(index);
                Vector3f newNormal = new Vector3f(
                        currentNormal.x + normal.x,
                        currentNormal.y + normal.y,
                        currentNormal.z + normal.z
                );
                model.getNormals().set(index, newNormal);
            }
        }

        for (int i = 0; i < model.getNormals().size(); i++) {
            Vector3f n = model.getNormals().get(i);
            float length = (float)Math.sqrt(n.x * n.x + n.y * n.y + n.z * n.z);
            if (length > 0.0001f) {
                model.getNormals().set(i, new Vector3f(
                        n.x / length,
                        n.y / length,
                        n.z / length
                ));
            }
        }
    }

    private static Vector3f calculateTriangleNormal(Vector3f v0, Vector3f v1, Vector3f v2) {
        float edge1X = v1.x - v0.x;
        float edge1Y = v1.y - v0.y;
        float edge1Z = v1.z - v0.z;

        float edge2X = v2.x - v0.x;
        float edge2Y = v2.y - v0.y;
        float edge2Z = v2.z - v0.z;

        float nx = edge1Y * edge2Z - edge1Z * edge2Y;
        float ny = edge1Z * edge2X - edge1X * edge2Z;
        float nz = edge1X * edge2Y - edge1Y * edge2X;

        float length = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length > 0.0001f) {
            nx /= length;
            ny /= length;
            nz /= length;
        }

        return new Vector3f(nx, ny, nz);
    }
}