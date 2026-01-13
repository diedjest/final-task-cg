package ru.vsu.cs.finaltaskcg.math.matrix;

import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;
import ru.vsu.cs.finaltaskcg.math.vector.Vector4;
import ru.vsu.cs.finaltaskcg.math.validation.MathValidator;

public class Matrix4 {
    private final int SIZE = 4;
    private final double[][] m = new double[SIZE][SIZE];

    public Matrix4() {
        zero();
    }

    public Matrix4(double[][] values) {
        MathValidator.checkArraySize(values, SIZE, SIZE);

        for (int i = 0; i < 4; i++)
            System.arraycopy(values[i], 0, m[i], 0, 4);
    }

    public Matrix4(Matrix4 other) {
        MathValidator.checkNotNull(other, "Matrix4 for copy");

        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(other.m[i], 0, this.m[i], 0, SIZE);
        }
    }

    public double get(int row, int col) {
        MathValidator.checkMatrixIndex(row, col, SIZE, SIZE);
        return m[row][col];
    }

    public void set(int row, int col, double value) {
        MathValidator.checkMatrixIndex(row, col, SIZE, SIZE);
        m[row][col] = value;
    }

    public static Matrix4 identity() {
        Matrix4 r = new Matrix4();
        r.m[0][0] = r.m[1][1] = r.m[2][2] = r.m[3][3] = 1;
        return r;
    }

    public void zero() {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                m[i][j] = 0;
    }

    public Matrix4 add(Matrix4 other) {
        MathValidator.checkNotNull(other, "Matrix4 for addition");
        Matrix4 r = new Matrix4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                r.m[i][j] = m[i][j] + other.m[i][j];
        return r;
    }

    public Matrix4 sub(Matrix4 other) {
        MathValidator.checkNotNull(other, "Matrix4 for subtraction");
        Matrix4 r = new Matrix4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                r.m[i][j] = m[i][j] - other.m[i][j];
        return r;
    }

    public Matrix4 mul(Matrix4 other) {
        MathValidator.checkNotNull(other, "Matrix4 for multiplication");
        Matrix4 r = new Matrix4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                for (int k = 0; k < 4; k++)
                    r.m[i][j] += m[i][k] * other.m[k][j];
        return r;
    }

    public Vector4 mul(Vector4 v) {
        MathValidator.checkNotNull(v, "Vector4");
        return new Vector4(
                m[0][0]*v.getX() + m[0][1]*v.getY() + m[0][2]*v.getZ() + m[0][3]*v.getW(),
                m[1][0]*v.getX() + m[1][1]*v.getY() + m[1][2]*v.getZ() + m[1][3]*v.getW(),
                m[2][0]*v.getX() + m[2][1]*v.getY() + m[2][2]*v.getZ() + m[2][3]*v.getW(),
                m[3][0]*v.getX() + m[3][1]*v.getY() + m[3][2]*v.getZ() + m[3][3]*v.getW()
        );
    }

    public Matrix4 transpose() {
        Matrix4 r = new Matrix4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                r.m[j][i] = m[i][j];
        return r;
    }

    public double determinant() {
        double det = 0;
        for (int k = 0; k < 4; k++) {
            det += m[0][k] * cofactor(0, k);
        }
        return det;
    }

    private double cofactor(int row, int col) {
        return ((row + col) % 2 == 0 ? 1 : -1) * minor(row, col);
    }

    private double minor(int row, int col) {
        double[][] sub = new double[3][3];
        int r = 0;
        for (int i = 0; i < 4; i++) {
            if (i == row) continue;
            int c = 0;
            for (int j = 0; j < 4; j++) {
                if (j == col) continue;
                sub[r][c] = m[i][j];
                c++;
            }
            r++;
        }
        return new Matrix3(sub).determinant();
    }

    public Matrix4 inverse() {
        double det = determinant();
        MathValidator.checkDeterminant(det);

        Matrix4 r = new Matrix4();

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                r.m[j][i] = cofactor(i, j) / det;

        return r;
    }
}