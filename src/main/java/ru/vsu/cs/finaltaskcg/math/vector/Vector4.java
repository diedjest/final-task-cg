package ru.vsu.cs.finaltaskcg.math.vector;

import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;

/**
 * Класс для работы с четырехмерными векторами.
 * Предоставляет основные операции над векторами в четырехмерном пространстве.
 */
public class Vector4 {
    /**
     * Координаты вектора
     */
    private double x, y, z, w;

    /**
     * Конструктор по умолчанию.
     * Создает нулевой вектор
     */
    public Vector4() { this(0, 0, 0, 0); }

    /**
     * Конструктор с заданными координатами
     * @param x координата X вектора
     * @param y координата Y вектора
     * @param z координата Z вектора
     * @param w координата W вектора
     */
    public Vector4(double x, double y, double z, double w) {
        this.x = x; this.y = y; this.z = z; this.w = w;
    }

    /**
     * Доступ к X координате вектора
     * @return значение X
     */
    public double getX() {
        return x;
    }

    /**
     * Доступ к Y координате вектора
     * @return значение Y
     */
    public double getY() {
        return y;
    }

    /**
     * Доступ к Z координате вектора
     * @return значение Z
     */
    public double getZ() {
        return z;
    }

    /**
     * Доступ к W координате вектора
     * @return значение W
     */
    public double getW() {
        return w;
    }

    /**
     * Устанавливает новое значение координаты X
     * @param x - новое значение X
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Устанавливает новое значение координаты Y
     * @param y - новое значение Y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Устанавливает новое значение координаты Z
     * @param z - новое значение Z
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Устанавливает новое значение координаты W
     * @param w - новое значение W
     */
    public void setW(double w) {
        this.w = w;
    }

    /**
     * Складывает векторы
     * @param v - вектор для сложения
     * @return результат сложения векторов
     */
    public Vector4 add(Vector4 v) {
        return new Vector4(x + v.x, y + v.y, z + v.z, w + v.w);
    }

    /**
     * Вычитает векторы
     * @param v - вектор для вычитания
     * @return результат вычитания векторов
     */
    public Vector4 sub(Vector4 v) {
        return new Vector4(x - v.x, y - v.y, z - v.z, w - v.w);
    }

    /**
     * Умножает вектор на число
     * @param s - число для умножения
     * @return вектор, умноженный на число
     */
    public Vector4 mul(double s) {
        return new Vector4(x * s, y * s, z * s, w * s);
    }

    /**
     * Делит вектор на число
     * @param s - число для деления
     * @return вектор, разделенный на число
     * @throws MathException если деление на 0
     */
    public Vector4 div(double s) {
        if (s == 0) throw new MathException("Divide by zero");
        return new Vector4(x / s, y / s, z / s, w / s);
    }

    /**
     * Находит длину вектора
     * @return длина вектора
     */
    public double length() {
        return Math.sqrt(x*x + y*y + z*z + w*w);
    }

    /**
     * Нормализует вектор
     * @return нормализованный вектор
     * @throws MathException если вектор нулевой
     */
    public Vector4 normalize() {
        double len = length();
        if (len == 0) throw new MathException("Cannot normalize zero vector");
        return div(len);
    }

    /**
     * Скалярное произведение векторов
     * @param v - вектор для умножения
     * @return число, результат скалярного произведения
     */
    public double dot(Vector4 v) {
        return x*v.x + y*v.y + z*v.z + w*v.w;
    }

    /**
     * Строковое представление вектора
     * @return строка вида Vector4(x, y, z, w)
     */
    @Override
    public String toString() {
        return "Vector4(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}