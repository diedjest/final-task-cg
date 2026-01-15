package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import java.util.ArrayList;

public class Model {
    private ArrayList<Vector3f> vertices = new ArrayList<>();
    private ArrayList<Vector2f> textureVertices = new ArrayList<>();
    private ArrayList<Vector3f> normals = new ArrayList<>();
    private ArrayList<Polygon> polygons = new ArrayList<>();
    private String name;

    public Model() {}

    public ArrayList<Vector3f> getVertices() {
        return vertices;
    }

    public ArrayList<Vector2f> getTextureVertices() {
        return textureVertices;
    }

    public ArrayList<Vector3f> getNormals() {
        return normals;
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public String getName() {
        return name;
    }

    public void setVertices(ArrayList<Vector3f> vertices) {
        this.vertices = vertices;
    }

    public void setTextureVertices(ArrayList<Vector2f> textureVertices) {
        this.textureVertices = textureVertices;
    }

    public void setNormals(ArrayList<Vector3f> normals) {
        this.normals = normals;
    }

    public void setPolygons(ArrayList<Polygon> polygons) {
        this.polygons = polygons;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public int getPolygonCount() {
        return polygons.size();
    }

    public void clear() {
        vertices.clear();
        textureVertices.clear();
        normals.clear();
        polygons.clear();
        name = null;
    }
}