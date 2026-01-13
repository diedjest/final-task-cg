package ru.vsu.cs.finaltaskcg.math.matrix;

import ru.vsu.cs.uvarov_d_p.cg.math.exceptions.MathException;
import ru.vsu.cs.uvarov_d_p.cg.math.vector.Vector4;

/**
 * Класс для работы с матрицами 4x4.
 * Предоставляет основные операции над матрицами размерности 4x4.
 */
public class Matrix4 {

    private final double[][] m = new double[4][4];

    /**
     * Конструктор по умолчанию.
     * Создает нулевую матрицу 4x4.
     */
    public Matrix4() {
        zero();
    }

    /**
     * Конструктор с заданными значениями.
     * @param values двумерный массив 4x4 со значениями матрицы
     * @throws MathException если массив не размерности 4x4
     */
    public Matrix4(double[][] values) {
        if (values.length != 4 || values[0].length != 4)
            throw new MathException("Matrix4 requires 4x4 array");

        for (int i = 0; i < 4; i++)
            System.arraycopy(values[i], 0, m[i], 0, 4);
    }

    /**
     * Получает элемент матрицы по индексам
     * @param row строка матрицы (0-3)
     * @param col столбец матрицы (0-3)
     * @return значение элемента матрицы
     * @throws MathException если индексы выходят за границы
     */
    public double get(int row, int col) {
        if (row < 0 || row >= 4 || col < 0 || col >= 4) {
            throw new MathException("Matrix indices must be in range [0, 3]. Got [" + row + ", " + col + "]");
        }
        return m[row][col];
    }

    /**
     * Устанавливает элемент матрицы по индексам
     * @param row строка матрицы (0-3)
     * @param col столбец матрицы (0-3)
     * @param value значение для установки
     * @throws MathException если индексы выходят за границы
     */
    public void set(int row, int col, double value) {
        if (row < 0 || row >= 4 || col < 0 || col >= 4) {
            throw new MathException("Matrix indices must be in range [0, 3]. Got [" + row + ", " + col + "]");
        }
        m[row][col] = value;
    }

    /**
     * Создает единичную матрицу 4x4
     * @return единичная матрица
     */
    public static Matrix4 identity() {
        Matrix4 r = new Matrix4();
        r.m[0][0] = r.m[1][1] = r.m[2][2] = r.m[3][3] = 1;
        return r;
    }

    /**
     * Заполняет матрицу нулями
     */
    public void zero() {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                m[i][j] = 0;
    }

    /**
     * Складывает матрицы
     * @param other матрица для сложения
     * @return результат сложения матриц
     */
    public Matrix4 add(Matrix4 other) {
        Matrix4 r = new Matrix4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                r.m[i][j] = m[i][j] + other.m[i][j];
        return r;
    }

    /**
     * Вычитает матрицы
     * @param other матрица для вычитания
     * @return результат вычитания матриц
     */
    public Matrix4 sub(Matrix4 other) {
        Matrix4 r = new Matrix4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                r.m[i][j] = m[i][j] - other.m[i][j];
        return r;
    }

    /**
     * Умножает матрицы
     * @param other матрица для умножения
     * @return результат умножения матриц
     */
    public Matrix4 mul(Matrix4 other) {
        Matrix4 r = new Matrix4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                for (int k = 0; k < 4; k++)
                    r.m[i][j] += m[i][k] * other.m[k][j];
        return r;
    }

    /**
     * Умножает матрицу на вектор
     * @param v вектор для умножения
     * @return результат умножения матрицы на вектор
     */
    public Vector4 mul(Vector4 v) {
        return new Vector4(
                m[0][0]*v.getX() + m[0][1]*v.getY() + m[0][2]*v.getZ() + m[0][3]*v.getW(),
                m[1][0]*v.getX() + m[1][1]*v.getY() + m[1][2]*v.getZ() + m[1][3]*v.getW(),
                m[2][0]*v.getX() + m[2][1]*v.getY() + m[2][2]*v.getZ() + m[2][3]*v.getW(),
                m[3][0]*v.getX() + m[3][1]*v.getY() + m[3][2]*v.getZ() + m[3][3]*v.getW()
        );
    }

    /**
     * Транспонирует матрицу
     * @return транспонированная матрица
     */
    public Matrix4 transpose() {
        Matrix4 r = new Matrix4();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                r.m[j][i] = m[i][j];
        return r;
    }

    /**
     * Вычисляет определитель матрицы
     * @return определитель матрицы
     */
    public double determinant() {
        double det = 0;
        for (int k = 0; k < 4; k++) {
            det += m[0][k] * cofactor(0, k);
        }
        return det;
    }

    /**
     * Вычисляет кофактор элемента матрицы
     * @param row строка элемента
     * @param col столбец элемента
     * @return кофактор элемента
     */
    private double cofactor(int row, int col) {
        return ((row + col) % 2 == 0 ? 1 : -1) * minor(row, col);
    }

    /**
     * Вычисляет минор элемента матрицы
     * @param row строка элемента
     * @param col столбец элемента
     * @return минор элемента
     */
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

    /**
     * Вычисляет обратную матрицу
     * @return обратная матрица
     * @throws MathException если матрица вырожденная (определитель равен 0)
     */
    public Matrix4 inverse() {
        double det = determinant();
        if (det == 0)
            throw new MathException("Matrix4 is singular");

        Matrix4 r = new Matrix4();

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                r.m[j][i] = cofactor(i, j) / det;

        return r;
    }

    /**
     * Строковое представление матрицы
     * @return матрица
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix4:\n");
        for (int i = 0; i < 4; i++) {
            sb.append("[ ");
            for (int j = 0; j < 4; j++) {
                sb.append(m[i][j]);
                if (j < 3) {
                    sb.append(", ");
                }
            }
            sb.append(" ]");
            if (i < 3) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}