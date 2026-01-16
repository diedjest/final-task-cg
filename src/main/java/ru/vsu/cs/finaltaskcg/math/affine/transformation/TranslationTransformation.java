package ru.vsu.cs.finaltaskcg.math.affine.transformation;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;

/**
 * Class for Translation Transformation
 *
 * @author Roman Merkulov
 */
public class TranslationTransformation implements Transformation {
    private final double tx, ty, tz;

    public TranslationTransformation(double tx, double ty, double tz) {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }

    @Override
    public Matrix4 getMatrix() {
        Matrix4 matrix = Matrix4.identity();
        matrix.set(0, 3, tx);
        matrix.set(1, 3, ty);
        matrix.set(2, 3, tz);
        return matrix;
    }
}