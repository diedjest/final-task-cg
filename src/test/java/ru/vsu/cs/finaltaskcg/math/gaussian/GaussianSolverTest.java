package ru.vsu.cs.finaltaskcg.math.gaussian;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

public class GaussianSolverTest {

    @Test
    void testSolve2x2() {
        double[][] A = {
                {2, 1},
                {1, -1}
        };
        double[] b = {5, 1};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(2, sol.length);
        assertEquals(2, sol[0], EPSILON);
        assertEquals(1, sol[1], EPSILON);
    }

    @Test
    void testSolve2x2WithNegativeNumbers() {
        double[][] A = {
                {3, -2},
                {1, 4}
        };
        double[] b = {7, 9};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(2, sol.length);
        assertEquals(23.0 / 7.0, sol[0], EPSILON);
        assertEquals(10.0 / 7.0, sol[1], EPSILON);
    }

    @Test
    void testSolve3x3() {
        double[][] A = {
                {1, 1, 1},
                {0, 2, 5},
                {2, 5, -1}
        };

        double[] b = {6, -4, 27};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(3, sol.length);
        assertEquals(5, sol[0], EPSILON);
        assertEquals(3, sol[1], EPSILON);
        assertEquals(-2, sol[2], EPSILON);
    }

    @Test
    void testSolve3x3WithZeros() {
        double[][] A = {
                {2, 0, -1},
                {0, 3, 2},
                {1, 1, 0}
        };
        double[] b = {5, 11, 4};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(3, sol.length);
        assertEquals(3, sol[0], EPSILON);
        assertEquals(1, sol[1], EPSILON);
        assertEquals(1, sol[2], EPSILON);
    }

    @Test
    void testSolve4x4() {
        double[][] A = {
                {7, 9, 4, 2},
                {2, -2, 1, 1},
                {5, 6, 3, 2},
                {2, 3, 1, 1}
        };

        double[] b = {2, 6, 3, 0};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(4, sol.length);
        assertEquals(-0.4, sol[0], EPSILON);
        assertEquals(-1.2, sol[1], EPSILON);
        assertEquals(3.4, sol[2], EPSILON);
        assertEquals(1, sol[3], EPSILON);
    }

    @Test
    void testSolve4x4Identity() {
        double[][] A = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        double[] b = {2, 3, 4, 5};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(4, sol.length);
        assertEquals(2, sol[0], EPSILON);
        assertEquals(3, sol[1], EPSILON);
        assertEquals(4, sol[2], EPSILON);
        assertEquals(5, sol[3], EPSILON);
    }

    @Test
    void testPivotingRequired() {
        double[][] A = {
                {0, 1},
                {2, 3}
        };

        double[] b = {2, 7};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(0.5, sol[0], EPSILON);
        assertEquals(2.0, sol[1], EPSILON);
    }

    @Test
    void testPivotingRequired3x3() {
        double[][] A = {
                {0, 0, 1},
                {0, 2, 3},
                {1, 2, 3}
        };
        double[] b = {1, 5, 6};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(3, sol.length);
        assertEquals(-1, sol[0], EPSILON);
        assertEquals(1, sol[1], EPSILON);
        assertEquals(1, sol[2], EPSILON);
    }

    @Test
    void testMismatchedSizes() {
        double[][] A = {
                {1, 2},
                {3, 4}
        };

        double[] b = {5, 6, 7};

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testMismatchedSizesReverse() {
        double[][] A = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };

        double[] b = {1, 2}; // Не хватает одного элемента

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testSingularSystem() {
        double[][] A = {
                {1, 1},
                {2, 2}
        };

        double[] b = {2, 4};

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testSingularSystem3x3() {
        double[][] A = {
                {1, 2, 3},
                {2, 4, 6},
                {3, 6, 9}
        };

        double[] b = {1, 2, 3};

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testNearSingularSystem() {
        double[][] A = {
                {1, 1},
                {1, 1 + 1e-15} // Очень близко к сингулярной
        };

        double[] b = {2, 2};

        // Матрица технически несингулярна, но определитель очень мал
        // Должен выбросить исключение из-за EPSILON проверки
        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testNullMatrix() {
        double[][] A = null;
        double[] b = {1, 2};

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testNullVector() {
        double[][] A = {
                {1, 2},
                {3, 4}
        };

        double[] b = null;

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testEmptyMatrix() {
        double[][] A = {};
        double[] b = {};

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testNonSquareMatrix() {
        double[][] A = {
                {1, 2, 3},
                {4, 5, 6}
        };

        double[] b = {1, 2};

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testMatrixWithNullRow() {
        double[][] A = {
                {1, 2},
                null,
        };

        double[] b = {1, 2};

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testMatrixWithWrongRowSize() {
        double[][] A = {
                {1, 2, 3},
                {4, 5}, // Неправильный размер строки
                {7, 8, 9}
        };

        double[] b = {1, 2, 3};

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testLargeNumbers() {
        double[][] A = {
                {1e6, 2e6},
                {3e6, 4e6}
        };

        double[] b = {5e6, 6e6};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(2, sol.length);
        assertEquals(-2e6, sol[0], EPSILON);
        assertEquals(3.5, sol[1], EPSILON);
    }

    @Test
    void testVerySmallNumbers() {
        double[][] A = {
                {1e-10, 2e-10},
                {3e-10, 4e-10}
        };

        double[] b = {5e-10, 6e-10};

        // Матрица близка к сингулярной (определитель ~ -2e-20)
        // Должен выбросить исключение из-за EPSILON проверки
        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }

    @Test
    void testBackSubstitution() {
        // Верхнетреугольная матрица (метод Гаусса уже выполнил прямой ход)
        double[][] A = {
                {1, 2, 3},
                {0, 1, 4},
                {0, 0, 1}
        };

        double[] b = {14, 8, 2};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(3, sol.length);
        assertEquals(1, sol[0], EPSILON);
        assertEquals(0, sol[1], EPSILON);
        assertEquals(2, sol[2], EPSILON);
    }

    @Test
    void testSolutionVerification() {
        // Проверяем, что решение действительно удовлетворяет системе
        double[][] A = {
                {2, 1, -1},
                {-3, -1, 2},
                {-2, 1, 2}
        };

        double[] b = {8, -11, -3};

        double[] sol = GaussianSolver.solve(A, b);

        // Проверяем Ax = b
        double[] Ax = new double[3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Ax[i] += A[i][j] * sol[j];
            }
        }

        assertEquals(b[0], Ax[0], EPSILON);
        assertEquals(b[1], Ax[1], EPSILON);
        assertEquals(b[2], Ax[2], EPSILON);
    }

    @Test
    void testRandomSystem() {
        // Случайная система 3x3
        double[][] A = {
                {3.5, 2.1, -1.7},
                {0.8, 4.2, 3.3},
                {-2.1, 1.4, 5.6}
        };

        double[] b = {7.2, 12.5, 8.9};

        double[] sol = GaussianSolver.solve(A, b);

        // Проверяем, что решение удовлетворяет системе
        for (int i = 0; i < 3; i++) {
            double sum = 0;
            for (int j = 0; j < 3; j++) {
                sum += A[i][j] * sol[j];
            }
            assertEquals(b[i], sum, EPSILON * 100); // Более мягкая проверка для случайных чисел
        }
    }

    @Test
    void testDiagonalDominantSystem() {
        // Матрица с диагональным преобладанием (устойчива к ошибкам округления)
        double[][] A = {
                {4, 1, 1},
                {1, 5, 2},
                {1, 2, 6}
        };

        double[] b = {6, 8, 9};

        double[] sol = GaussianSolver.solve(A, b);

        // Проверяем решение
        for (int i = 0; i < 3; i++) {
            double sum = 0;
            for (int j = 0; j < 3; j++) {
                sum += A[i][j] * sol[j];
            }
            assertEquals(b[i], sum, EPSILON);
        }
    }

    @Test
    void testIllConditionedSystem() {
        // Плохо обусловленная система (маленькое изменение в данных → большое изменение в решении)
        double[][] A = {
                {1, 1},
                {1, 1.0001}
        };

        double[] b = {2, 2.0001};

        // Эта система должна решиться, но может быть численно неустойчивой
        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(2, sol.length);
        // Решение должно быть близко к (1, 1)
        assertEquals(1, sol[0], 1e-4); // Меньшая точность из-за плохой обусловленности
        assertEquals(1, sol[1], 1e-4);
    }

    @Test
    void testMultipleRightHandSides() {
        // Одна и та же матрица, разные правые части
        double[][] A = {
                {2, 1},
                {1, 3}
        };

        double[] b1 = {5, 5};
        double[] b2 = {1, 2};
        double[] b3 = {3, 4};

        double[] sol1 = GaussianSolver.solve(A, b1);
        double[] sol2 = GaussianSolver.solve(A, b2);
        double[] sol3 = GaussianSolver.solve(A, b3);

        // Проверяем решения
        assertEquals(2, sol1[0], EPSILON);
        assertEquals(1, sol1[1], EPSILON);

        assertEquals(0.2, sol2[0], EPSILON);
        assertEquals(0.6, sol2[1], EPSILON);

        assertEquals(1, sol3[0], EPSILON);
        assertEquals(1, sol3[1], EPSILON);
    }
}