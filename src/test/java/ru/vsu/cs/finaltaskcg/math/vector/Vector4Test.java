package ru.vsu.cs.finaltaskcg.math.vector;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Vector4Test {

    @Test
    void testConstructorDefault() {
        Vector4 v = new Vector4();
        assertEquals(0, v.getX());
        assertEquals(0, v.getY());
        assertEquals(0, v.getZ());
        assertEquals(0, v.getW());
    }

    @Test
    void testConstructorWithValues() {
        Vector4 v = new Vector4(1.1, -2.2, 3.3, 4.4);
        assertEquals(1.1, v.getX());
        assertEquals(-2.2, v.getY());
        assertEquals(3.3, v.getZ());
        assertEquals(4.4, v.getW());
    }

    @Test
    void testGetSetX() {
        Vector4 vector = new Vector4();
        vector.setX(25.7);
        assertEquals(25.7, vector.getX(), 1e-10);
    }

    @Test
    void testGetSetY() {
        Vector4 vector = new Vector4();
        vector.setY(-12.4);
        assertEquals(-12.4, vector.getY(), 1e-10);
    }

    @Test
    void testGetSetZ() {
        Vector4 vector = new Vector4();
        vector.setZ(8.9);
        assertEquals(8.9, vector.getZ(), 1e-10);
    }

    @Test
    void testGetSetW() {
        Vector4 vector = new Vector4();
        vector.setW(-5.1);
        assertEquals(-5.1, vector.getW(), 1e-10);
    }

    @Test
    void testAdd() {
        Vector4 a = new Vector4(1, 2, 3, 4);
        Vector4 b = new Vector4(5, 6, 7, 8);
        Vector4 c = a.add(b);

        assertEquals(6, c.getX());
        assertEquals(8, c.getY());
        assertEquals(10, c.getZ());
        assertEquals(12, c.getW());
    }

    @Test
    void testSub() {
        Vector4 a = new Vector4(10, 9, 8, 7);
        Vector4 b = new Vector4(1, 2, 3, 4);
        Vector4 c = a.sub(b);

        assertEquals(9, c.getX());
        assertEquals(7, c.getY());
        assertEquals(5, c.getZ());
        assertEquals(3, c.getW());
    }

    @Test
    void testMul() {
        Vector4 v = new Vector4(2, -3, 4, -5);
        Vector4 r = v.mul(2);

        assertEquals(4, r.getX());
        assertEquals(-6, r.getY());
        assertEquals(8, r.getZ());
        assertEquals(-10, r.getW());
    }

    @Test
    void testDiv() {
        Vector4 v = new Vector4(8, -6, 4, -2);
        Vector4 r = v.div(2);

        assertEquals(4, r.getX());
        assertEquals(-3, r.getY());
        assertEquals(2, r.getZ());
        assertEquals(-1, r.getW());
    }

    @Test
    void testDivByZeroThrows() {
        Vector4 v = new Vector4(1, 2, 3, 4);
        assertThrows(MathException.class, () -> v.div(0));
    }

    @Test
    void testLength() {
        Vector4 v = new Vector4(1, 2, 3, 4);
        assertEquals(Math.sqrt(30), v.length(), 1e-9);
    }

    @Test
    void testNormalize() {
        Vector4 v = new Vector4(2, 0, 4, 0);
        Vector4 n = v.normalize();

        assertEquals(2.0 / Math.sqrt(20), n.getX(), 1e-9);
        assertEquals(0.0, n.getY(), 1e-9);
        assertEquals(4.0 / Math.sqrt(20), n.getZ(), 1e-9);
        assertEquals(0.0, n.getW(), 1e-9);
        assertEquals(1.0, n.length(), 1e-9);
    }

    @Test
    void testNormalizeZeroVectorThrows() {
        Vector4 v = new Vector4(0, 0, 0, 0);
        assertThrows(MathException.class, v::normalize);
    }

    @Test
    void testDotProduct() {
        Vector4 a = new Vector4(1, 2, 3, 4);
        Vector4 b = new Vector4(-1, 0, 1, 2);

        double result = a.dot(b);

        assertEquals(10, result);
    }
}
