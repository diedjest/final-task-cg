package com.cgvsu.editor;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.HashSet;
import java.util.Set;

public class PolygonRemover {

    public static void removePolygonWithCleanup(Model model, int polygonIndex) {
        if (model == null || polygonIndex < 0 || polygonIndex >= model.getPolygons().size()) {
            return;
        }

        Polygon polygon = model.getPolygons().get(polygonIndex);
        Set<Integer> verticesInPolygon = new HashSet<>(polygon.getVertexIndices());

        model.getPolygons().remove(polygonIndex);

        Set<Integer> usedVertices = new HashSet<>();
        for (Polygon p : model.getPolygons()) {
            usedVertices.addAll(p.getVertexIndices());
        }

        Set<Integer> unusedVertices = new HashSet<>();
        for (Integer vertexIndex : verticesInPolygon) {
            if (!usedVertices.contains(vertexIndex)) {
                unusedVertices.add(vertexIndex);
            }
        }

        ModelEditor.removeSelectedVertices(model, unusedVertices);
    }
}