package ru.vsu.cs.finaltaskcg.math.affine.transformation;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;

/**
 * Class for scale transformation
 *
 * @author Roman Merkulov
 */
public class ScaleTransformation implements Transformation {
    private final double sx, sy, sz;

    public ScaleTransformation(double sx, double sy, double sz) {
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
    }

    public ScaleTransformation(double uniformScale) {
        this(uniformScale, uniformScale, uniformScale);
    }

    @Override
    public Matrix4 getMatrix() {
        Matrix4 matrix = Matrix4.identity();
        matrix.set(0, 0, sx);
        matrix.set(1, 1, sy);
        matrix.set(2, 2, sz);
        return matrix;
    }
}