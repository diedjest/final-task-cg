package ru.vsu.cs.finaltaskcg.math.matrix;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;
import ru.vsu.cs.finaltaskcg.math.vector.Vector4;

import static org.junit.jupiter.api.Assertions.*;
import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

class Matrix4Test {

    @Test
    void testDefaultConstructor() {
        Matrix4 matrix = new Matrix4();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0.0, matrix.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testArrayConstructor() {
        double[][] values = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        };

        Matrix4 matrix = new Matrix4(values);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(values[i][j], matrix.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testArrayConstructorWithInvalidDimensions() {
        double[][] invalidValues1 = {{1, 2}, {3, 4}, {5, 6}, {7, 8}}; // 4x2
        double[][] invalidValues2 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}}; // 4x3
        double[][] invalidValues3 = {{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}, {11, 12, 13, 14, 15}, {16, 17, 18, 19, 20}}; // 4x5
        double[][] invalidValues4 = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}}; // 3x4

        assertThrows(MathException.class, () -> new Matrix4(invalidValues1));
        assertThrows(MathException.class, () -> new Matrix4(invalidValues2));
        assertThrows(MathException.class, () -> new Matrix4(invalidValues3));
        assertThrows(MathException.class, () -> new Matrix4(invalidValues4));
    }

    @Test
    void testArrayConstructorWithNull() {
        double[][] nullArray = null;
        double[][] nullRow = {{1, 2, 3, 4}, null, {9, 10, 11, 12}, {13, 14, 15, 16}};

        assertThrows(MathException.class, () -> new Matrix4(nullArray));
        assertThrows(MathException.class, () -> new Matrix4(nullRow));
    }

    @Test
    void testGetAndSet() {
        Matrix4 matrix = new Matrix4();

        matrix.set(0, 0, 1.5);
        matrix.set(1, 2, -2.7);
        matrix.set(2, 1, 3.14);
        matrix.set(3, 3, -5.2);

        assertEquals(1.5, matrix.get(0, 0), EPSILON);
        assertEquals(-2.7, matrix.get(1, 2), EPSILON);
        assertEquals(3.14, matrix.get(2, 1), EPSILON);
        assertEquals(-5.2, matrix.get(3, 3), EPSILON);
    }

    @Test
    void testGetWithInvalidIndexes() {
        Matrix4 matrix = new Matrix4();

        assertThrows(MathException.class, () -> matrix.get(-1, 0));
        assertThrows(MathException.class, () -> matrix.get(4, 0));
        assertThrows(MathException.class, () -> matrix.get(0, -1));
        assertThrows(MathException.class, () -> matrix.get(0, 4));
        assertThrows(MathException.class, () -> matrix.get(4, 4));
        assertThrows(MathException.class, () -> matrix.get(-1, -1));
    }

    @Test
    void testSetWithInvalidIndexes() {
        Matrix4 matrix = new Matrix4();

        assertThrows(MathException.class, () -> matrix.set(-1, 0, 1.0));
        assertThrows(MathException.class, () -> matrix.set(4, 0, 1.0));
        assertThrows(MathException.class, () -> matrix.set(0, -1, 1.0));
        assertThrows(MathException.class, () -> matrix.set(0, 4, 1.0));
        assertThrows(MathException.class, () -> matrix.set(4, 4, 1.0));
        assertThrows(MathException.class, () -> matrix.set(-1, -1, 1.0));
    }

    @Test
    void testIdentity() {
        Matrix4 identity = Matrix4.identity();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j) {
                    assertEquals(1.0, identity.get(i, j), EPSILON);
                } else {
                    assertEquals(0.0, identity.get(i, j), EPSILON);
                }
            }
        }
    }

    @Test
    void testZero() {
        Matrix4 matrix = new Matrix4();

        double[][] values = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix.set(i, j, values[i][j]);
            }
        }

        assertNotEquals(0.0, matrix.get(0, 0), EPSILON);

        matrix.zero();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0.0, matrix.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testAdd() {
        double[][] values1 = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        };

        double[][] values2 = {
                {16.0, 15.0, 14.0, 13.0},
                {12.0, 11.0, 10.0, 9.0},
                {8.0, 7.0, 6.0, 5.0},
                {4.0, 3.0, 2.0, 1.0}
        };

        Matrix4 matrix1 = new Matrix4(values1);
        Matrix4 matrix2 = new Matrix4(values2);
        Matrix4 result = matrix1.add(matrix2);

        assertNotSame(matrix1, result);
        assertNotSame(matrix2, result);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(values1[i][j] + values2[i][j], result.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testAddWithNullThrows() {
        Matrix4 matrix1 = new Matrix4();

        assertThrows(MathException.class, () -> matrix1.add(null));
    }

    @Test
    void testSub() {
        double[][] values1 = {
                {10.0, 9.0, 8.0, 7.0},
                {6.0, 5.0, 4.0, 3.0},
                {2.0, 1.0, 0.0, -1.0},
                {-2.0, -3.0, -4.0, -5.0}
        };

        double[][] values2 = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        };

        Matrix4 matrix1 = new Matrix4(values1);
        Matrix4 matrix2 = new Matrix4(values2);
        Matrix4 result = matrix1.sub(matrix2);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(values1[i][j] - values2[i][j], result.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testSubWithNullThrows() {
        Matrix4 matrix1 = new Matrix4();

        assertThrows(MathException.class, () -> matrix1.sub(null));
    }

    @Test
    void testMulMatrix() {
        double[][] values1 = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        };

        double[][] values2 = {
                {16.0, 15.0, 14.0, 13.0},
                {12.0, 11.0, 10.0, 9.0},
                {8.0, 7.0, 6.0, 5.0},
                {4.0, 3.0, 2.0, 1.0}
        };

        double[][] expected = {
                {80.0, 70.0, 60.0, 50.0},
                {240.0, 214.0, 188.0, 162.0},
                {400.0, 358.0, 316.0, 274.0},
                {560.0, 502.0, 444.0, 386.0}
        };

        Matrix4 matrix1 = new Matrix4(values1);
        Matrix4 matrix2 = new Matrix4(values2);
        Matrix4 result = matrix1.mul(matrix2);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(expected[i][j], result.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testMulMatrixWithNullThrows() {
        Matrix4 matrix1 = new Matrix4();
        Matrix4 matrix2 = null;

        assertThrows(MathException.class, () -> matrix1.mul(matrix2));
    }

    @Test
    void testMulVector() {
        double[][] matrixValues = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        };

        Vector4 vector = new Vector4(2.0, 3.0, 4.0, 5.0);

        Matrix4 matrix = new Matrix4(matrixValues);
        Vector4 result = matrix.mul(vector);

        assertEquals(40.0, result.getX(), EPSILON);
        assertEquals(96.0, result.getY(), EPSILON);
        assertEquals(152.0, result.getZ(), EPSILON);
        assertEquals(208.0, result.getW(), EPSILON);
    }

    @Test
    void testMulVectorWithNullThrows() {
        Matrix4 matrix = new Matrix4();
        Vector4 vector = null;

        assertThrows(MathException.class, () -> matrix.mul(vector));
    }

    @Test
    void testMulVectorWithZeroMatrix() {
        Matrix4 zero = new Matrix4();
        Vector4 vector = new Vector4(1.0, 2.0, 3.0, 4.0);
        Vector4 result = zero.mul(vector);

        assertEquals(0.0, result.getX(), EPSILON);
        assertEquals(0.0, result.getY(), EPSILON);
        assertEquals(0.0, result.getZ(), EPSILON);
        assertEquals(0.0, result.getW(), EPSILON);
    }

    @Test
    void testTranspose() {
        double[][] values = {
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        };

        double[][] expected = {
                {1.0, 5.0, 9.0, 13.0},
                {2.0, 6.0, 10.0, 14.0},
                {3.0, 7.0, 11.0, 15.0},
                {4.0, 8.0, 12.0, 16.0}
        };

        Matrix4 matrix = new Matrix4(values);
        Matrix4 transposed = matrix.transpose();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(expected[i][j], transposed.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testTransposeIdentity() {
        Matrix4 identity = Matrix4.identity();
        Matrix4 transposed = identity.transpose();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(identity.get(i, j), transposed.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testTransposeTwice() {
        Matrix4 matrix = new Matrix4(new double[][]{
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        });

        Matrix4 transposedTwice = matrix.transpose().transpose();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(matrix.get(i, j), transposedTwice.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testDeterminant() {
        double[][] values = {
                {1.0, 0.0, 2.0, -1.0},
                {3.0, 0.0, 0.0, 5.0},
                {2.0, 1.0, 4.0, -3.0},
                {1.0, 0.0, 5.0, 0.0}
        };

        Matrix4 matrix = new Matrix4(values);
        double det = matrix.determinant();

        assertEquals(30.0, det, EPSILON);
    }

    @Test
    void testDeterminantIdentity() {
        Matrix4 identity = Matrix4.identity();
        double det = identity.determinant();

        assertEquals(1.0, det, EPSILON);
    }

    @Test
    void testDeterminantZero() {
        double[][] values = {
                {1.0, 2.0, 3.0, 4.0},
                {2.0, 4.0, 6.0, 8.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0}
        };

        Matrix4 matrix = new Matrix4(values);
        double det = matrix.determinant();

        assertEquals(0.0, det, EPSILON);
    }

    @Test
    void testDeterminantVerySmall() {
        double[][] values = {
                {1e-10, 2e-10, 3e-10, 4e-10},
                {2e-10, 4e-10, 6e-10, 8e-10},
                {5e-10, 6e-10, 7e-10, 8e-10},
                {9e-10, 10e-10, 11e-10, 12e-10}
        };

        Matrix4 matrix = new Matrix4(values);
        double det = matrix.determinant();

        // Определитель должен быть очень близок к 0
        assertEquals(0.0, det, EPSILON);
    }

    @Test
    void testInverse() {
        double[][] values = {
                {2.0, 0.0, 0.0, 0.0},
                {0.0, 2.0, 0.0, 0.0},
                {0.0, 0.0, 2.0, 0.0},
                {0.0, 0.0, 0.0, 2.0}
        };

        Matrix4 matrix = new Matrix4(values);
        Matrix4 inverse = matrix.inverse();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j) {
                    assertEquals(0.5, inverse.get(i, j), EPSILON);
                } else {
                    assertEquals(0.0, inverse.get(i, j), EPSILON);
                }
            }
        }

        Matrix4 identityCheck = matrix.mul(inverse);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == j) {
                    assertEquals(1.0, identityCheck.get(i, j), EPSILON);
                } else {
                    assertEquals(0.0, identityCheck.get(i, j), EPSILON);
                }
            }
        }
    }

    @Test
    void testInverseSingularMatrix() {
        double[][] values = {
                {1.0, 2.0, 3.0, 4.0},
                {2.0, 4.0, 6.0, 8.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0}
        };

        Matrix4 matrix = new Matrix4(values);

        assertThrows(MathException.class, matrix::inverse);
    }

    @Test
    void testInverseVeryCloseToSingular() {
        double[][] values = {
                {1e-10, 0, 0, 0},
                {0, 1e-10, 0, 0},
                {0, 0, 1e-10, 0},
                {0, 0, 0, 1e-10}
        };

        Matrix4 matrix = new Matrix4(values);

        // Очень маленький определитель (1e-40) < EPSILON
        assertThrows(MathException.class, matrix::inverse);
    }

    @Test
    void testIdentityProperties() {
        Matrix4 identity = Matrix4.identity();
        Matrix4 randomMatrix = new Matrix4(new double[][]{
                {1.0, 2.0, 3.0, 4.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0},
                {13.0, 14.0, 15.0, 16.0}
        });

        Matrix4 result1 = identity.mul(randomMatrix);
        Matrix4 result2 = randomMatrix.mul(identity);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(randomMatrix.get(i, j), result1.get(i, j), EPSILON);
                assertEquals(randomMatrix.get(i, j), result2.get(i, j), EPSILON);
            }
        }

        assertEquals(1.0, identity.determinant(), EPSILON);

        Matrix4 transposedIdentity = identity.transpose();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(identity.get(i, j), transposedIdentity.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testMatrixMultiplicationAssociativity() {
        Matrix4 A = new Matrix4(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        });

        Matrix4 B = new Matrix4(new double[][]{
                {16, 15, 14, 13},
                {12, 11, 10, 9},
                {8, 7, 6, 5},
                {4, 3, 2, 1}
        });

        Matrix4 C = new Matrix4(new double[][]{
                {0.5, 1.5, 2.5, 3.5},
                {4.5, 5.5, 6.5, 7.5},
                {8.5, 9.5, 10.5, 11.5},
                {12.5, 13.5, 14.5, 15.5}
        });

        // (A × B) × C
        Matrix4 left = A.mul(B).mul(C);
        // A × (B × C)
        Matrix4 right = A.mul(B.mul(C));

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(left.get(i, j), right.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testTransposeInverseProperty() {
        // Для невырожденной матрицы: (A^-1)^T = (A^T)^-1
        Matrix4 A = new Matrix4(new double[][]{
                {1, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 3, 0},
                {0, 0, 0, 4}
        });

        Matrix4 A_inv = A.inverse();
        Matrix4 A_transpose = A.transpose();
        Matrix4 A_transpose_inv = A_transpose.inverse();
        Matrix4 A_inv_transpose = A_inv.transpose();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(A_transpose_inv.get(i, j), A_inv_transpose.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testDeterminantAfterScaling() {
        Matrix4 matrix = new Matrix4(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        });

        double originalDet = matrix.determinant();

        // Масштабирование строки 0 на 2
        Matrix4 scaled = new Matrix4(new double[][]{
                {2, 4, 6, 8},  // Строка 0 * 2
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        });

        double scaledDet = scaled.determinant();

        // Определитель должен умножиться на 2
        assertEquals(2 * originalDet, scaledDet, EPSILON);
    }

    @Test
    void testZeroMatrixProperties() {
        Matrix4 zero = new Matrix4(); // Уже нулевая матрица
        Matrix4 random = new Matrix4(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        });

        // Умножение на нулевую матрицу
        Matrix4 result1 = zero.mul(random);
        Matrix4 result2 = random.mul(zero);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0.0, result1.get(i, j), EPSILON);
                assertEquals(0.0, result2.get(i, j), EPSILON);
            }
        }

        // Сложение с нулевой матрицей
        Matrix4 result3 = random.add(zero);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(random.get(i, j), result3.get(i, j), EPSILON);
            }
        }

        // Определитель нулевой матрицы
        assertEquals(0.0, zero.determinant(), EPSILON);
    }

    @Test
    void testInverseMultiplicationProperties() {
        Matrix4 A = new Matrix4(new double[][]{
                {2, 1, 1, 3},
                {1, -1, 0, 1},
                {0, 2, 1, 1},
                {1, 0, -1, 2}
        });

        Matrix4 B = new Matrix4(new double[][]{
                {1, 2, 0, 1},
                {0, 1, 1, 2},
                {2, 0, 1, 0},
                {1, 1, 2, 1}
        });

        // (AB)^-1 = B^-1 * A^-1
        Matrix4 AB = A.mul(B);
        Matrix4 AB_inv = AB.inverse();
        Matrix4 B_inv_A_inv = B.inverse().mul(A.inverse());

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(AB_inv.get(i, j), B_inv_A_inv.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testTransposeMultiplicationProperty() {
        Matrix4 A = new Matrix4(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        });

        Matrix4 B = new Matrix4(new double[][]{
                {16, 15, 14, 13},
                {12, 11, 10, 9},
                {8, 7, 6, 5},
                {4, 3, 2, 1}
        });

        // (AB)^T = B^T * A^T
        Matrix4 AB = A.mul(B);
        Matrix4 AB_transpose = AB.transpose();
        Matrix4 B_transpose_A_transpose = B.transpose().mul(A.transpose());

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(AB_transpose.get(i, j), B_transpose_A_transpose.get(i, j), EPSILON);
            }
        }
    }
}