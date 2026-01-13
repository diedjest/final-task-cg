package ru.vsu.cs.finaltaskcg.math.matrix;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;

import static org.junit.jupiter.api.Assertions.*;
import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

class Matrix3Test {

    @Test
    void testDefaultConstructor() {
        Matrix3 matrix = new Matrix3();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(0.0, matrix.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testArrayConstructor() {
        double[][] values = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };

        Matrix3 matrix = new Matrix3(values);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(values[i][j], matrix.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testArrayConstructorWithInvalidDimensions() {
        double[][] invalidValues1 = {{1, 2}, {3, 4}};
        double[][] invalidValues2 = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
        double[][] invalidValues3 = {{1, 2, 3}, {4, 5, 6}};

        assertThrows(MathException.class, () -> new Matrix3(invalidValues1));
        assertThrows(MathException.class, () -> new Matrix3(invalidValues2));
        assertThrows(MathException.class, () -> new Matrix3(invalidValues3));
    }

    @Test
    void testArrayConstructorWithNull() {
        double[][] nullArray = null;
        double[][] nullRow = {{1, 2, 3}, null, {7, 8, 9}};

        assertThrows(MathException.class, () -> new Matrix3(nullArray));
        assertThrows(MathException.class, () -> new Matrix3(nullRow));
    }

    @Test
    void testGetAndSet() {
        Matrix3 matrix = new Matrix3();

        matrix.set(0, 0, 1.5);
        matrix.set(1, 2, -2.7);
        matrix.set(2, 1, 3.14);

        assertEquals(1.5, matrix.get(0, 0), EPSILON);
        assertEquals(-2.7, matrix.get(1, 2), EPSILON);
        assertEquals(3.14, matrix.get(2, 1), EPSILON);
    }

    @Test
    void testGetWithInvalidIndexes() {
        Matrix3 matrix = new Matrix3();

        assertThrows(MathException.class, () -> matrix.get(-1, 0));
        assertThrows(MathException.class, () -> matrix.get(3, 0));
        assertThrows(MathException.class, () -> matrix.get(0, -1));
        assertThrows(MathException.class, () -> matrix.get(0, 3));
    }

    @Test
    void testSetWithInvalidIndexes() {
        Matrix3 matrix = new Matrix3();

        assertThrows(MathException.class, () -> matrix.set(-1, 0, 1.0));
        assertThrows(MathException.class, () -> matrix.set(3, 0, 1.0));
        assertThrows(MathException.class, () -> matrix.set(0, -1, 1.0));
        assertThrows(MathException.class, () -> matrix.set(0, 3, 1.0));
    }

    @Test
    void testIdentity() {
        Matrix3 identity = Matrix3.identity();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
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
        Matrix3 matrix = new Matrix3();

        double[][] values = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix.set(i, j, values[i][j]);
            }
        }

        assertNotEquals(0.0, matrix.get(0, 0), EPSILON);

        matrix.zero();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(0.0, matrix.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testAdd() {
        double[][] values1 = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };

        double[][] values2 = {
                {9.0, 8.0, 7.0},
                {6.0, 5.0, 4.0},
                {3.0, 2.0, 1.0}
        };

        Matrix3 matrix1 = new Matrix3(values1);
        Matrix3 matrix2 = new Matrix3(values2);
        Matrix3 result = matrix1.add(matrix2);

        assertNotSame(matrix1, result);
        assertNotSame(matrix2, result);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(values1[i][j] + values2[i][j], result.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testAddWithNullThrows() {
        Matrix3 matrix1 = new Matrix3();

        assertThrows(MathException.class, () -> matrix1.add(null));
    }

    @Test
    void testSub() {
        double[][] values1 = {
                {10.0, 8.0, 6.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };

        double[][] values2 = {
                {1.0, 2.0, 3.0},
                {1.0, 2.0, 3.0},
                {1.0, 2.0, 3.0}
        };

        Matrix3 matrix1 = new Matrix3(values1);
        Matrix3 matrix2 = new Matrix3(values2);
        Matrix3 result = matrix1.sub(matrix2);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(values1[i][j] - values2[i][j], result.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testSubWithNullThrows() {
        Matrix3 matrix1 = new Matrix3();

        assertThrows(MathException.class, () -> matrix1.sub(null));
    }

    @Test
    void testMulMatrix() {
        double[][] values1 = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };

        double[][] values2 = {
                {9.0, 8.0, 7.0},
                {6.0, 5.0, 4.0},
                {3.0, 2.0, 1.0}
        };

        double[][] expected = {
                {30.0, 24.0, 18.0},
                {84.0, 69.0, 54.0},
                {138.0, 114.0, 90.0}
        };

        Matrix3 matrix1 = new Matrix3(values1);
        Matrix3 matrix2 = new Matrix3(values2);
        Matrix3 result = matrix1.mul(matrix2);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(expected[i][j], result.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testMulMatrixWithNullThrows() {
        Matrix3 matrix1 = new Matrix3();
        Matrix3 matrix2 = null;

        assertThrows(MathException.class, () -> matrix1.mul(matrix2));
    }

    @Test
    void testMulVector() {
        double[][] matrixValues = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };

        Vector3 vector = new Vector3(2.0, 3.0, 4.0);

        Matrix3 matrix = new Matrix3(matrixValues);
        Vector3 result = matrix.mul(vector);

        assertEquals(20.0, result.getX(), EPSILON);
        assertEquals(47.0, result.getY(), EPSILON);
        assertEquals(74.0, result.getZ(), EPSILON);
    }

    @Test
    void testMulVectorWithNullThrows() {
        Matrix3 matrix = new Matrix3();
        Vector3 vector = null;

        assertThrows(MathException.class, () -> matrix.mul(vector));
    }

    @Test
    void testTranspose() {
        double[][] values = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        };

        double[][] expected = {
                {1.0, 4.0, 7.0},
                {2.0, 5.0, 8.0},
                {3.0, 6.0, 9.0}
        };

        Matrix3 matrix = new Matrix3(values);
        Matrix3 transposed = matrix.transpose();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(expected[i][j], transposed.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testTransposeIdentity() {
        Matrix3 identity = Matrix3.identity();
        Matrix3 transposed = identity.transpose();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(identity.get(i, j), transposed.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testDeterminant() {
        double[][] values = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 10.0}
        };

        Matrix3 matrix = new Matrix3(values);
        double det = matrix.determinant();

        assertEquals(-3.0, det, EPSILON);
    }

    @Test
    void testDeterminantIdentity() {
        Matrix3 identity = Matrix3.identity();
        double det = identity.determinant();

        assertEquals(1.0, det, EPSILON);
    }

    @Test
    void testDeterminantZero() {
        double[][] values = {
                {1.0, 2.0, 3.0},
                {2.0, 4.0, 6.0},
                {4.0, 5.0, 6.0}
        };

        Matrix3 matrix = new Matrix3(values);
        double det = matrix.determinant();

        assertEquals(0.0, det, EPSILON);
    }

    @Test
    void testDeterminantVerySmall() {
        double[][] values = {
                {1e-15, 2e-15, 3e-15},
                {2e-15, 4e-15, 6e-15},
                {4e-15, 5e-15, 6e-15}
        };

        Matrix3 matrix = new Matrix3(values);
        double det = matrix.determinant();

        // Определитель должен быть очень близок к 0
        assertEquals(0.0, det, EPSILON);
    }

    @Test
    void testInverse() {
        double[][] values = {
                {2, 0, 0},
                {0, 2, 0},
                {0, 0, 2}
        };

        Matrix3 matrix = new Matrix3(values);
        Matrix3 inverse = matrix.inverse();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    assertEquals(0.5, inverse.get(i, j), EPSILON);
                } else {
                    assertEquals(0.0, inverse.get(i, j), EPSILON);
                }
            }
        }

        Matrix3 identityCheck = matrix.mul(inverse);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
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
                {1.0, 2.0, 3.0},
                {2.0, 4.0, 6.0},
                {4.0, 5.0, 6.0}
        };

        Matrix3 matrix = new Matrix3(values);

        assertThrows(MathException.class, matrix::inverse);
    }

    @Test
    void testInverseVeryCloseToSingular() {
        double[][] values = {
                {1e-10, 0, 0},
                {0, 1e-10, 0},
                {0, 0, 1e-10}
        };

        Matrix3 matrix = new Matrix3(values);

        // Очень маленький определитель (1e-30) < EPSILON
        assertThrows(MathException.class, matrix::inverse);
    }

    @Test
    void testIdentityProperties() {
        Matrix3 identity = Matrix3.identity();
        Matrix3 randomMatrix = new Matrix3(new double[][]{
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}
        });

        Matrix3 result1 = identity.mul(randomMatrix);
        Matrix3 result2 = randomMatrix.mul(identity);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(randomMatrix.get(i, j), result1.get(i, j), EPSILON);
                assertEquals(randomMatrix.get(i, j), result2.get(i, j), EPSILON);
            }
        }

        assertEquals(1.0, identity.determinant(), EPSILON);

        Matrix3 transposedIdentity = identity.transpose();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(identity.get(i, j), transposedIdentity.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testMatrixMultiplicationAssociativity() {
        Matrix3 A = new Matrix3(new double[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });

        Matrix3 B = new Matrix3(new double[][]{
                {9, 8, 7},
                {6, 5, 4},
                {3, 2, 1}
        });

        Matrix3 C = new Matrix3(new double[][]{
                {0.5, 1.5, 2.5},
                {3.5, 4.5, 5.5},
                {6.5, 7.5, 8.5}
        });

        // (A × B) × C
        Matrix3 left = A.mul(B).mul(C);
        // A × (B × C)
        Matrix3 right = A.mul(B.mul(C));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(left.get(i, j), right.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testTransposeInverseProperty() {
        // Для невырожденной матрицы: (A⁻¹)ᵀ = (Aᵀ)⁻¹
        Matrix3 A = new Matrix3(new double[][]{
                {1, 2, 3},
                {0, 4, 5},
                {1, 0, 6}
        });

        Matrix3 A_inv = A.inverse();
        Matrix3 A_transpose = A.transpose();
        Matrix3 A_transpose_inv = A_transpose.inverse();
        Matrix3 A_inv_transpose = A_inv.transpose();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(A_transpose_inv.get(i, j), A_inv_transpose.get(i, j), EPSILON);
            }
        }
    }

    @Test
    void testDeterminantAfterScaling() {
        Matrix3 matrix = new Matrix3(new double[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });

        double originalDet = matrix.determinant();

        // Масштабирование строки 0 на 2
        Matrix3 scaled = new Matrix3(new double[][]{
                {2, 4, 6},  // Строка 0 × 2
                {4, 5, 6},
                {7, 8, 9}
        });

        double scaledDet = scaled.determinant();

        // Определитель должен умножиться на 2
        assertEquals(2 * originalDet, scaledDet, EPSILON);
    }

    @Test
    void testZeroMatrixProperties() {
        Matrix3 zero = new Matrix3(); // Уже нулевая матрица
        Matrix3 random = new Matrix3(new double[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        });

        // Умножение на нулевую матрицу
        Matrix3 result1 = zero.mul(random);
        Matrix3 result2 = random.mul(zero);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(0.0, result1.get(i, j), EPSILON);
                assertEquals(0.0, result2.get(i, j), EPSILON);
            }
        }

        // Сложение с нулевой матрицей
        Matrix3 result3 = random.add(zero);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(random.get(i, j), result3.get(i, j), EPSILON);
            }
        }

        // Определитель нулевой матрицы
        assertEquals(0.0, zero.determinant(), EPSILON);
    }
}