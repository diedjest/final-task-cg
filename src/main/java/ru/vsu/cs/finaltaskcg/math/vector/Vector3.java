package ru.vsu.cs.finaltaskcg.math.vector;

import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;

/**
 * Класс для работы с трехмерными векторами.
 * Предоставляет основные операции над векторами в трехмерном пространстве.
 */
public class Vector3 {
    /**
     * Координаты вектора
     */
    private double x, y, z;

    /**
     * Конструктор по умолчанию.
     * Создает нулевой вектор
     */
    public Vector3() { this(0, 0, 0); }

    /**
     * Конструктор с заданными координатами
     * @param x координата X вектора
     * @param y координата Y вектора
     * @param z координата Z вектора
     */
    public Vector3(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }

    /**
     * Доступ к X координате вектора
     * @return значение X
     */
    public double getX() {
        return x;
    }

    /**
     * Устанавливает новое значение координаты X
     * @param x - новое значение X
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Доступ к Y координате вектора
     * @return значение Y
     */
    public double getY() {
        return y;
    }

    /**
     * Устанавливает новое значение координаты Y
     * @param y - новое значение Y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Доступ к Z координате вектора
     * @return значение Z
     */
    public double getZ() {
        return z;
    }

    /**
     * Устанавливает новое значение координаты Z
     * @param z - новое значение Z
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Складывает векторы
     * @param v - вектор для сложения
     * @return результат сложения векторов
     */
    public Vector3 add(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Вычитает векторы
     * @param v - вектор для вычитания
     * @return результат вычитания векторов
     */
    public Vector3 sub(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Умножает вектор на число
     * @param s - число для умножения
     * @return вектор, умноженный на число
     */
    public Vector3 mul(double s) {
        return new Vector3(x * s, y * s, z * s);
    }

    /**
     * Делит вектор на число
     * @param s - число для деления
     * @return вектор, разделенный на число
     * @throws MathException если деление на 0
     */
    public Vector3 div(double s) {
        if (s == 0) throw new MathException("Divide by zero");
        return new Vector3(x / s, y / s, z / s);
    }

    /**
     * Находит длину вектора
     * @return длина вектора
     */
    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    /**
     * Нормализует вектор
     * @return нормализованный вектор
     * @throws MathException если вектор нулевой
     */
    public Vector3 normalize() {
        double len = length();
        if (len == 0) throw new MathException("Cannot normalize zero vector");
        return div(len);
    }

    /**
     * Скалярное произведение векторов
     * @param v - вектор для умножения
     * @return число, результат скалярного произведения
     */
    public double dot(Vector3 v) {
        return x*v.x + y*v.y + z*v.z;
    }

    /**
     * Векторное произведение векторов
     * @param v - вектор для умножения
     * @return вектор, результат векторного произведения
     */
    public Vector3 cross(Vector3 v) {
        return new Vector3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    /**
     * Строковое представление вектора
     * @return строка вида Vector3(x, y, z)
     */
    @Override
    public String toString() {
        return "Vector3(" + x + ", " + y + ", " + z + ")";
    }
}