package ru.vsu.cs.finaltaskcg.math.gaussian;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GaussianSolverTest {

    @Test
    void testSolve2x2() {
        double[][] A = {
                {2, 1},
                {1,-1}
        };
        double[] b = {5, 1};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(2, sol.length);
        assertEquals(2, sol[0], 1e-9);
        assertEquals(1, sol[1], 1e-9);
    }

    @Test
    void testSolve3x3() {
        double[][] A = {
                {1, 1, 1},
                {0, 2, 5},
                {2, 5,-1}
        };

        double[] b = {6, -4, 27};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(3, sol.length);
        assertEquals(5, sol[0], 1e-9);
        assertEquals(3, sol[1], 1e-9);
        assertEquals(-2, sol[2], 1e-9);
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
        assertEquals(-0.4, sol[0], 1e-9);
        assertEquals(-1.2, sol[1], 1e-9);
        assertEquals(3.4, sol[2], 1e-9);
        assertEquals(1, sol[3], 1e-9);
    }

    @Test
    void testPivotingRequired() {
        double[][] A = {
                {0, 1},
                {2, 3}
        };

        double[] b = {2, 7};

        double[] sol = GaussianSolver.solve(A, b);

        assertEquals(0.5, sol[0], 1e-9);
        assertEquals(2.0, sol[1], 1e-9);
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
    void testSingularSystem() {
        double[][] A = {
                {1, 1},
                {2, 2}
        };

        double[] b = {2, 4};

        assertThrows(MathException.class, () -> GaussianSolver.solve(A, b));
    }
}

