package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;

public class ScaleTransformation implements Transformation {
    private final double sx, sy, sz;

    public ScaleTransformation(double sx, double sy, double sz) {
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
    }

    public ScaleTransformation(double uniformScale) {
        this(uniformScale, uniformScale, uniformScale);
    }

    public Matrix4 getMatrix() {
        double[][] matrix4 = {
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, 0},
                {0, 0, 0, 1}
        };
        return new Matrix4(matrix4);
    }
}
