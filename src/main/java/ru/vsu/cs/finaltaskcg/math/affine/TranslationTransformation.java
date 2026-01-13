package ru.vsu.cs.finaltaskcg.math.affine;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

public class TranslationTransformation implements Transformation {
    private final double tx, ty, tz;

    public TranslationTransformation(double tx, double ty, double tz) {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }

    @Override
    public Matrix4d getMatrix() {
        return new Matrix4d(
                1, 0, 0, tx,
                0, 1, 0, ty,
                0, 0, 1, tz,
                0, 0, 0, 1
        );
    }
}
