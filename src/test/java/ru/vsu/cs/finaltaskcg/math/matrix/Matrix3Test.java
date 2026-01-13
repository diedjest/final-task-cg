package ru.vsu.cs.finaltaskcg.math.matrix;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.uvarov_d_p.cg.math.exceptions.MathException;
import ru.vsu.cs.uvarov_d_p.cg.math.vector.Vector3;

import static org.junit.jupiter.api.Assertions.*;

class Matrix3Test {

    @Test
    void testDefaultConstructor() {
        Matrix3 matrix = new Matrix3();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(0.0, matrix.get(i, j), 1e-10);
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
                assertEquals(values[i][j], matrix.get(i, j), 1e-10);
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
    void testGetAndSet() {
        Matrix3 matrix = new Matrix3();

        matrix.set(0, 0, 1.5);
        matrix.set(1, 2, -2.7);
        matrix.set(2, 1, 3.14);

        assertEquals(1.5, matrix.get(0, 0), 1e-10);
        assertEquals(-2.7, matrix.get(1, 2), 1e-10);
        assertEquals(3.14, matrix.get(2, 1), 1e-10);
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
                    assertEquals(1.0, identity.get(i, j), 1e-10);
                } else {
                    assertEquals(0.0, identity.get(i, j), 1e-10);
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

        assertNotEquals(0.0, matrix.get(0, 0));

        matrix.zero();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(0.0, matrix.get(i, j), 1e-10);
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

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(values1[i][j] + values2[i][j], result.get(i, j), 1e-10);
            }
        }
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
                assertEquals(values1[i][j] - values2[i][j], result.get(i, j), 1e-10);
            }
        }
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
                assertEquals(expected[i][j], result.get(i, j), 1e-10);
            }
        }
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

        assertEquals(20.0, result.getX(), 1e-10);
        assertEquals(47.0, result.getY(), 1e-10);
        assertEquals(74.0, result.getZ(), 1e-10);
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
                assertEquals(expected[i][j], transposed.get(i, j), 1e-10);
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

        assertEquals(-3.0, det, 1e-10);
    }

    @Test
    void testDeterminantIdentity() {
        Matrix3 identity = Matrix3.identity();
        double det = identity.determinant();

        assertEquals(1.0, det, 1e-10);
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

        assertEquals(0.0, det, 1e-10);
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
                    assertEquals(0.5, inverse.get(i, j), 1e-10);
                } else {
                    assertEquals(0.0, inverse.get(i, j), 1e-10);
                }
            }
        }

        Matrix3 identityCheck = matrix.mul(inverse);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    assertEquals(1.0, identityCheck.get(i, j), 1e-10);
                } else {
                    assertEquals(0.0, identityCheck.get(i, j), 1e-10);
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
                assertEquals(randomMatrix.get(i, j), result1.get(i, j), 1e-10);
                assertEquals(randomMatrix.get(i, j), result2.get(i, j), 1e-10);
            }
        }

        assertEquals(1.0, identity.determinant(), 1e-10);

        Matrix3 transposedIdentity = identity.transpose();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(identity.get(i, j), transposedIdentity.get(i, j), 1e-10);
            }
        }
    }
}