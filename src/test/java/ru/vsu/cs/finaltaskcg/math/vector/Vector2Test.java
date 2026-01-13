package ru.vsu.cs.finaltaskcg.math.vector;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;


import static org.junit.jupiter.api.Assertions.*;
import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

class Vector2Test {
    @Test
    void testConstructorDefault() {
        Vector2 v = new Vector2();
        assertEquals(0, v.getX(), EPSILON);
        assertEquals(0, v.getY(), EPSILON);
    }

    @Test
    void testConstructorWithValues() {
        Vector2 v = new Vector2(3.5, -2.1);
        assertEquals(3.5, v.getX(), EPSILON);
        assertEquals(-2.1, v.getY(), EPSILON);
    }

    @Test
    void testGetSetX() {
        Vector2 vector = new Vector2();
        vector.setX(5.5);
        assertEquals(5.5, vector.getX(), EPSILON);
        assertEquals(0, vector.getY(), EPSILON);
    }

    @Test
    void testGetSetY() {
        Vector2 vector = new Vector2();
        vector.setY(-3.2);
        assertEquals(-3.2, vector.getY(), EPSILON);
        assertEquals(0, vector.getX(), EPSILON);
    }

    @Test
    void testAdd() {
        Vector2 a = new Vector2(1, 2);
        Vector2 b = new Vector2(3, 4);
        Vector2 c = a.add(b);

        // Проверяем, что исходные векторы не изменились
        assertNotSame(a, c);
        assertNotSame(b, c);
        assertEquals(1, a.getX(), EPSILON);
        assertEquals(2, a.getY(), EPSILON);
        assertEquals(3, b.getX(), EPSILON);
        assertEquals(4, b.getY(), EPSILON);

        // Проверяем результат
        assertEquals(4, c.getX(), EPSILON);
        assertEquals(6, c.getY(), EPSILON);
    }

    @Test
    void testAddWithNullThrows() {
        Vector2 a = new Vector2(1, 2);

        assertThrows(MathException.class, () -> a.add(null));
    }

    @Test
    void testSub() {
        Vector2 a = new Vector2(5, 5);
        Vector2 b = new Vector2(2, 3);
        Vector2 c = a.sub(b);

        assertEquals(3, c.getX(), EPSILON);
        assertEquals(2, c.getY(), EPSILON);
    }

    @Test
    void testSubWithNullThrows() {
        Vector2 a = new Vector2(1, 2);

        assertThrows(MathException.class, () -> a.sub(null));
    }

    @Test
    void testMul() {
        Vector2 a = new Vector2(2, -3);
        Vector2 b = a.mul(2.5);

        assertEquals(5, b.getX(), EPSILON);
        assertEquals(-7.5, b.getY(), EPSILON);
    }

    @Test
    void testMulByZero() {
        Vector2 a = new Vector2(2, -3);
        Vector2 b = a.mul(0);

        assertEquals(0, b.getX(), EPSILON);
        assertEquals(0, b.getY(), EPSILON);
    }

    @Test
    void testDiv() {
        Vector2 a = new Vector2(6, -9);
        Vector2 b = a.div(3);

        assertEquals(2, b.getX(), EPSILON);
        assertEquals(-3, b.getY(), EPSILON);
    }

    @Test
    void testDivByZeroThrows() {
        Vector2 a = new Vector2(1, 1);

        // Теперь проверка сработает на очень маленькое число
        assertThrows(MathException.class, () -> a.div(0));
        assertThrows(MathException.class, () -> a.div(1e-20)); // Очень маленькое число
        assertThrows(MathException.class, () -> a.div(-1e-20));
    }

    @Test
    void testDivByEpsilonDoesNotThrow() {
        Vector2 a = new Vector2(1, 1);

        // Деление на число больше EPSILON должно работать
        Vector2 result = a.div(1e-5);
        assertNotNull(result);
        assertEquals(1e5, result.getX(), 1); // Допустима большая погрешность
    }

    @Test
    void testLength() {
        Vector2 a = new Vector2(3, 4);
        assertEquals(5, a.length(), EPSILON);

        Vector2 b = new Vector2(0, 0);
        assertEquals(0, b.length(), EPSILON);

        Vector2 c = new Vector2(1, 0);
        assertEquals(1, c.length(), EPSILON);

        Vector2 d = new Vector2(0, 1);
        assertEquals(1, d.length(), EPSILON);
    }

    @Test
    void testNormalize() {
        Vector2 a = new Vector2(3, 4);
        Vector2 n = a.normalize();

        assertEquals(3.0 / 5.0, n.getX(), EPSILON);
        assertEquals(4.0 / 5.0, n.getY(), EPSILON);
        assertEquals(1.0, n.length(), EPSILON);
    }

    @Test
    void testNormalizeUnitVector() {
        Vector2 a = new Vector2(1, 0);
        Vector2 n = a.normalize();

        assertEquals(1, n.getX(), EPSILON);
        assertEquals(0, n.getY(), EPSILON);
        assertEquals(1, n.length(), EPSILON);
    }

    @Test
    void testNormalizeZeroVectorThrows() {
        Vector2 zero = new Vector2(0, 0);

        // Сейчас выбросится исключение из-за EPSILON
        assertThrows(MathException.class, zero::normalize);
    }

    @Test
    void testNormalizeVerySmallVectorThrows() {
        Vector2 small = new Vector2(1e-15, 1e-15);

        // Длина ~1.414e-15 < EPSILON, должно выбросить исключение
        assertThrows(MathException.class, small::normalize);
    }

    @Test
    void testDotProduct() {
        Vector2 a = new Vector2(1, 3);
        Vector2 b = new Vector2(4, -2);

        assertEquals(-2, a.dot(b), EPSILON);

        // Проверка ортогональности
        Vector2 c = new Vector2(1, 0);
        Vector2 d = new Vector2(0, 1);
        assertEquals(0, c.dot(d), EPSILON);
    }

    @Test
    void testDotProductWithNullThrows() {
        Vector2 a = new Vector2(1, 2);

        assertThrows(MathException.class, () -> a.dot(null));
    }

    @Test
    void testDivByNegative() {
        Vector2 a = new Vector2(6, -9);
        Vector2 b = a.div(-3);

        assertEquals(-2, b.getX(), EPSILON);
        assertEquals(3, b.getY(), EPSILON);
    }

    @Test
    void testChainOperations() {
        Vector2 a = new Vector2(1, 2);
        Vector2 b = new Vector2(3, 4);
        Vector2 c = new Vector2(5, 6);

        Vector2 result = a.add(b).sub(c).mul(2).div(4);

        // ((1+3-5)*2)/4 = (-1*2)/4 = -0.5
        // ((2+4-6)*2)/4 = (0*2)/4 = 0
        assertEquals(-0.5, result.getX(), EPSILON);
        assertEquals(0, result.getY(), EPSILON);
    }
}