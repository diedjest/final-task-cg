package com.cgvsu.transformations;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;

import java.util.ArrayList;
import java.util.List;

public class TransformManager {
    private List<Transformation> transformations;

    public TransformManager() {
        this.transformations = new ArrayList<>();
    }

    public void addTransformation(Transformation transformation) {
        transformations.add(transformation);
    }

    public void clearTransformations() {
        transformations.clear();
    }

    public void applyToModel(Model model) {
        if (model == null || transformations.isEmpty()) {
            return;
        }

        List<Vector3f> vertices = model.getVertices();
        ArrayList<Vector3f> newVertices = new ArrayList<>();

        float[][] compositeMatrix = getCompositeMatrix();

        for (Vector3f vertex : vertices) {
            newVertices.add(applyMatrix(compositeMatrix, vertex));
        }

        model.setVertices(newVertices);

        if (!model.getNormals().isEmpty()) {
            List<Vector3f> normals = model.getNormals();
            ArrayList<Vector3f> newNormals = new ArrayList<>();

            for (Vector3f normal : normals) {
                newNormals.add(applyRotation(compositeMatrix, normal));
            }

            model.setNormals(newNormals);
        }
    }

    private float[][] getCompositeMatrix() {
        if (transformations.isEmpty()) {
            return new float[][] {
                    {1, 0, 0, 0},
                    {0, 1, 0, 0},
                    {0, 0, 1, 0},
                    {0, 0, 0, 1}
            };
        }

        float[][] result = transformations.get(0).getMatrix();

        for (int i = 1; i < transformations.size(); i++) {
            result = multiplyMatrices(transformations.get(i).getMatrix(), result);
        }

        return result;
    }

    private Vector3f applyMatrix(float[][] matrix, Vector3f point) {
        float x = point.x;
        float y = point.y;
        float z = point.z;

        float newX = matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z + matrix[0][3];
        float newY = matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z + matrix[1][3];
        float newZ = matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z + matrix[2][3];
        float w = matrix[3][0] * x + matrix[3][1] * y + matrix[3][2] * z + matrix[3][3];

        if (Math.abs(w) > 1e-7f) {
            return new Vector3f(newX / w, newY / w, newZ / w);
        }
        return new Vector3f(newX, newY, newZ);
    }

    private Vector3f applyRotation(float[][] matrix, Vector3f vector) {
        float x = vector.x;
        float y = vector.y;
        float z = vector.z;

        float newX = matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z;
        float newY = matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z;
        float newZ = matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z;

        float length = (float) Math.sqrt(newX * newX + newY * newY + newZ * newZ);
        if (length > 1e-7f) {
            return new Vector3f(newX / length, newY / length, newZ / length);
        }
        return new Vector3f(newX, newY, newZ);
    }

    private float[][] multiplyMatrices(float[][] a, float[][] b) {
        float[][] result = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return result;
    }

    public void translate(Model model, float dx, float dy, float dz) {
        clearTransformations();
        addTransformation(new TranslationTransformation(dx, dy, dz));
        applyToModel(model);
    }

    public void rotate(Model model, Axis axis, float angle) {
        clearTransformations();
        addTransformation(new RotationTransformation(axis, angle));
        applyToModel(model);
    }

    public void scale(Model model, float sx, float sy, float sz) {
        clearTransformations();
        addTransformation(new ScaleTransformation(sx, sy, sz));
        applyToModel(model);
    }
}