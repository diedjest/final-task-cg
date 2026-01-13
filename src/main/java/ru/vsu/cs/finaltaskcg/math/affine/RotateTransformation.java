package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;

public class RotateTransformation implements Transformation {
    private final Matrix4 rotation;

    public RotateTransformation(Axis axis, double angle) {
        this.rotation = createRotationMatrix(axis, angle);
    }

    private Matrix4 createRotationMatrix(Axis axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        Matrix4 matrix = Matrix4.identity();

        switch (axis) {
            case X:
                matrix.set(1, 1, cos);  matrix.set(1, 2, -sin);
                matrix.set(2, 1, sin);  matrix.set(2, 2, cos);
                break;

            case Y:
                matrix.set(0, 0, cos);  matrix.set(0, 2, sin);
                matrix.set(2, 0, -sin); matrix.set(2, 2, cos);
                break;

            case Z:
                matrix.set(0, 0, cos);  matrix.set(0, 1, -sin);
                matrix.set(1, 0, sin);  matrix.set(1, 1, cos);
                break;

            default:
                throw new IllegalArgumentException("Unknown axis: " + axis);
        }

        return matrix;
    }

    @Override
    public Matrix4 getMatrix() {
        return rotation;
    }
}