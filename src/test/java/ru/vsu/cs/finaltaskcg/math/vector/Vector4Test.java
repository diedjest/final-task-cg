package ru.vsu.cs.finaltaskcg.math.vector;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

class Vector4Test {

    @Test
    void testConstructorDefault() {
        Vector4 v = new Vector4();
        assertEquals(0, v.getX(), EPSILON);
        assertEquals(0, v.getY(), EPSILON);
        assertEquals(0, v.getZ(), EPSILON);
        assertEquals(0, v.getW(), EPSILON);
    }

    @Test
    void testConstructorWithValues() {
        Vector4 v = new Vector4(1.1, -2.2, 3.3, 4.4);
        assertEquals(1.1, v.getX(), EPSILON);
        assertEquals(-2.2, v.getY(), EPSILON);
        assertEquals(3.3, v.getZ(), EPSILON);
        assertEquals(4.4, v.getW(), EPSILON);
    }

    @Test
    void testGetSetX() {
        Vector4 vector = new Vector4();
        vector.setX(25.7);
        assertEquals(25.7, vector.getX(), EPSILON);
    }

    @Test
    void testGetSetY() {
        Vector4 vector = new Vector4();
        vector.setY(-12.4);
        assertEquals(-12.4, vector.getY(), EPSILON);
    }

    @Test
    void testGetSetZ() {
        Vector4 vector = new Vector4();
        vector.setZ(8.9);
        assertEquals(8.9, vector.getZ(), EPSILON);
    }

    @Test
    void testGetSetW() {
        Vector4 vector = new Vector4();
        vector.setW(-5.1);
        assertEquals(-5.1, vector.getW(), EPSILON);
    }

    @Test
    void testAdd() {
        Vector4 a = new Vector4(1, 2, 3, 4);
        Vector4 b = new Vector4(5, 6, 7, 8);
        Vector4 c = a.add(b);

        assertNotSame(a, c);
        assertNotSame(b, c);
        assertEquals(6, c.getX(), EPSILON);
        assertEquals(8, c.getY(), EPSILON);
        assertEquals(10, c.getZ(), EPSILON);
        assertEquals(12, c.getW(), EPSILON);
    }

    @Test
    void testAddWithNullThrows() {
        Vector4 a = new Vector4(1, 2, 3, 4);

        assertThrows(MathException.class, () -> a.add(null));
    }

    @Test
    void testSub() {
        Vector4 a = new Vector4(10, 9, 8, 7);
        Vector4 b = new Vector4(1, 2, 3, 4);
        Vector4 c = a.sub(b);

        assertEquals(9, c.getX(), EPSILON);
        assertEquals(7, c.getY(), EPSILON);
        assertEquals(5, c.getZ(), EPSILON);
        assertEquals(3, c.getW(), EPSILON);
    }

    @Test
    void testSubWithNullThrows() {
        Vector4 a = new Vector4(1, 2, 3, 4);

        assertThrows(MathException.class, () -> a.sub(null));
    }

    @Test
    void testMul() {
        Vector4 v = new Vector4(2, -3, 4, -5);
        Vector4 r = v.mul(2);

        assertEquals(4, r.getX(), EPSILON);
        assertEquals(-6, r.getY(), EPSILON);
        assertEquals(8, r.getZ(), EPSILON);
        assertEquals(-10, r.getW(), EPSILON);
    }

    @Test
    void testMulByZero() {
        Vector4 v = new Vector4(2, -3, 4, -5);
        Vector4 r = v.mul(0);

        assertEquals(0, r.getX(), EPSILON);
        assertEquals(0, r.getY(), EPSILON);
        assertEquals(0, r.getZ(), EPSILON);
        assertEquals(0, r.getW(), EPSILON);
    }

    @Test
    void testDiv() {
        Vector4 v = new Vector4(8, -6, 4, -2);
        Vector4 r = v.div(2);

        assertEquals(4, r.getX(), EPSILON);
        assertEquals(-3, r.getY(), EPSILON);
        assertEquals(2, r.getZ(), EPSILON);
        assertEquals(-1, r.getW(), EPSILON);
    }

    @Test
    void testDivByNegative() {
        Vector4 v = new Vector4(8, -6, 4, -2);
        Vector4 r = v.div(-2);

        assertEquals(-4, r.getX(), EPSILON);
        assertEquals(3, r.getY(), EPSILON);
        assertEquals(-2, r.getZ(), EPSILON);
        assertEquals(1, r.getW(), EPSILON);
    }

    @Test
    void testDivByZeroThrows() {
        Vector4 v = new Vector4(1, 2, 3, 4);

        assertThrows(MathException.class, () -> v.div(0));
    }

    @Test
    void testDivByVerySmallThrows() {
        Vector4 v = new Vector4(1, 2, 3, 4);

        assertThrows(MathException.class, () -> v.div(1e-20));
        assertThrows(MathException.class, () -> v.div(-1e-20));
    }

    @Test
    void testDivByEpsilonDoesNotThrow() {
        Vector4 v = new Vector4(1, 2, 3, 4);

        // Деление на число больше EPSILON
        Vector4 result = v.div(1e-5);
        assertNotNull(result);
        assertEquals(1e5, result.getX(), 100); // Допустима большая погрешность
    }

    @Test
    void testLength() {
        Vector4 v = new Vector4(1, 2, 3, 4);
        assertEquals(Math.sqrt(30), v.length(), EPSILON);

        // Нулевой вектор
        Vector4 zero = new Vector4(0, 0, 0, 0);
        assertEquals(0, zero.length(), EPSILON);

        // Единичные векторы
        Vector4 xUnit = new Vector4(1, 0, 0, 0);
        assertEquals(1, xUnit.length(), EPSILON);

        Vector4 yUnit = new Vector4(0, 1, 0, 0);
        assertEquals(1, yUnit.length(), EPSILON);

        Vector4 zUnit = new Vector4(0, 0, 1, 0);
        assertEquals(1, zUnit.length(), EPSILON);

        Vector4 wUnit = new Vector4(0, 0, 0, 1);
        assertEquals(1, wUnit.length(), EPSILON);
    }

    @Test
    void testNormalize() {
        Vector4 v = new Vector4(2, 0, 4, 0);
        Vector4 n = v.normalize();

        double len = Math.sqrt(20);
        assertEquals(2.0 / len, n.getX(), EPSILON);
        assertEquals(0.0, n.getY(), EPSILON);
        assertEquals(4.0 / len, n.getZ(), EPSILON);
        assertEquals(0.0, n.getW(), EPSILON);
        assertEquals(1.0, n.length(), EPSILON);
    }

    @Test
    void testNormalizeUnitVector() {
        Vector4 a = new Vector4(1, 0, 0, 0);
        Vector4 n = a.normalize();

        assertEquals(1, n.getX(), EPSILON);
        assertEquals(0, n.getY(), EPSILON);
        assertEquals(0, n.getZ(), EPSILON);
        assertEquals(0, n.getW(), EPSILON);
        assertEquals(1, n.length(), EPSILON);
    }

    @Test
    void testNormalizeZeroVectorThrows() {
        Vector4 v = new Vector4(0, 0, 0, 0);

        assertThrows(MathException.class, v::normalize);
    }

    @Test
    void testNormalizeVerySmallVectorThrows() {
        Vector4 small = new Vector4(1e-15, 1e-15, 1e-15, 1e-15);

        // Длина ~2e-15 < EPSILON, должно выбросить исключение
        assertThrows(MathException.class, small::normalize);
    }

    @Test
    void testDotProduct() {
        Vector4 a = new Vector4(1, 2, 3, 4);
        Vector4 b = new Vector4(-1, 0, 1, 2);

        double result = a.dot(b);
        assertEquals(10, result, EPSILON);
    }

    @Test
    void testDotProductWithNullThrows() {
        Vector4 a = new Vector4(1, 2, 3, 4);

        assertThrows(MathException.class, () -> a.dot(null));
    }

    @Test
    void testDotProductOrthogonal() {
        // Ортогональные векторы
        Vector4 a = new Vector4(1, 0, 0, 0);
        Vector4 b = new Vector4(0, 1, 0, 0);
        assertEquals(0, a.dot(b), EPSILON);

        Vector4 c = new Vector4(0, 0, 1, 0);
        assertEquals(0, a.dot(c), EPSILON);
        assertEquals(0, b.dot(c), EPSILON);

        Vector4 d = new Vector4(0, 0, 0, 1);
        assertEquals(0, a.dot(d), EPSILON);
        assertEquals(0, b.dot(d), EPSILON);
        assertEquals(0, c.dot(d), EPSILON);
    }

    @Test
    void testDotProductCommutative() {
        Vector4 a = new Vector4(1, 2, 3, 4);
        Vector4 b = new Vector4(5, 6, 7, 8);

        double ab = a.dot(b);
        double ba = b.dot(a);

        // Скалярное произведение коммутативно
        assertEquals(ab, ba, EPSILON);
    }

    @Test
    void testChainOperations() {
        Vector4 a = new Vector4(1, 2, 3, 4);
        Vector4 b = new Vector4(5, 6, 7, 8);
        Vector4 c = new Vector4(9, 10, 11, 12);

        Vector4 result = a.add(b).sub(c).mul(2).div(4);

        // ((1+5-9)*2)/4 = (-3*2)/4 = -1.5
        // ((2+6-10)*2)/4 = (-2*2)/4 = -1
        // ((3+7-11)*2)/4 = (-1*2)/4 = -0.5
        // ((4+8-12)*2)/4 = (0*2)/4 = 0
        assertEquals(-1.5, result.getX(), EPSILON);
        assertEquals(-1, result.getY(), EPSILON);
        assertEquals(-0.5, result.getZ(), EPSILON);
        assertEquals(0, result.getW(), EPSILON);
    }

    @Test
    void testDotProductLinearity() {
        Vector4 a = new Vector4(1, 2, 3, 4);
        Vector4 b = new Vector4(5, 6, 7, 8);
        Vector4 c = new Vector4(9, 10, 11, 12);
        double scalar = 2.5;

        // Линейность по первому аргументу
        double left1 = a.add(b).dot(c);
        double right1 = a.dot(c) + b.dot(c);
        assertEquals(left1, right1, EPSILON);

        // Линейность по второму аргументу
        double left2 = a.dot(b.add(c));
        double right2 = a.dot(b) + a.dot(c);
        assertEquals(left2, right2, EPSILON);

        // Однородность
        double left3 = a.mul(scalar).dot(b);
        double right3 = scalar * a.dot(b);
        assertEquals(left3, right3, EPSILON);
    }

    @Test
    void testHomogeneousCoordinates() {
        // Для однородных координат часто w=1
        Vector4 homogeneous = new Vector4(2, 3, 4, 1);

        // Деление на w (нормализация однородных координат)
        Vector4 normalized = homogeneous.div(homogeneous.getW());

        assertEquals(2, normalized.getX(), EPSILON);
        assertEquals(3, normalized.getY(), EPSILON);
        assertEquals(4, normalized.getZ(), EPSILON);
        assertEquals(1, normalized.getW(), EPSILON);
    }

    @Test
    void testLengthAfterScaling() {
        Vector4 v = new Vector4(1, 2, 3, 4);
        double scale = 2.5;

        Vector4 scaled = v.mul(scale);
        double expectedLength = v.length() * Math.abs(scale);

        assertEquals(expectedLength, scaled.length(), EPSILON);
    }
}