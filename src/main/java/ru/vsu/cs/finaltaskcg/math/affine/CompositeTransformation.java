package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.affine.transformation.Transformation;
import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for composite transformation combines multiply transformations
 */
public class CompositeTransformation implements Transformation {
    private final List<Transformation> transformations;
    private Matrix4 cachedMatrix;
    private boolean isDirty;

    public CompositeTransformation() {
        this.transformations = new ArrayList<>();
        this.cachedMatrix = Matrix4.identity();
        this.isDirty = false;
    }

    /**
     * Adds transformation to composition
     */
    public void add(Transformation transformation) {
        this.transformations.add(transformation);
        this.isDirty = true;
    }

    /**
     * Recalculates the cached transformation matrix
     */
    private void updateCachedMatrix() {
        Matrix4 result = Matrix4.identity();

        for (Transformation transformation : transformations) {
            result = transformation.getMatrix().mul(result);
        }

        this.cachedMatrix = result;
        this.isDirty = false;
    }

    /**
     * Returns composite transformation matrix
     * If cache is dirty recal matrix
     */
    @Override
    public Matrix4 getMatrix() {
        if (isDirty) {
            updateCachedMatrix();
        }
        return cachedMatrix;
    }
}