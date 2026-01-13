package ru.vsu.cs.finaltaskcg.math.vector;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

class Vector3Test {

    @Test
    void testConstructorDefault() {
        Vector3 v = new Vector3();
        assertEquals(0, v.getX(), EPSILON);
        assertEquals(0, v.getY(), EPSILON);
        assertEquals(0, v.getZ(), EPSILON);
    }

    @Test
    void testConstructorWithValues() {
        Vector3 v = new Vector3(1.5, -2.0, 3.25);
        assertEquals(1.5, v.getX(), EPSILON);
        assertEquals(-2.0, v.getY(), EPSILON);
        assertEquals(3.25, v.getZ(), EPSILON);
    }

    @Test
    void testGetSetX() {
        Vector3 vector = new Vector3();
        vector.setX(10.5);
        assertEquals(10.5, vector.getX(), EPSILON);
    }

    @Test
    void testGetSetY() {
        Vector3 vector = new Vector3();
        vector.setY(-7.8);
        assertEquals(-7.8, vector.getY(), EPSILON);
    }

    @Test
    void testGetSetZ() {
        Vector3 vector = new Vector3();
        vector.setZ(15.3);
        assertEquals(15.3, vector.getZ(), EPSILON);
    }

    @Test
    void testAdd() {
        Vector3 a = new Vector3(1, 2, 3);
        Vector3 b = new Vector3(4, 5, 6);
        Vector3 c = a.add(b);

        assertNotSame(a, c);
        assertNotSame(b, c);
        assertEquals(5, c.getX(), EPSILON);
        assertEquals(7, c.getY(), EPSILON);
        assertEquals(9, c.getZ(), EPSILON);
    }

    @Test
    void testAddWithNullThrows() {
        Vector3 a = new Vector3(1, 2, 3);

        assertThrows(MathException.class, () -> a.add(null));
    }

    @Test
    void testSub() {
        Vector3 a = new Vector3(5, 5, 5);
        Vector3 b = new Vector3(2, 3, 4);
        Vector3 c = a.sub(b);

        assertEquals(3, c.getX(), EPSILON);
        assertEquals(2, c.getY(), EPSILON);
        assertEquals(1, c.getZ(), EPSILON);
    }

    @Test
    void testSubWithNullThrows() {
        Vector3 a = new Vector3(1, 2, 3);

        assertThrows(MathException.class, () -> a.sub(null));
    }

    @Test
    void testMul() {
        Vector3 a = new Vector3(2, -3, 4);
        Vector3 b = a.mul(2.5);

        assertEquals(5, b.getX(), EPSILON);
        assertEquals(-7.5, b.getY(), EPSILON);
        assertEquals(10, b.getZ(), EPSILON);
    }

    @Test
    void testMulByZero() {
        Vector3 a = new Vector3(2, -3, 4);
        Vector3 b = a.mul(0);

        assertEquals(0, b.getX(), EPSILON);
        assertEquals(0, b.getY(), EPSILON);
        assertEquals(0, b.getZ(), EPSILON);
    }

    @Test
    void testDiv() {
        Vector3 a = new Vector3(6, -9, 3);
        Vector3 b = a.div(3);

        assertEquals(2, b.getX(), EPSILON);
        assertEquals(-3, b.getY(), EPSILON);
        assertEquals(1, b.getZ(), EPSILON);
    }

    @Test
    void testDivByNegative() {
        Vector3 a = new Vector3(6, -9, 3);
        Vector3 b = a.div(-3);

        assertEquals(-2, b.getX(), EPSILON);
        assertEquals(3, b.getY(), EPSILON);
        assertEquals(-1, b.getZ(), EPSILON);
    }

    @Test
    void testDivByZeroThrows() {
        Vector3 a = new Vector3(1, 2, 3);

        assertThrows(MathException.class, () -> a.div(0));
    }

    @Test
    void testDivByVerySmallThrows() {
        Vector3 a = new Vector3(1, 2, 3);

        assertThrows(MathException.class, () -> a.div(1e-20));
        assertThrows(MathException.class, () -> a.div(-1e-20));
    }

    @Test
    void testDivByEpsilonDoesNotThrow() {
        Vector3 a = new Vector3(1, 2, 3);

        // Деление на число больше EPSILON
        Vector3 result = a.div(1e-5);
        assertNotNull(result);
        assertEquals(1e5, result.getX(), 100); // Допустима большая погрешность
    }

    @Test
    void testLength() {
        Vector3 v = new Vector3(2, 3, 6);
        assertEquals(7, v.length(), EPSILON);

        // Нулевой вектор
        Vector3 zero = new Vector3(0, 0, 0);
        assertEquals(0, zero.length(), EPSILON);

        // Единичные векторы
        Vector3 xUnit = new Vector3(1, 0, 0);
        assertEquals(1, xUnit.length(), EPSILON);

        Vector3 yUnit = new Vector3(0, 1, 0);
        assertEquals(1, yUnit.length(), EPSILON);

        Vector3 zUnit = new Vector3(0, 0, 1);
        assertEquals(1, zUnit.length(), EPSILON);
    }

    @Test
    void testNormalize() {
        Vector3 v = new Vector3(3, 0, 4);
        Vector3 n = v.normalize();

        assertEquals(3.0 / 5.0, n.getX(), EPSILON);
        assertEquals(0.0, n.getY(), EPSILON);
        assertEquals(4.0 / 5.0, n.getZ(), EPSILON);
        assertEquals(1.0, n.length(), EPSILON);
    }

    @Test
    void testNormalizeUnitVector() {
        Vector3 a = new Vector3(1, 0, 0);
        Vector3 n = a.normalize();

        assertEquals(1, n.getX(), EPSILON);
        assertEquals(0, n.getY(), EPSILON);
        assertEquals(0, n.getZ(), EPSILON);
        assertEquals(1, n.length(), EPSILON);
    }

    @Test
    void testNormalizeZeroVectorThrows() {
        Vector3 zero = new Vector3(0, 0, 0);

        assertThrows(MathException.class, zero::normalize);
    }

    @Test
    void testNormalizeVerySmallVectorThrows() {
        Vector3 small = new Vector3(1e-15, 1e-15, 1e-15);

        // Длина ~1.732e-15 < EPSILON, должно выбросить исключение
        assertThrows(MathException.class, small::normalize);
    }

    @Test
    void testDotProduct() {
        Vector3 a = new Vector3(1, 3, -5);
        Vector3 b = new Vector3(4, -2, -1);

        assertEquals(3, a.dot(b), EPSILON);
    }

    @Test
    void testDotProductWithNullThrows() {
        Vector3 a = new Vector3(1, 2, 3);

        assertThrows(MathException.class, () -> a.dot(null));
    }

    @Test
    void testDotProductOrthogonal() {
        // Ортогональные векторы
        Vector3 a = new Vector3(1, 0, 0);
        Vector3 b = new Vector3(0, 1, 0);
        assertEquals(0, a.dot(b), EPSILON);

        Vector3 c = new Vector3(0, 0, 1);
        assertEquals(0, a.dot(c), EPSILON);
        assertEquals(0, b.dot(c), EPSILON);
    }

    @Test
    void testCrossProduct() {
        Vector3 a = new Vector3(1, 2, 3);
        Vector3 b = new Vector3(4, 5, 6);
        Vector3 c = a.cross(b);

        assertEquals(-3, c.getX(), EPSILON);
        assertEquals(6, c.getY(), EPSILON);
        assertEquals(-3, c.getZ(), EPSILON);
    }

    @Test
    void testCrossProductWithNullThrows() {
        Vector3 a = new Vector3(1, 2, 3);

        assertThrows(MathException.class, () -> a.cross(null));
    }

    @Test
    void testCrossPerpendicularity() {
        Vector3 a = new Vector3(1, 0, 0);
        Vector3 b = new Vector3(0, 1, 0);

        Vector3 c = a.cross(b);

        assertEquals(0, c.getX(), EPSILON);
        assertEquals(0, c.getY(), EPSILON);
        assertEquals(1, c.getZ(), EPSILON);

        // Проверка ортогональности
        assertEquals(0, c.dot(a), EPSILON);
        assertEquals(0, c.dot(b), EPSILON);
    }

    @Test
    void testCrossProductAntiCommutative() {
        Vector3 a = new Vector3(1, 2, 3);
        Vector3 b = new Vector3(4, 5, 6);

        Vector3 crossAB = a.cross(b);
        Vector3 crossBA = b.cross(a);

        // a * b = -(b * a)
        assertEquals(-crossAB.getX(), crossBA.getX(), EPSILON);
        assertEquals(-crossAB.getY(), crossBA.getY(), EPSILON);
        assertEquals(-crossAB.getZ(), crossBA.getZ(), EPSILON);
    }

    @Test
    void testCrossProductWithParallelVectors() {
        Vector3 a = new Vector3(1, 2, 3);
        Vector3 b = a.mul(2.5); // Коллинеарный вектор

        Vector3 cross = a.cross(b);

        // Векторное произведение коллинеарных векторов = 0
        assertEquals(0, cross.getX(), EPSILON);
        assertEquals(0, cross.getY(), EPSILON);
        assertEquals(0, cross.getZ(), EPSILON);
    }

    @Test
    void testChainOperations() {
        Vector3 a = new Vector3(1, 2, 3);
        Vector3 b = new Vector3(4, 5, 6);
        Vector3 c = new Vector3(7, 8, 9);

        Vector3 result = a.add(b).sub(c).mul(2).div(4);

        // ((1+4-7)*2)/4 = (-2*2)/4 = -1
        // ((2+5-8)*2)/4 = (-1*2)/4 = -0.5
        // ((3+6-9)*2)/4 = (0*2)/4 = 0
        assertEquals(-1, result.getX(), EPSILON);
        assertEquals(-0.5, result.getY(), EPSILON);
        assertEquals(0, result.getZ(), EPSILON);
    }

    @Test
    void testCrossDotIdentity() {
        // Тождество Лагранжа: (a * b) . c = a . (b * c)
        Vector3 a = new Vector3(1, 2, 3);
        Vector3 b = new Vector3(4, 5, 6);
        Vector3 c = new Vector3(7, 8, 9);

        double left = a.cross(b).dot(c);
        double right = a.dot(b.cross(c));

        assertEquals(left, right, EPSILON);
    }
}