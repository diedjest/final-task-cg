package ru.vsu.cs.finaltaskcg.transformations;

public class ScaleTransformation implements Transformation {
    private final float sx, sy, sz;

    public ScaleTransformation(float sx, float sy, float sz) {
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
    }

    public ScaleTransformation(float uniformScale) {
        this(uniformScale, uniformScale, uniformScale);
    }

    @Override
    public float[][] getMatrix() {
        return new float[][] {
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, 0},
                {0, 0, 0, 1}
        };
    }
}