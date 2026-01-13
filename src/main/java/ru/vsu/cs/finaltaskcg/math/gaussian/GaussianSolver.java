package ru.vsu.cs.finaltaskcg.math.gaussian;

import ru.vsu.cs.uvarov_d_p.cg.math.exceptions.MathException;

/**
 * Класс для решения систем линейных уравнений методом Гаусса.
 * Реализует метод исключения Гаусса с выбором главного элемента.
 */
public class GaussianSolver {

    /**
     * Решает систему линейных уравнений методом Гаусса
     * @param A матрица коэффициентов системы
     * @param b вектор правых частей
     * @return вектор решения системы
     * @throws MathException если размеры матрицы и вектора не совпадают
     * @throws MathException если система не имеет единственного решения
     */
    public static double[] solve(double[][] A, double[] b) {
        int n = A.length;

        if (A.length != b.length)
            throw new MathException("Matrix and vector sizes do not match");

        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        for (int i = 0; i < n; i++) {
            int maxRow = i;
            for (int k = i + 1; k < n; k++)
                if (Math.abs(M[k][i]) > Math.abs(M[maxRow][i]))
                    maxRow = k;

            double[] tmp = M[i];
            M[i] = M[maxRow];
            M[maxRow] = tmp;

            if (M[i][i] == 0)
                throw new MathException("System has no unique solution");

            double diag = M[i][i];
            for (int j = i; j <= n; j++)
                M[i][j] /= diag;

            for (int k = 0; k < n; k++) {
                if (k == i) continue;

                double factor = M[k][i];
                for (int j = i; j <= n; j++)
                    M[k][j] -= factor * M[i][j];
            }
        }

        double[] result = new double[n];
        for (int i = 0; i < n; i++)
            result[i] = M[i][n];

        return result;
    }
}