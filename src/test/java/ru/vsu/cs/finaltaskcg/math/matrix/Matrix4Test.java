package ru.vsu.cs.finaltaskcg.math.matrix;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.uvarov_d_p.cg.math.exceptions.MathException;
import ru.vsu.cs.uvarov_d_p.cg.math.vector.Vector4;

import static org.junit.jupiter.api.Assertions.*;

class Matrix4Test {

    @Test
    void testDefaultConstructor() {
        Matrix4 matrix = new Matrix4();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0.0, matrix.get(i, j), 1e-10);
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
                assertEquals(values[i][j], matrix.get(i, j), 1e-10);
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
    void testGetAndSet() {
        Matrix4 matrix = new Matrix4();

        matrix.set(0, 0, 1.5);
        matrix.set(1, 2, -2.7);
        matrix.set(2, 1, 3.14);
        matrix.set(3, 3, -5.2);

        assertEquals(1.5, matrix.get(0, 0), 1e-10);
        assertEquals(-2.7, matrix.get(1, 2), 1e-10);
        assertEquals(3.14, matrix.get(2, 1), 1e-10);
        assertEquals(-5.2, matrix.get(3, 3), 1e-10);
    }

    @Test
    void testGetWithInvalidIndexes() {
        Matrix4 matrix = new Matrix4();

        assertThrows(MathException.class, () -> matrix.get(-1, 0));
        assertThrows(MathException.class, () -> matrix.get(4, 0));
        assertThrows(MathException.class, () -> matrix.get(0, -1));
        assertThrows(MathException.class, () -> matrix.get(0, 4));
    }

    @Test
    void testSetWithInvalidIndexes() {
        Matrix4 matrix = new Matrix4();

        assertThrows(MathException.class, () -> matrix.set(-1, 0, 1.0));
        assertThrows(MathException.class, () -> matrix.set(4, 0, 1.0));
        assertThrows(MathException.class, () -> matrix.set(0, -1, 1.0));
        assertThrows(MathException.class, () -> matrix.set(0, 4, 1.0));
    }

    @Test
    void testIdentity() {
        Matrix4 identity = Matrix4.identity();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
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

        assertNotEquals(0.0, matrix.get(0, 0));

        matrix.zero();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0.0, matrix.get(i, j), 1e-10);
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

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(values1[i][j] + values2[i][j], result.get(i, j), 1e-10);
            }
        }
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
                assertEquals(values1[i][j] - values2[i][j], result.get(i, j), 1e-10);
            }
        }
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
                assertEquals(expected[i][j], result.get(i, j), 1e-10);
            }
        }
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

        assertEquals(40.0, result.getX(), 1e-10);
        assertEquals(96.0, result.getY(), 1e-10);
        assertEquals(152.0, result.getZ(), 1e-10);
        assertEquals(208.0, result.getW(), 1e-10);
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
                assertEquals(expected[i][j], transposed.get(i, j), 1e-10);
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

        assertEquals(30.0, det, 1e-10);
    }

    @Test
    void testDeterminantIdentity() {
        Matrix4 identity = Matrix4.identity();
        double det = identity.determinant();

        assertEquals(1.0, det, 1e-10);
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

        assertEquals(0.0, det, 1e-10);
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
                    assertEquals(0.5, inverse.get(i, j), 1e-10);
                } else {
                    assertEquals(0.0, inverse.get(i, j), 1e-10);
                }
            }
        }

        Matrix4 identityCheck = matrix.mul(inverse);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
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
                {1.0, 2.0, 3.0, 4.0},
                {2.0, 4.0, 6.0, 8.0},
                {5.0, 6.0, 7.0, 8.0},
                {9.0, 10.0, 11.0, 12.0}
        };

        Matrix4 matrix = new Matrix4(values);

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
                assertEquals(randomMatrix.get(i, j), result1.get(i, j), 1e-10);
                assertEquals(randomMatrix.get(i, j), result2.get(i, j), 1e-10);
            }
        }

        assertEquals(1.0, identity.determinant(), 1e-10);

        Matrix4 transposedIdentity = identity.transpose();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(identity.get(i, j), transposedIdentity.get(i, j), 1e-10);
            }
        }
    }
}