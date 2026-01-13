package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;

public class TranslationTransformation implements Transformation {
    private final double tx, ty, tz;

    public TranslationTransformation(double tx, double ty, double tz) {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }

    @Override
    public Matrix4 getMatrix() {
        double[][] matrix4 = {
                {1, 0, 0, tx},
                {0, 1, 0, ty},
                {0, 0, 1, tz},
                {0, 0, 0, 1}};
        return new Matrix4(matrix4);
    }
}
