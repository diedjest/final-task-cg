package ru.vsu.cs.finaltaskcg.math.validation;

import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;

import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

/**
 * Class for validation matrices data
 *
 * @author Dmitriy Uvarov
 */
public final class MathValidator {

    private MathValidator() {}

    /**
     * Check the validity of indexes
     */
    public static void checkMatrixIndex(int row, int col, int rows, int cols) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new MathException(
                    String.format("Matrix indices must be in range [0, %d) and [0, %d). Got [%d, %d]",
                            rows, cols, row, col)
            );
        }
    }

    public static void checkNotNull(Object obj, String name) {
        if (obj == null) {
            throw new MathException(name + " cannot be null");
        }
    }

    /**
     * Check equality to zero (epsilon = 1e-10)
     */
    public static void checkNotZero(double value, String name) {
        if (Math.abs(value) < EPSILON) {
            throw new MathException(name + " cannot be zero. Got: " + value);
        }
    }

    public static void checkArraySize(double[][] array, int expectedRows, int expectedCols) {
        checkNotNull(array, "Array");

        if (array.length != expectedRows) {
            throw new MathException(
                    String.format("Array must have %d rows. Got: %d", expectedRows, array.length)
            );
        }

        for (int i = 0; i < expectedRows; i++) {
            if (array[i] == null) {
                throw new MathException("Row " + i + " cannot be null");
            }
            if (array[i].length != expectedCols) {
                throw new MathException(
                        String.format("Row %d must have %d columns. Got: %d",
                                i, expectedCols, array[i].length)
                );
            }
        }
    }

    /**
     * Check determinant equality to zero (epsilon = 1e-10)
     */
    public static void checkDeterminant(double det) {
        if (Math.abs(det) < EPSILON) {
            throw new MathException("Matrix is singular (determinant = " + det + ")");
        }
    }
}