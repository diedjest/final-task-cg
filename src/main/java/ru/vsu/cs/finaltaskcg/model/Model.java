package ru.vsu.cs.finaltaskcg.model;

import ru.vsu.cs.finaltaskcg.math.vector.Vector2;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;

import java.util.*;

public class Model {

    public ArrayList<Vector3> vertices = new ArrayList<Vector3>();
    public ArrayList<Vector2> textureVertices = new ArrayList<Vector2>();
    public ArrayList<Vector3> normals = new ArrayList<Vector3>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();
}
