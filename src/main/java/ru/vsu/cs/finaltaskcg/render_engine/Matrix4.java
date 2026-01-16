package ru.vsu.cs.finaltaskcg.render_engine;

public class Matrix4 {
    private double[][] matrix;

    public Matrix4() {
        matrix = new double[4][4];
    }

    public Matrix4(double[][] matrix) {
        this.matrix = matrix;
    }

    public static Matrix4 identity() {
        double[][] identity = new double[4][4];
        for (int i = 0; i < 4; i++) {
            identity[i][i] = 1.0;
        }
        return new Matrix4(identity);
    }

    public double get(int row, int col) {
        return matrix[row][col];
    }

    public void set(int row, int col, double value) {
        matrix[row][col] = value;
    }

    public Matrix4 mul(Matrix4 other) {
        double[][] result = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result[i][j] += matrix[i][k] * other.matrix[k][j];
                }
            }
        }
        return new Matrix4(result);
    }
}
