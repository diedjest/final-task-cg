package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;
import ru.vsu.cs.finaltaskcg.math.vector.Vector4;

public class RotateTransformationOnQuad implements Transformation {
    private final Vector4 rotation;

    public RotateTransformationOnQuad(Axis axis, double angle) {
        this.rotation = createRotationQuad(axis, angle);
    }

    private Vector4 createRotationQuad(Axis axis, double angle) {
        switch (axis) {
            case X:
                return new Vector4(
                        -Math.sin(angle/2),
                        0,
                        0,
                        Math.cos(angle/2)
                );
            case Y:
                return new Vector4(
                        0,
                        Math.sin(angle/2),
                        0,
                        Math.cos(angle/2)
                );
            case Z:
                return new Vector4(
                        0,
                        0,
                        -Math.sin(angle/2),
                        Math.cos(angle/2)
                );
            default:
                throw new IllegalArgumentException("Unknown axis: " + axis);
        }
    }

    // ВАЖНО: метод должен возвращать Matrix4, а не Vector4!
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

        // Вычисляем элементы матрицы поворота из кватерниона
        // Формулы для матрицы 3x3 поворота из кватерниона
        double xx = x * x;
        double xy = x * y;
        double xz = x * z;
        double xw = x * w;
        double yy = y * y;
        double yz = y * z;
        double yw = y * w;
        double zz = z * z;
        double zw = z * w;

        // Создаем матрицу 4x4 для аффинного преобразования
        Matrix4 matrix = new Matrix4();

        // Первая строка
        matrix.set(0, 0, 1 - 2 * (yy + zz));
        matrix.set(0, 1, 2 * (xy - zw));
        matrix.set(0, 2, 2 * (xz + yw));
        matrix.set(0, 3, 0);

        // Вторая строка
        matrix.set(1, 0, 2 * (xy + zw));
        matrix.set(1, 1, 1 - 2 * (xx + zz));
        matrix.set(1, 2, 2 * (yz - xw));
        matrix.set(1, 3, 0);

        // Третья строка
        matrix.set(2, 0, 2 * (xz - yw));
        matrix.set(2, 1, 2 * (yz + xw));
        matrix.set(2, 2, 1 - 2 * (xx + yy));
        matrix.set(2, 3, 0);

        // Четвертая строка (однородные координаты)
        matrix.set(3, 0, 0);
        matrix.set(3, 1, 0);
        matrix.set(3, 2, 0);
        matrix.set(3, 3, 1);

        return matrix;
    }
}