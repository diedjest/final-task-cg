package ru.vsu.cs.finaltaskcg.math.gaussian;

import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;
import ru.vsu.cs.finaltaskcg.math.validation.MathValidator;

import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

public class GaussianSolver {

    public static double[] solve(double[][] A, double[] b) {
        MathValidator.checkNotNull(A, "Matrix A");
        MathValidator.checkNotNull(b, "Vector b");

        int n = A.length;

        if (n != b.length)
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

            if (Math.abs(M[i][i]) < EPSILON)
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