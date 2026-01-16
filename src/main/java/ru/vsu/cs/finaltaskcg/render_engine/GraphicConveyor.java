package ru.vsu.cs.finaltaskcg.render_engine;

public class GraphicConveyor {

    public static Matrix4 rotateScaleTranslate() {
        return Matrix4.identity();
    }

    public static Matrix4 lookAt(Vector3 eye, Vector3 target) {
        return lookAt(eye, target, new Vector3(0, 1.0, 0));
    }

    public static Matrix4 lookAt(Vector3 eye, Vector3 target, Vector3 up) {
        Vector3 resultZ = target.sub(eye).normalize();
        Vector3 resultX = up.cross(resultZ).normalize();
        Vector3 resultY = resultZ.cross(resultX).normalize();

        double[][] matrix = {
                {resultX.x, resultY.x, resultZ.x, 0},
                {resultX.y, resultY.y, resultZ.y, 0},
                {resultX.z, resultY.z, resultZ.z, 0},
                {-resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1}
        };
        return new Matrix4(matrix);
    }

    public static Matrix4 perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {

        double tangentMinusOnDegree = 1.0 / Math.tan(fov * 0.5);

        Matrix4 matrix = new Matrix4();

        matrix.set(0, 0, tangentMinusOnDegree / aspectRatio);
        matrix.set(1, 1, tangentMinusOnDegree);
        matrix.set(2, 2, (farPlane + nearPlane) / (farPlane - nearPlane));
        matrix.set(2, 3, 1.0);
        matrix.set(3, 2, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));

        return matrix;
    }

    public static Vector3 multiplyMatrix4ByVector3(final Matrix4 matrix, final Vector3 vertex) {
        double x = (vertex.x * matrix.get(0, 0)) + (vertex.y * matrix.get(1, 0)) + (vertex.z * matrix.get(2, 0)) + matrix.get(3, 0);
        double y = (vertex.x * matrix.get(0, 1)) + (vertex.y * matrix.get(1, 1)) + (vertex.z * matrix.get(2, 1)) + matrix.get(3, 1);
        double z = (vertex.x * matrix.get(0, 2)) + (vertex.y * matrix.get(1, 2)) + (vertex.z * matrix.get(2, 2)) + matrix.get(3, 2);
        double w = (vertex.x * matrix.get(0, 3)) + (vertex.y * matrix.get(1, 3)) + (vertex.z * matrix.get(2, 3)) + matrix.get(3, 3);

        if (w != 0) {
            return new Vector3(x / w, y / w, z / w);
        }
        return new Vector3(x, y, z);
    }

    public static Vector2 vertexToPoint(final Vector3 vertex, final int width, final int height) {
        return new Vector2(vertex.x * width + width / 2.0, -vertex.y * height + height / 2.0);
    }
}
