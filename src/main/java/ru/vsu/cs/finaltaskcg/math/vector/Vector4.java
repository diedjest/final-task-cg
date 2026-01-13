package ru.vsu.cs.finaltaskcg.math.vector;

import ru.vsu.cs.finaltaskcg.math.validation.MathValidator;

public class Vector4 {
    private double x, y, z, w;

    public Vector4() { this(0, 0, 0, 0); }

    public Vector4(double x, double y, double z, double w) {
        this.x = x; this.y = y; this.z = z; this.w = w;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getW() {
        return w;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setW(double w) {
        this.w = w;
    }

    public Vector4 add(Vector4 v) {
        MathValidator.checkNotNull(v, "Vector4 for addition");
        return new Vector4(x + v.x, y + v.y, z + v.z, w + v.w);
    }

    public Vector4 sub(Vector4 v) {
        MathValidator.checkNotNull(v, "Vector4 for subtraction");
        return new Vector4(x - v.x, y - v.y, z - v.z, w - v.w);
    }

    public Vector4 mul(double s) {
        return new Vector4(x * s, y * s, z * s, w * s);
    }

    public Vector4 div(double s) {
        MathValidator.checkNotZero(s, "Divisor");
        return new Vector4(x / s, y / s, z / s, w / s);
    }

    public double length() {
        return Math.sqrt(x*x + y*y + z*z + w*w);
    }

    public Vector4 normalize() {
        double len = length();
        MathValidator.checkNotZero(len, "Vector length for normalization");
        return div(len);
    }

    public double dot(Vector4 v) {
        MathValidator.checkNotNull(v, "Vector4 for dot product");
        return x*v.x + y*v.y + z*v.z + w*v.w;
    }
}