package ru.vsu.cs.finaltaskcg.math.affine.transformation;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import ru.vsu.cs.finaltaskcg.math.vector.Vector4;

import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

/**
 * Interface for affine transformations
 *
 * @author Roman Merkulov
 */
public interface Transformation {

    Matrix4 getMatrix();

    /**
     * Applies transformation to 3D point
     */
    default Vector3 apply(Vector3 point) {
        Vector4 homogeneousPoint = new Vector4(point.getX(), point.getY(), point.getZ(), 1.0);

        Vector4 transformedHomogeneous = getMatrix().mul(homogeneousPoint);

        double w = transformedHomogeneous.getW();
        if (Math.abs(w) > EPSILON) {
            return new Vector3(
                    transformedHomogeneous.getX() / w,
                    transformedHomogeneous.getY() / w,
                    transformedHomogeneous.getZ() / w
            );
        } else {
            return new Vector3(
                    transformedHomogeneous.getX(),
                    transformedHomogeneous.getY(),
                    transformedHomogeneous.getZ()
            );
        }
    }
}