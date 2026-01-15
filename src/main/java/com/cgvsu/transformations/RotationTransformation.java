package com.cgvsu.transformations;

public class RotationTransformation implements Transformation {
    private final Axis axis;
    private final float angle;

    public RotationTransformation(Axis axis, float angle) {
        this.axis = axis;
        this.angle = angle;
    }

    @Override
    public float[][] getMatrix() {
        float cos = (float) Math.cos(Math.toRadians(angle));
        float sin = (float) Math.sin(Math.toRadians(angle));

        switch (axis) {
            case X:
                return new float[][] {
                        {1, 0, 0, 0},
                        {0, cos, -sin, 0},
                        {0, sin, cos, 0},
                        {0, 0, 0, 1}
                };
            case Y:
                return new float[][] {
                        {cos, 0, sin, 0},
                        {0, 1, 0, 0},
                        {-sin, 0, cos, 0},
                        {0, 0, 0, 1}
                };
            case Z:
                return new float[][] {
                        {cos, -sin, 0, 0},
                        {sin, cos, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}
                };
            default:
                return new float[][] {
                        {1, 0, 0, 0},
                        {0, 1, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}
                };
        }
    }
}