package ru.vsu.cs.finaltaskcg.model;

import ru.vsu.cs.finaltaskcg.math.vector.Vector3;

import java.util.ArrayList;

public class CameraModel {

    public static Model createCameraModel(Vector3 position, Vector3 target) {
        Model model = new Model();

        // Вершины пирамиды (камеры)
        // Основание пирамиды
        model.vertices.add(new Vector3(-5, -5, 0));
        model.vertices.add(new Vector3(5, -5, 0));
        model.vertices.add(new Vector3(5, 5, 0));
        model.vertices.add(new Vector3(-5, 5, 0));
        // Вершина пирамиды (направление камеры)
        model.vertices.add(new Vector3(0, 0, -15));

        // Полигоны
        Polygon base = new Polygon();
        base.setVertexIndices(new ArrayList<Integer>() {{ add(0); add(1); add(2); add(3); }});

        Polygon side1 = new Polygon();
        side1.setVertexIndices(new ArrayList<Integer>() {{ add(0); add(1); add(4); }});

        Polygon side2 = new Polygon();
        side2.setVertexIndices(new ArrayList<Integer>() {{ add(1); add(2); add(4); }});

        Polygon side3 = new Polygon();
        side3.setVertexIndices(new ArrayList<Integer>() {{ add(2); add(3); add(4); }});

        Polygon side4 = new Polygon();
        side4.setVertexIndices(new ArrayList<Integer>() {{ add(3); add(0); add(4); }});

        model.polygons.add(base);
        model.polygons.add(side1);
        model.polygons.add(side2);
        model.polygons.add(side3);
        model.polygons.add(side4);

        return model;
    }
}