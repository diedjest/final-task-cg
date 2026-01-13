package ru.vsu.cs.finaltaskcg.math.affine;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;

public class RotateTransformationOnQuad implements Transformation {
    private final Quat4d rotation;


    public RotateTransformationOnQuad(Axis axis, double angle) {
        this.rotation = createRotationQuad(axis, angle);
    }

    private Quat4d createRotationQuad(Axis axis, double angle) {
        switch (axis) {
            case X:
                return new Quat4d(
                        -Math.sin(angle/2),
                        0,
                        0,
                        Math.cos(angle/2)
                );
            case Y:
                return new Quat4d(
                        0,
                        Math.sin(angle/2),
                        0,
                        Math.cos(angle/2)
                );
            case Z:
                return new Quat4d(
                        0,
                        0,
                        -Math.sin(angle/2),
                        Math.cos(angle/2)
                );
            default:
                throw new IllegalArgumentException("Unknow axis:" + axis);
        }
    }

    @Override
    public Matrix4d getMatrix() {
        Matrix4d matrix = new Matrix4d();
        matrix.set(rotation);
        return matrix;
    }
}
