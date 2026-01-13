package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.affine.transformation.Transformation;
import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;

import java.util.ArrayList;
import java.util.List;

public class CompositeTransformation implements Transformation {
    private final List<Transformation> transformations;
    private Matrix4 cachedMatrix;
    private boolean isDirty;

    public CompositeTransformation() {
        this.transformations = new ArrayList<>();
        this.cachedMatrix = Matrix4.identity();
        this.isDirty = false;
    }

    public void add(Transformation transformation) {
        this.transformations.add(transformation);
        this.isDirty = true;
    }

    private void updateCachedMatrix() {
        Matrix4 result = Matrix4.identity();

        for (Transformation transformation : transformations) {
            // Умножаем матрицы: result = transformation.getMatrix() × result
            result = transformation.getMatrix().mul(result);
        }

        this.cachedMatrix = result;
        this.isDirty = false;
    }

    @Override
    public Matrix4 getMatrix() {
        if (isDirty) {
            updateCachedMatrix();
        }
        return cachedMatrix; // Возвращаем копию
    }
}