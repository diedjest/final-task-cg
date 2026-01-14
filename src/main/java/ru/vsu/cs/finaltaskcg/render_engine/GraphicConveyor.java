package ru.vsu.cs.finaltaskcg.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

public class GraphicConveyor {

    public static Matrix4f rotateScaleTranslate() {
        float[][] matrix = new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        return new Matrix4f(matrix);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultZ = new Vector3f(
                target.x - eye.x,
                target.y - eye.y,
                target.z - eye.z
        );
        resultZ = resultZ.normalize();

        Vector3f resultX = up.cross(resultZ);
        resultX = resultX.normalize();

        Vector3f resultY = resultZ.cross(resultX);
        resultY = resultY.normalize();

        float[][] matrix = new float[][]{
                {resultX.x, resultY.x, resultZ.x, 0},
                {resultX.y, resultY.y, resultZ.y, 0},
                {resultX.z, resultY.z, resultZ.z, 0},
                {-resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1}
        };
        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {

        float tangentMinusOnDegree = (float) (1.0F / Math.tan(fov * 0.5F));

        float[][] matrix = new float[4][4];
        // Инициализируем нулями
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = 0;
            }
        }

        matrix[0][0] = tangentMinusOnDegree / aspectRatio;
        matrix[1][1] = tangentMinusOnDegree;
        matrix[2][2] = (farPlane + nearPlane) / (farPlane - nearPlane);
        matrix[2][3] = 1.0F;
        matrix[3][2] = 2 * (nearPlane * farPlane) / (nearPlane - farPlane);

        return new Matrix4f(matrix);
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        float x = (vertex.x * matrix.get(0, 0)) + (vertex.y * matrix.get(1, 0)) + (vertex.z * matrix.get(2, 0)) + matrix.get(3, 0);
        float y = (vertex.x * matrix.get(0, 1)) + (vertex.y * matrix.get(1, 1)) + (vertex.z * matrix.get(2, 1)) + matrix.get(3, 1);
        float z = (vertex.x * matrix.get(0, 2)) + (vertex.y * matrix.get(1, 2)) + (vertex.z * matrix.get(2, 2)) + matrix.get(3, 2);
        float w = (vertex.x * matrix.get(0, 3)) + (vertex.y * matrix.get(1, 3)) + (vertex.z * matrix.get(2, 3)) + matrix.get(3, 3);

        if (w != 0) {
            return new Vector3f(x / w, y / w, z / w);
        }
        return new Vector3f(x, y, z);
    }

    public static Vector2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Vector2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }
}