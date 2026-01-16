package ru.vsu.cs.finaltaskcg.math.vector;

import ru.vsu.cs.finaltaskcg.math.validation.MathValidator;

public class Vector2 {
    private double x, y;

    public Vector2() { this(0, 0); }

    public Vector2(double x, double y) {
        this.x = x; this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) { this.x = x; }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2 add(Vector2 v) {
        MathValidator.checkNotNull(v, "Vector2 for addition");
        return new Vector2(x + v.x, y + v.y);
    }

    public Vector2 sub(Vector2 v) {
        MathValidator.checkNotNull(v, "Vector2 for subtraction");
        return new Vector2(x - v.x, y - v.y);
    }

    public Vector2 mul(double s) {
        return new Vector2(x * s, y * s);
    }

    public Vector2 div(double s) {
        MathValidator.checkNotZero(s, "Divisor");
        return new Vector2(x / s, y / s);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2 normalize() {
        double len = length();
        MathValidator.checkNotZero(len, "Vector length for normalization");
        return div(len);
    }

    public double dot(Vector2 v) {
        MathValidator.checkNotNull(v, "Vector2 for dot product");
        return x * v.x + y * v.y;
    }
}