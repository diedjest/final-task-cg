package ru.vsu.cs.finaltaskcg.model;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;
import ru.vsu.cs.finaltaskcg.math.vector.Vector2;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import ru.vsu.cs.finaltaskcg.render_engine.GraphicConveyor;

import java.util.*;

public class Model {

    public ArrayList<Vector3> vertices = new ArrayList<>();
    public ArrayList<Vector2> textureVertices = new ArrayList<>();
    public ArrayList<Vector3> normals = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();

    private ModelTransform transform = new ModelTransform();

    public Matrix4 getTransformationMatrix() {
        return GraphicConveyor.rotateScaleTranslate(
                transform.getTranslation(),
                transform.getRotation(),
                transform.getScale()
        );
    }

    public ModelTransform getTransform() {
        return transform;
    }

    public void setTransform(ModelTransform transform) {
        this.transform = transform;
    }

    public void applyTransformations() {
        Matrix4 transformMatrix = getTransformationMatrix();

        for (int i = 0; i < vertices.size(); i++) {
            Vector3 vertex = vertices.get(i);
            Vector3 transformed = GraphicConveyor.multiplyMatrix4ByVector3(transformMatrix, vertex);
            vertices.set(i, transformed);
        }

        transform.reset();
    }
}
