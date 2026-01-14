package ru.vsu.cs.finaltaskcg.render_engine;

import com.cgvsu.math.Vector3f;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rasterizer {

    public static void fillTriangle(
            GraphicsContext gc,
            ZBuffer zBuffer,
            Vector3f v1, Vector3f v2, Vector3f v3,
            Color color) {

        // Преобразуем вершины в экранные координаты
        int[] xs = {(int) v1.x, (int) v2.x, (int) v3.x};
        int[] ys = {(int) v1.y, (int) v2.y, (int) v3.y};
        float[] zs = {v1.z, v2.z, v3.z};

        // Находим границы треугольника
        int minX = Math.max(0, Math.min(xs[0], Math.min(xs[1], xs[2])));
        int maxX = Math.min(zBuffer.getWidth() - 1, Math.max(xs[0], Math.max(xs[1], xs[2])));
        int minY = Math.max(0, Math.min(ys[0], Math.min(ys[1], ys[2])));
        int maxY = Math.min(zBuffer.getHeight() - 1, Math.max(ys[0], Math.max(ys[1], ys[2])));

        // Вычисляем площадь треугольника
        float area = edgeFunction(xs[0], ys[0], xs[1], ys[1], xs[2], ys[2]);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                // Проверяем, находится ли точка внутри треугольника
                float w0 = edgeFunction(xs[1], ys[1], xs[2], ys[2], x, y);
                float w1 = edgeFunction(xs[2], ys[2], xs[0], ys[0], x, y);
                float w2 = edgeFunction(xs[0], ys[0], xs[1], ys[1], x, y);

                if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                    // Барицентрические координаты
                    w0 /= area;
                    w1 /= area;
                    w2 /= area;

                    // Интерполяция Z
                    float z = w0 * zs[0] + w1 * zs[1] + w2 * zs[2];

                    // Проверка Z-буфера
                    if (zBuffer.testAndSet(x, y, z)) {
                        gc.getPixelWriter().setColor(x, y, color);
                    }
                }
            }
        }
    }

    private static float edgeFunction(int ax, int ay, int bx, int by, int px, int py) {
        return (bx - ax) * (py - ay) - (by - ay) * (px - ax);
    }
}