package ru.vsu.cs.finaltaskcg.math.matrix;

import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;

/**
 * Класс для работы с матрицами 3x3.
 * Предоставляет основные операции над матрицами размерности 3x3.
 */
public class Matrix3 {

    private final double[][] m = new double[3][3];

    /**
     * Конструктор по умолчанию.
     * Создает нулевую матрицу 3x3.
     */
    public Matrix3() {
        zero();
    }

    /**
     * Конструктор с заданными значениями.
     * @param values двумерный массив 3x3 со значениями матрицы
     * @throws MathException если массив не размерности 3x3
     */
    public Matrix3(double[][] values) {
        if (values.length != 3 || values[0].length != 3)
            throw new MathException("Matrix3 requires 3x3 array");

        for (int i = 0; i < 3; i++)
            System.arraycopy(values[i], 0, m[i], 0, 3);
    }

    /**
     * Получает элемент матрицы по индексам
     * @param row строка матрицы (0-2)
     * @param col столбец матрицы (0-2)
     * @return значение элемента матрицы
     * @throws MathException если индексы выходят за границы
     */
    public double get(int row, int col) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            throw new MathException("Matrix indices must be in range [0, 2]. Got [" + row + ", " + col + "]");
        }
        return m[row][col];
    }

    /**
     * Устанавливает элемент матрицы по индексам
     * @param row строка матрицы (0-2)
     * @param col столбец матрицы (0-2)
     * @param value значение для установки
     * @throws MathException если индексы выходят за границы
     */
    public void set(int row, int col, double value) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            throw new MathException("Matrix indices must be in range [0, 2]. Got [" + row + ", " + col + "]");
        }
        m[row][col] = value;
    }

    /**
     * Создает единичную матрицу 3x3
     * @return единичная матрица
     */
    public static Matrix3 identity() {
        Matrix3 r = new Matrix3();
        r.m[0][0] = r.m[1][1] = r.m[2][2] = 1;
        return r;
    }

    /**
     * Заполняет матрицу нулями
     */
    public void zero() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                m[i][j] = 0;
    }

    /**
     * Складывает матрицы
     * @param other матрица для сложения
     * @return результат сложения матриц
     */
    public Matrix3 add(Matrix3 other) {
        Matrix3 r = new Matrix3();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                r.m[i][j] = m[i][j] + other.m[i][j];
        return r;
    }

    /**
     * Вычитает матрицы
     * @param other матрица для вычитания
     * @return результат вычитания матриц
     */
    public Matrix3 sub(Matrix3 other) {
        Matrix3 r = new Matrix3();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                r.m[i][j] = m[i][j] - other.m[i][j];
        return r;
    }

    /**
     * Умножает матрицы
     * @param other матрица для умножения
     * @return результат умножения матриц
     */
    public Matrix3 mul(Matrix3 other) {
        Matrix3 r = new Matrix3();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    r.m[i][j] += m[i][k] * other.m[k][j];
        return r;
    }

    /**
     * Умножает матрицу на вектор
     * @param v вектор для умножения
     * @return результат умножения матрицы на вектор
     */
    public Vector3 mul(Vector3 v) {
        return new Vector3(
                m[0][0]*v.getX() + m[0][1]*v.getY() + m[0][2]*v.getZ(),
                m[1][0]*v.getX() + m[1][1]*v.getY() + m[1][2]*v.getZ(),
                m[2][0]*v.getX() + m[2][1]*v.getY() + m[2][2]*v.getZ()
        );
    }

    /**
     * Транспонирует матрицу
     * @return транспонированная матрица
     */
    public Matrix3 transpose() {
        Matrix3 r = new Matrix3();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                r.m[j][i] = m[i][j];
        return r;
    }

    /**
     * Вычисляет определитель матрицы
     * @return определитель матрицы
     */
    public double determinant() {
        return
                m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1]) -
                        m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0]) +
                        m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0]);
    }

    /**
     * Вычисляет обратную матрицу
     * @return обратная матрица
     * @throws MathException если матрица вырожденная (определитель равен 0)
     */
    public Matrix3 inverse() {
        double det = determinant();
        if (det == 0)
            throw new MathException("Matrix3 is singular");

        Matrix3 r = new Matrix3();

        r.m[0][0] =  (m[1][1] * m[2][2] - m[1][2] * m[2][1]) / det;
        r.m[0][1] = -(m[0][1] * m[2][2] - m[0][2] * m[2][1]) / det;
        r.m[0][2] =  (m[0][1] * m[1][2] - m[0][2] * m[1][1]) / det;

        r.m[1][0] = -(m[1][0] * m[2][2] - m[1][2] * m[2][0]) / det;
        r.m[1][1] =  (m[0][0] * m[2][2] - m[0][2] * m[2][0]) / det;
        r.m[1][2] = -(m[0][0] * m[1][2] - m[0][2] * m[1][0]) / det;

        r.m[2][0] =  (m[1][0] * m[2][1] - m[1][1] * m[2][0]) / det;
        r.m[2][1] = -(m[0][0] * m[2][1] - m[0][1] * m[2][0]) / det;
        r.m[2][2] =  (m[0][0] * m[1][1] - m[0][1] * m[1][0]) / det;

        return r;
    }

    /**
     * Строковое представление матрицы
     * @return матрица
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix3:\n");
        for (int i = 0; i < 3; i++) {
            sb.append("[ ");
            for (int j = 0; j < 3; j++) {
                sb.append(m[i][j]);
                if (j < 2) {
                    sb.append(", ");
                }
            }
            sb.append(" ]");
            if (i < 2) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}