package ru.vsu.cs.finaltaskcg.math.vector;

import ru.vsu.cs.finaltaskcg.math.exceptions.MathException;

/**
 * Класс для работы с двумерными векторами.
 * Предоставляет основные операции над векторами в двумерном пространстве.
 */
public class Vector2 {
    /**
     * Координаты вектора
     */
    private double x, y;

    /**
     * Конструктор по умолчанию.
     * Создает нулевой вектор
     */
    public Vector2() { this(0, 0); }

    /**
     * Конструктор с заданными координатами
     * @param x - координата X
     * @param y - координата Y
     */
    public Vector2(double x, double y) {
        this.x = x; this.y = y;
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
     * Складывает векторы
     * @param v - вектор для сложения
     * @return результат сложения векторов
     */
    public Vector2 add(Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }

    /**
     * Вычитает векторы
     * @param v - вектор для вычитания
     * @return результат вычитания векторов
     */
    public Vector2 sub(Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }

    /**
     * Умножает вектор на число
     * @param s - число для умножения
     * @return вектор, умноженный на число
     */
    public Vector2 mul(double s) {
        return new Vector2(x * s, y * s);
    }

    /**
     * Делит вектор на число
     * @param s - число для деления
     * @return вектор, разделенный на число
     * @throws MathException если деление на 0
     */
    public Vector2 div(double s) {
        if (s == 0) throw new MathException("Divide by zero");
        return new Vector2(x / s, y / s);
    }

    /**
     * Находит длину вектора
     * @return длина вектора
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Нормализует вектор
     * @return нормализованный вектор
     * @throws MathException если вектор нулевой
     */
    public Vector2 normalize() {
        double len = length();
        if (len == 0) throw new MathException("Cannot normalize zero vector");
        return div(len);
    }

    /**
     * Скалярное произведение векторов
     * @param v - вектор для умножения
     * @return число, результат скалярного произведения
     */
    public double dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    /**
     * Строковое представление вектора
     * @return строка вида Vector2(x, y)
     */
    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
}

