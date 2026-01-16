package ru.vsu.cs.finaltaskcg.math.vector;

import ru.vsu.cs.finaltaskcg.math.validation.MathValidator;

/**
 * Class for Vector2 linear operations
 *
 * @author Dmitriy Uvarov
 */
public class Vector2 {
    private double x, y;

    /**
     * Default constructor with zero vector
     */
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

    /**
     * Adds vectors
     */
    public Vector2 add(Vector2 v) {
        MathValidator.checkNotNull(v, "Vector2 for addition");
        return new Vector2(x + v.x, y + v.y);
    }

    /**
     * Subtracts vectors
     */
    public Vector2 sub(Vector2 v) {
        MathValidator.checkNotNull(v, "Vector2 for subtraction");
        return new Vector2(x - v.x, y - v.y);
    }

    /**
     * Multiply vector by number
     */
    public Vector2 mul(double s) {
        return new Vector2(x * s, y * s);
    }

    /**
     * Divide vector by number
     */
    public Vector2 div(double s) {
        MathValidator.checkNotZero(s, "Divisor");
        return new Vector2(x / s, y / s);
    }

    /**
     * Calc vector length
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Normalize vector
     */
    public Vector2 normalize() {
        double len = length();
        MathValidator.checkNotZero(len, "Vector length for normalization");
        return div(len);
    }

    /**
     * Calc dot product of vectors
     */
    public double dot(Vector2 v) {
        MathValidator.checkNotNull(v, "Vector2 for dot product");
        return x * v.x + y * v.y;
    }
}