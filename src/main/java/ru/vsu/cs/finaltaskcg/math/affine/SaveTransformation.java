package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;

public class SaveTransformation implements Transformation {
    private final Matrix4 saveCondition;

    public SaveTransformation(Matrix4 matrix) {
        this.saveCondition = new Matrix4(matrix); // Используем конструктор копирования
    }

    @Override
    public Matrix4 getMatrix() {
        // Возвращаем копию, чтобы нельзя было изменить сохраненное состояние
        return new Matrix4(saveCondition);
    }
}