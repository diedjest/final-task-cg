package ru.vsu.cs.finaltaskcg.objwriter;

import ru.vsu.cs.finaltaskcg.math.vector.Vector2;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ObjWriter {

    public static void write(Model model, String filePath) throws IOException {
        Path path = Paths.get(filePath);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("# Model exported from 3D Viewer\n");

            for (Vector3 vertex : model.vertices) {
                writer.write(String.format("v %.6f %.6f %.6f\n",
                        vertex.getX(), vertex.getY(), vertex.getZ()));
            }

            if (!model.textureVertices.isEmpty()) {
                writer.write("\n");
                for (Vector2 texCoord : model.textureVertices) {
                    writer.write(String.format("vt %.6f %.6f\n",
                            texCoord.getX(), texCoord.getY()));
                }
            }

            if (!model.normals.isEmpty()) {
                writer.write("\n");
                for (Vector3 normal : model.normals) {
                    writer.write(String.format("vn %.6f %.6f %.6f\n",
                            normal.getX(), normal.getY(), normal.getZ()));
                }
            }

            if (!model.polygons.isEmpty()) {
                writer.write("\n");
                for (Polygon polygon : model.polygons) {
                    writer.write("f ");
                    List<Integer> vertexIndices = polygon.getVertexIndices();
                    List<Integer> texIndices = polygon.getTextureVertexIndices();
                    List<Integer> normalIndices = polygon.getNormalIndices();

                    for (int i = 0; i < vertexIndices.size(); i++) {
                        if (i > 0) writer.write(" ");

                        int vIndex = vertexIndices.get(i) + 1;

                        if (!texIndices.isEmpty() && !normalIndices.isEmpty()) {
                            int vtIndex = texIndices.get(i) + 1;
                            int vnIndex = normalIndices.get(i) + 1;
                            writer.write(String.format("%d/%d/%d", vIndex, vtIndex, vnIndex));
                        } else if (!texIndices.isEmpty()) {
                            int vtIndex = texIndices.get(i) + 1;
                            writer.write(String.format("%d/%d", vIndex, vtIndex));
                        } else if (!normalIndices.isEmpty()) {
                            int vnIndex = normalIndices.get(i) + 1;
                            writer.write(String.format("%d//%d", vIndex, vnIndex));
                        } else {
                            writer.write(String.valueOf(vIndex));
                        }
                    }
                    writer.write("\n");
                }
            }
        }
    }
}