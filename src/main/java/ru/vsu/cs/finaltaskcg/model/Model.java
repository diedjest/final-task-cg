package ru.vsu.cs.finaltaskcg.model;

import ru.vsu.cs.finaltaskcg.math.affine.AffineBuilder;
import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;
import ru.vsu.cs.finaltaskcg.math.vector.Vector2;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import ru.vsu.cs.finaltaskcg.math.vector.Vector4;
import ru.vsu.cs.finaltaskcg.render_engine.GraphicConveyor;

import java.util.*;

public class Model {

    public ArrayList<Vector3> vertices = new ArrayList<>();
    public ArrayList<Vector2> textureVertices = new ArrayList<>();
    public ArrayList<Vector3> normals = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();

    // dont touch
    public AffineBuilder affineBuilder = new AffineBuilder();

    // dont touch
    public Matrix4 getTransformationMatrix() {
        return affineBuilder.build().getMatrix();
    }

    // dont touch
    public void resetTransform() {
        affineBuilder = new AffineBuilder();
    }

    // dont touch
    public void applyTransformations() {
        Matrix4 transformMatrix = getTransformationMatrix();

        for (int i = 0; i < vertices.size(); i++) {
            Vector3 v = vertices.get(i);

            Vector4 v4 = new Vector4(v.getX(), v.getY(), v.getZ(), 1.0);
            Vector4 res = transformMatrix.mul(v4);

            vertices.set(i, new Vector3(
                    res.getX() / res.getW(),
                    res.getY() / res.getW(),
                    res.getZ() / res.getW()
            ));
        }


        resetTransform();
    }
}
