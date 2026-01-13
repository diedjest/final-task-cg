package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;

public class RotateTransformation implements Transformation {
    private final Matrix4 rotation;

    public RotateTransformation(Axis axis, double angle) {
       this.rotation = createRotationMatrix(axis, angle);
    }

    private Matrix4 createRotationMatrix(Axis axis, double angle) {
        double[][] xRotationMatrix = new double[][] {
                {1, 0, 0, 0},
                {0, Math.cos(angle), Math.sin(angle), 0},
                {0, -Math.sin(angle), Math.cos(angle), 0},
                {0, 0, 0, 1}};
        double[][] yRotationMatrix = new double[][] {
                {Math.cos(angle), 0, Math.sin(angle), 0},
                {0, 1, 0, 0},
                {-Math.sin(angle), 0, Math.cos(angle), 0},
                {0, 0, 0, 1}
        };
        double[][] zRotationMatrix = new double[][] {
                {Math.cos(angle), Math.sin(angle), 0, 0},
                {-Math.sin(angle), Math.cos(angle), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        switch (axis) {
            case X:
                return new Matrix4(xRotationMatrix);
            case Y:
                return new Matrix4(yRotationMatrix);
            case Z:
                return new Matrix4(zRotationMatrix);
            default:
                throw new IllegalArgumentException("Unknown axis:" + axis);
        }
    }

    public Matrix4 getMatrix() {
        return rotation;
    }
}
