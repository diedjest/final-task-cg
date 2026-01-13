package ru.vsu.cs.finaltaskcg.math.affine;

import javax.vecmath.Matrix4d;


public class SaveTransformation implements Transformation{
    private final Matrix4d saveCondition;

    public SaveTransformation(Matrix4d matrix) {
        this.saveCondition = (Matrix4d) matrix.clone();
    }

    @Override
    public Matrix4d getMatrix() {
        return (Matrix4d) this.saveCondition.clone();
    }
}
