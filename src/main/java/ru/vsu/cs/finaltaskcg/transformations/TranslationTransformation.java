package ru.vsu.cs.finaltaskcg.transformations;

public class TranslationTransformation implements Transformation {
    private final float tx, ty, tz;

    public TranslationTransformation(float tx, float ty, float tz) {
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
    }

    @Override
    public float[][] getMatrix() {
        return new float[][] {
                {1, 0, 0, tx},
                {0, 1, 0, ty},
                {0, 0, 1, tz},
                {0, 0, 0, 1}
        };
    }
}