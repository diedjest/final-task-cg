package ru.vsu.cs.finaltaskcg.math.affine;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

public class RotateTransformation implements Transformation {
    private final Matrix4d rotation;

    public RotateTransformation(Axis axis, double angle) {
       this.rotation =  createRotationMatrix(axis, angle);
    }

    private Matrix4d createRotationMatrix(Axis axis, double angle) {
        switch (axis) {
            case X:
                return new Matrix4d(
                        1, 0, 0, 0,
                        0, Math.cos(angle), Math.sin(angle), 0,
                        0, -Math.sin(angle), Math.cos(angle), 0,
                        0, 0, 0, 1
                );
            case Y:
                return new Matrix4d(
                        Math.cos(angle), 0, Math.sin(angle), 0,
                        0, 1, 0, 0,
                        -Math.sin(angle), 0, Math.cos(angle), 0,
                        0, 0, 0, 1
                );
            case Z:
                return new Matrix4d(
                        Math.cos(angle), Math.sin(angle), 0, 0,
                        -Math.sin(angle), Math.cos(angle), 0, 0,
                        0, 0, 1, 0,
                        0, 0, 0, 1
                );
            default:
                throw new IllegalArgumentException("Unknown axis:" + axis);
        }
    }

    @Override
    public Matrix4d getMatrix() {
        Matrix4d matrix = new Matrix4d();
        matrix.set(rotation);
        return matrix;
    }
}
