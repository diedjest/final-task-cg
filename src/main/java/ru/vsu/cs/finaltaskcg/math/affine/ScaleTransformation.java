package ru.vsu.cs.finaltaskcg.math.affine;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

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

    @Override
    public Matrix4d getMatrix() {
        return new Matrix4d(
            sx, 0, 0, 0,
            0, sy, 0, 0,
            0, 0, sz, 0,
            0, 0, 0, 1
        );
    }
}
