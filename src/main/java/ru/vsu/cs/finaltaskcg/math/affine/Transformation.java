package ru.vsu.cs.finaltaskcg.math.affine;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

public interface Transformation {
    Matrix4d getMatrix();

    default Point3d apply (Point3d point) {
        Point3d result = new Point3d();
        this.getMatrix().transform(point, result);
        return result;
    }
}
