package ru.vsu.cs.finaltaskcg.triangulation;

import ru.vsu.cs.finaltaskcg.model.Model;

/**
 * Интерфейс для триангуляции моделей
 */
public interface Triangulator {

    /**
     * Триангулирует модель (преобразует все полигоны в треугольники)
     */
    void triangulate(Model model);

    /**
     * Проверяет, нужно ли триангулировать модель
     */
    default boolean needsTriangulation(Model model) {
        if (model == null || model.polygons.isEmpty()) {
            return false;
        }

        for (ru.vsu.cs.finaltaskcg.model.Polygon polygon : model.polygons) {
            if (polygon.getVertexIndices().size() > 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Создает триангулированную копию модели
     */
    default Model createTriangulatedModel(Model model) {
        Model triangulatedModel = new Model();
        triangulatedModel.vertices = new java.util.ArrayList<>(model.vertices);
        triangulatedModel.textureVertices = new java.util.ArrayList<>(model.textureVertices);
        triangulatedModel.normals = new java.util.ArrayList<>(model.normals);
        triangulatedModel.polygons = new java.util.ArrayList<>(model.polygons);

        triangulate(triangulatedModel);
        return triangulatedModel;
    }
}