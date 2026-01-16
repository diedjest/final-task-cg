package ru.vsu.cs.finaltaskcg.math.vector;

import ru.vsu.cs.finaltaskcg.math.validation.MathValidator;

public class Vector3 {
    private double x, y, z;

    public Vector3() { this(0, 0, 0); }

    public Vector3(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector3 add(Vector3 v) {
        MathValidator.checkNotNull(v, "Vector3 for addition");
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 sub(Vector3 v) {
        MathValidator.checkNotNull(v, "Vector3 for subtraction");
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    public Vector3 mul(double s) {
        return new Vector3(x * s, y * s, z * s);
    }

    public Vector3 div(double s) {
        MathValidator.checkNotZero(s, "Divisor");
        return new Vector3(x / s, y / s, z / s);
    }

    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public Vector3 normalize() {
        double len = length();
        MathValidator.checkNotZero(len, "Vector length for normalization");
        return div(len);
    }

    public double dot(Vector3 v) {
        MathValidator.checkNotNull(v, "Vector3 for dot product");
        return x*v.x + y*v.y + z*v.z;
    }

    public Vector3 cross(Vector3 v) {
        MathValidator.checkNotNull(v, "Vector3 for cross product");
        return new Vector3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }
}