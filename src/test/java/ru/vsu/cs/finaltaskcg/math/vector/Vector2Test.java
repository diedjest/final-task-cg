package ru.vsu.cs.finaltaskcg.math.vector;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.uvarov_d_p.cg.math.exceptions.MathException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Vector2Test {

    @Test
    void testConstructorDefault() {
        Vector2 v = new Vector2();
        assertEquals(0, v.getX());
        assertEquals(0, v.getY());
    }

    @Test
    void testConstructorWithValues() {
        Vector2 v = new Vector2(3.5, -2.1);
        assertEquals(3.5, v.getX());
        assertEquals(-2.1, v.getY());
    }

    @Test
    void testGetSetX() {
        Vector2 vector = new Vector2();
        vector.setX(5.5);
        assertEquals(5.5, vector.getX(), 1e-10);
        assertEquals(0, vector.getY(), 1e-10);
    }

    @Test
    void testGetSetY() {
        Vector2 vector = new Vector2();
        vector.setY(-3.2);
        assertEquals(-3.2, vector.getY(), 1e-10);
    }

    @Test
    void testAdd() {
        Vector2 a = new Vector2(1, 2);
        Vector2 b = new Vector2(3, 4);
        Vector2 c = a.add(b);

        assert a != c;
        assert b != c;
        assert a.getX() == 1;
        assert a.getY() == 2;
        assert b.getX() == 3;
        assert b.getY() == 4;
        assertEquals(4, c.getX());
        assertEquals(6, c.getY());
    }

    @Test
    void testSub() {
        Vector2 a = new Vector2(5, 5);
        Vector2 b = new Vector2(2, 3);
        Vector2 c = a.sub(b);

        assertEquals(3, c.getX());
        assertEquals(2, c.getY());
    }

    @Test
    void testMul() {
        Vector2 a = new Vector2(2, -3);
        Vector2 b = a.mul(2.5);

        assertEquals(5, b.getX());
        assertEquals(-7.5, b.getY());
    }

    @Test
    void testDiv() {
        Vector2 a = new Vector2(6, -9);
        Vector2 b = a.div(3);

        assertEquals(2, b.getX());
        assertEquals(-3, b.getY());
    }

    @Test
    void testDivByZeroThrows() {
        Vector2 a = new Vector2(1, 1);

        assertThrows(MathException.class, () -> a.div(0));
    }

    @Test
    void testLength() {
        Vector2 a = new Vector2(3, 4);

        assertEquals(5, a.length(), 1e-10);
    }

    @Test
    void testNormalize() {
        Vector2 a = new Vector2(3, 4);
        Vector2 n = a.normalize();

        assertEquals(3.0 / 5.0, n.getX(), 1e-9);
        assertEquals(4.0 / 5.0, n.getY(), 1e-9);
        assertEquals(1.0, n.length(), 1e-9);
    }

    @Test
    void testNormalizeZeroVectorThrows() {
        Vector2 zero = new Vector2(0, 0);

        assertThrows(MathException.class, zero::normalize);
    }

    @Test
    void testDotProduct() {
        Vector2 a = new Vector2(1, 3);
        Vector2 b = new Vector2(4, -2);

        assertEquals(-2, a.dot(b));
    }
}
