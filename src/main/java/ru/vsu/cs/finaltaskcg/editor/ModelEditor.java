package ru.vsu.cs.finaltaskcg.editor;

import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;

import java.util.ArrayList;
import java.util.Set;

public class ModelEditor {

    public static void removeVertex(Model model, int vertexIndex) {
        if (model == null || vertexIndex < 0 || vertexIndex >= model.getVertices().size()) {
            return;
        }

        model.getVertices().remove(vertexIndex);

        ArrayList<Polygon> newPolygons = new ArrayList<>();
        for (Polygon polygon : model.getPolygons()) {
            if (!polygon.getVertexIndices().contains(vertexIndex)) {
                ArrayList<Integer> newIndices = new ArrayList<>();
                for (Integer index : polygon.getVertexIndices()) {
                    if (index > vertexIndex) {
                        newIndices.add(index - 1);
                    } else if (index < vertexIndex) {
                        newIndices.add(index);
                    }
                }
                polygon.setVertexIndices(newIndices);
                newPolygons.add(polygon);
            }
        }
        model.setPolygons(newPolygons);
    }

    public static void removePolygon(Model model, int polygonIndex) {
        if (model == null || polygonIndex < 0 || polygonIndex >= model.getPolygons().size()) {
            return;
        }

        model.getPolygons().remove(polygonIndex);
    }

    public static void removeSelectedVertices(Model model, Set<Integer> vertexIndices) {
        if (model == null || vertexIndices == null || vertexIndices.isEmpty()) {
            return;
        }

        ArrayList<Integer> sortedIndices = new ArrayList<>(vertexIndices);
        sortedIndices.sort((a, b) -> b - a);

        for (Integer vertexIndex : sortedIndices) {
            removeVertex(model, vertexIndex);
        }
    }

    public static void removeSelectedPolygons(Model model, Set<Integer> polygonIndices) {
        if (model == null || polygonIndices == null || polygonIndices.isEmpty()) {
            return;
        }

        ArrayList<Integer> sortedIndices = new ArrayList<>(polygonIndices);
        sortedIndices.sort((a, b) -> b - a);

        for (Integer polygonIndex : sortedIndices) {
            removePolygon(model, polygonIndex);
        }
    }
}