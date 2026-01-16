package ru.vsu.cs.finaltaskcg.math.affine.transformation;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;

public class SaveTransformation implements Transformation {
    private final Matrix4 saveCondition;

    public SaveTransformation(Matrix4 matrix) {
        this.saveCondition = new Matrix4(matrix);
    }

    @Override
    public Matrix4 getMatrix() {
        return new Matrix4(saveCondition);
    }
}