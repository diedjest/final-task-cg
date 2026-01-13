package ru.vsu.cs.finaltaskcg.math.vector;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.uvarov_d_p.cg.math.exceptions.MathException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Vector3Test {

    @Test
    void testConstructorDefault() {
        Vector3 v = new Vector3();
        assertEquals(0, v.getX());
        assertEquals(0, v.getY());
        assertEquals(0, v.getZ());
    }

    @Test
    void testConstructorWithValues() {
        Vector3 v = new Vector3(1.5, -2.0, 3.25);
        assertEquals(1.5, v.getX());
        assertEquals(-2.0, v.getY());
        assertEquals(3.25, v.getZ());
    }

    @Test
    void testGetSetX() {
        Vector3 vector = new Vector3();
        vector.setX(10.5);
        assertEquals(10.5, vector.getX(), 1e-10);
    }

    @Test
    void testGetSetY() {
        Vector3 vector = new Vector3();
        vector.setY(-7.8);
        assertEquals(-7.8, vector.getY(), 1e-10);
    }

    @Test
    void testGetSetZ() {
        Vector3 vector = new Vector3();
        vector.setZ(15.3);
        assertEquals(15.3, vector.getZ(), 1e-10);
    }

    @Test
    void testAdd() {
        Vector3 a = new Vector3(1, 2, 3);
        Vector3 b = new Vector3(4, 5, 6);
        Vector3 c = a.add(b);

        assertEquals(5, c.getX());
        assertEquals(7, c.getY());
        assertEquals(9, c.getZ());
    }

    @Test
    void testSub() {
        Vector3 a = new Vector3(5, 5, 5);
        Vector3 b = new Vector3(2, 3, 4);
        Vector3 c = a.sub(b);

        assertEquals(3, c.getX());
        assertEquals(2, c.getY());
        assertEquals(1, c.getZ());
    }

    @Test
    void testMul() {
        Vector3 a = new Vector3(2, -3, 4);
        Vector3 b = a.mul(2.5);

        assertEquals(5, b.getX());
        assertEquals(-7.5, b.getY());
        assertEquals(10, b.getZ());
    }

    @Test
    void testDiv() {
        Vector3 a = new Vector3(6, -9, 3);
        Vector3 b = a.div(3);

        assertEquals(2, b.getX());
        assertEquals(-3, b.getY());
        assertEquals(1, b.getZ());
    }

    @Test
    void testDivByZeroThrows() {
        Vector3 a = new Vector3(1, 2, 3);

        assertThrows(MathException.class, () -> a.div(0));
    }

    @Test
    void testLength() {
        Vector3 v = new Vector3(2, 3, 6);
        assertEquals(7, v.length(), 1e-9);
    }

    @Test
    void testNormalize() {
        Vector3 v = new Vector3(3, 0, 4);
        Vector3 n = v.normalize();

        assertEquals(3.0 / 5.0, n.getX(), 1e-9);
        assertEquals(0.0, n.getY(), 1e-9);
        assertEquals(4.0 / 5.0, n.getZ(), 1e-9);
        assertEquals(1.0, n.length(), 1e-9);
    }

    @Test
    void testNormalizeZeroVectorThrows() {
        Vector3 zero = new Vector3(0, 0, 0);

        assertThrows(MathException.class, zero::normalize);
    }

    @Test
    void testDotProduct() {
        Vector3 a = new Vector3(1, 3, -5);
        Vector3 b = new Vector3(4, -2, -1);

        assertEquals(3, a.dot(b));
    }

    @Test
    void testCrossProduct() {
        Vector3 a = new Vector3(1, 2, 3);
        Vector3 b = new Vector3(4, 5, 6);
        Vector3 c = a.cross(b);

        assertEquals(-3, c.getX());
        assertEquals(6, c.getY());
        assertEquals(-3, c.getZ());
    }

    @Test
    void testCrossPerpendicularity() {
        Vector3 a = new Vector3(1, 0, 0);
        Vector3 b = new Vector3(0, 1, 0);

        Vector3 c = a.cross(b);

        assertEquals(0, c.getX());
        assertEquals(0, c.getY());
        assertEquals(1, c.getZ());

        assertEquals(0, c.dot(a));
        assertEquals(0, c.dot(b));
    }
}
