package ru.vsu.cs.finaltaskcg.editor;

import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;

import java.util.ArrayList;

public class VertexRemover {

    public static void removeVertexWithDependencies(Model model, int vertexIndex) {
        if (model == null || vertexIndex < 0 || vertexIndex >= model.getVertices().size()) {
            return;
        }

        ArrayList<Integer> polygonsToRemove = new ArrayList<>();
        for (int i = 0; i < model.getPolygons().size(); i++) {
            Polygon polygon = model.getPolygons().get(i);
            if (polygon.getVertexIndices().contains(vertexIndex)) {
                polygonsToRemove.add(i);
            }
        }

        for (int i = polygonsToRemove.size() - 1; i >= 0; i--) {
            model.getPolygons().remove(polygonsToRemove.get(i).intValue());
        }

        model.getVertices().remove(vertexIndex);

        for (Polygon polygon : model.getPolygons()) {
            ArrayList<Integer> newIndices = new ArrayList<>();
            for (Integer index : polygon.getVertexIndices()) {
                if (index > vertexIndex) {
                    newIndices.add(index - 1);
                } else {
                    newIndices.add(index);
                }
            }
            polygon.setVertexIndices(newIndices);
        }
    }
}