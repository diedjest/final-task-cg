package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import ru.vsu.cs.finaltaskcg.math.vector.Vector4;

public interface Transformation {

    Matrix4 getMatrix();

    default Vector3 apply(Vector3 point) {
        // Преобразуем Vector3 в однородные координаты Vector4 (w=1 для точки)
        Vector4 homogeneousPoint = new Vector4(point.getX(), point.getY(), point.getZ(), 1.0);

        // Применяем матрицу преобразования
        Vector4 transformedHomogeneous = getMatrix().mul(homogeneousPoint);

        // Преобразуем обратно из однородных координат в 3D (деление на w)
        // Если w != 0, делаем перспективное деление
        double w = transformedHomogeneous.getW();
        if (Math.abs(w) > 1e-10) {
            return new Vector3(
                    transformedHomogeneous.getX() / w,
                    transformedHomogeneous.getY() / w,
                    transformedHomogeneous.getZ() / w
            );
        } else {
            // Если w близко к 0, возвращаем как есть (для направлений)
            return new Vector3(
                    transformedHomogeneous.getX(),
                    transformedHomogeneous.getY(),
                    transformedHomogeneous.getZ()
            );
        }
    }

    default Vector3 applyToVector(Vector3 vector) {
        // Для векторов используем w=0 (не применяется перенос)
        Vector4 homogeneousVector = new Vector4(vector.getX(), vector.getY(), vector.getZ(), 0.0);

        // Применяем матрицу преобразования
        Vector4 transformed = getMatrix().mul(homogeneousVector);

        // Возвращаем только x, y, z компоненты (w остается 0)
        return new Vector3(transformed.getX(), transformed.getY(), transformed.getZ());
    }

    default Vector4 apply(Vector4 homogeneousPoint) {
        return getMatrix().mul(homogeneousPoint);
    }
}