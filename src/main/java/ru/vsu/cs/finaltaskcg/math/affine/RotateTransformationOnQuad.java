package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;
import ru.vsu.cs.finaltaskcg.math.vector.Vector4;

public class RotateTransformationOnQuad implements Transformation {
    private final Vector4 rotation;

    public RotateTransformationOnQuad(Axis axis, double angle) {
        this.rotation = createRotationQuad(axis, angle);
    }

    private Vector4 createRotationQuad(Axis axis, double angle) {
        double halfAngle = angle / 2;
        double sinHalf = Math.sin(halfAngle);
        double cosHalf = Math.cos(halfAngle);

        switch (axis) {
            case X:
                return new Vector4(sinHalf, 0, 0, cosHalf);
            case Y:
                return new Vector4(0, sinHalf, 0, cosHalf);
            case Z:
                return new Vector4(0, 0, sinHalf, cosHalf);
            default:
                throw new IllegalArgumentException("Unknown axis: " + axis);
        }
    }

    @Override
    public Matrix4 getMatrix() {
        return quaternionToMatrix(rotation);
    }

    private Matrix4 quaternionToMatrix(Vector4 q) {
        // Нормализуем кватернион
        Vector4 qn = q.normalize();
        double x = qn.getX();
        double y = qn.getY();
        double z = qn.getZ();
        double w = qn.getW();

        // Предварительные вычисления
        double xx = x * x;
        double xy = x * y;
        double xz = x * z;
        double xw = x * w;
        double yy = y * y;
        double yz = y * z;
        double yw = y * w;
        double zz = z * z;
        double zw = z * w;

        // Создаем матрицу 4x4
        Matrix4 matrix = new Matrix4();

        // Первая строка
        matrix.set(0, 0, 1 - 2 * (yy + zz));  // 1 - 2y² - 2z²
        matrix.set(0, 1, 2 * (xy - zw));      // 2xy - 2zw
        matrix.set(0, 2, 2 * (xz + yw));      // 2xz + 2yw
        matrix.set(0, 3, 0);

        // Вторая строка
        matrix.set(1, 0, 2 * (xy + zw));      // 2xy + 2zw
        matrix.set(1, 1, 1 - 2 * (xx + zz));  // 1 - 2x² - 2z²
        matrix.set(1, 2, 2 * (yz - xw));      // 2yz - 2xw
        matrix.set(1, 3, 0);

        // Третья строка
        matrix.set(2, 0, 2 * (xz - yw));      // 2xz - 2yw
        matrix.set(2, 1, 2 * (yz + xw));      // 2yz + 2xw
        matrix.set(2, 2, 1 - 2 * (xx + yy));  // 1 - 2x² - 2y²
        matrix.set(2, 3, 0);

        // Четвертая строка (однородные координаты)
        matrix.set(3, 0, 0);
        matrix.set(3, 1, 0);
        matrix.set(3, 2, 0);
        matrix.set(3, 3, 1);

        return matrix;
    }
}