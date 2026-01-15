package com.cgvsu.objwriter;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class ObjWriter {

    public static void write(Model model, String filePath) throws IOException {
        if (!isValidModel(model)) {
            throw new IllegalArgumentException("Неверная модель");
        }

        StringBuilder content = new StringBuilder();

        writeVertices(model, content);

        if (model.getTextureVertices() != null && !model.getTextureVertices().isEmpty()) {
            writeTextureVertices(model, content);
        }

        if (model.getNormals() != null && !model.getNormals().isEmpty()) {
            writeNormals(model, content);
        }

        writePolygons(model, content);

        Path path = Paths.get(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(
                path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            writer.write(content.toString());
        }
    }

    private static void writeVertices(Model model, StringBuilder content) {
        ArrayList<Vector3f> vertices = model.getVertices();
        for (Vector3f vertex : vertices) {
            content.append(String.format("v %.6f %.6f %.6f%n",
                    vertex.getX(), vertex.getY(), vertex.getZ()));
        }
        if (!vertices.isEmpty()) {
            content.append("%n");
        }
    }

    private static void writeTextureVertices(Model model, StringBuilder content) {
        ArrayList<Vector2f> textureVertices = model.getTextureVertices();
        for (Vector2f texVertex : textureVertices) {
            content.append(String.format("vt %.6f %.6f%n",
                    texVertex.getX(), texVertex.getY()));
        }
        if (!textureVertices.isEmpty()) {
            content.append("%n");
        }
    }

    private static void writeNormals(Model model, StringBuilder content) {
        ArrayList<Vector3f> normals = model.getNormals();
        for (Vector3f normal : normals) {
            content.append(String.format("vn %.6f %.6f %.6f%n",
                    normal.getX(), normal.getY(), normal.getZ()));
        }
        if (!normals.isEmpty()) {
            content.append("%n");
        }
    }

    private static void writePolygons(Model model, StringBuilder content) {
        ArrayList<Polygon> polygons = model.getPolygons();
        for (Polygon polygon : polygons) {
            content.append("f");

            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> textureIndices = polygon.getTextureVertexIndices();
            ArrayList<Integer> normalIndices = polygon.getNormalIndices();

            for (int i = 0; i < vertexIndices.size(); i++) {
                content.append(" ");

                int vIdx = vertexIndices.get(i) + 1;

                Integer vtIdx = null;
                if (textureIndices != null && i < textureIndices.size()) {
                    vtIdx = textureIndices.get(i) + 1;
                }

                Integer vnIdx = null;
                if (normalIndices != null && i < normalIndices.size()) {
                    vnIdx = normalIndices.get(i) + 1;
                }

                if (vtIdx != null && vnIdx != null) {
                    content.append(String.format("%d/%d/%d", vIdx, vtIdx, vnIdx));
                } else if (vtIdx != null) {
                    content.append(String.format("%d/%d", vIdx, vtIdx));
                } else if (vnIdx != null) {
                    content.append(String.format("%d//%d", vIdx, vnIdx));
                } else {
                    content.append(vIdx);
                }
            }
            content.append("%n");
        }
    }

    private static boolean isValidModel(Model model) {
        if (model == null) {
            return false;
        }

        if (model.getVertices() == null) {
            return false;
        }

        if (model.getPolygons() == null) {
            return false;
        }

        int vertexCount = model.getVertices().size();
        for (int i = 0; i < model.getPolygons().size(); i++) {
            Polygon polygon = model.getPolygons().get(i);
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();

            if (vertexIndices == null || vertexIndices.size() < 3) {
                return false;
            }

            for (int vertexIndex : vertexIndices) {
                if (vertexIndex < 0 || vertexIndex >= vertexCount) {
                    return false;
                }
            }
        }

        return true;
    }
}