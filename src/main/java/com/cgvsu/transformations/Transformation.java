package com.cgvsu.transformations;

import com.cgvsu.math.Vector3f;

public interface Transformation {
    float[][] getMatrix();

    default Vector3f apply(Vector3f point) {
        float[][] matrix = getMatrix();
        float x = point.x;
        float y = point.y;
        float z = point.z;

        float newX = matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z + matrix[0][3];
        float newY = matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z + matrix[1][3];
        float newZ = matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z + matrix[2][3];
        float w = matrix[3][0] * x + matrix[3][1] * y + matrix[3][2] * z + matrix[3][3];

        if (Math.abs(w) > 1e-7f) {
            return new Vector3f(newX / w, newY / w, newZ / w);
        }
        return new Vector3f(newX, newY, newZ);
    }
}