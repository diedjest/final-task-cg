package ru.vsu.cs.finaltaskcg.render_engine;

public class ZBuffer {
    private float[][] buffer;
    private int width;
    private int height;

    public ZBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new float[height][width];
        clear();
    }

    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer[y][x] = Float.MAX_VALUE; // Инициализируем дальнюю плоскость (z = 1.0)
            }
        }
    }

    public boolean testAndSet(int x, int y, double z) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        // Преобразуем в float для сравнения
        float zValue = (float)z;

        // В NDC: -1 (близко), 1 (далеко)
        // Сохраняем пиксели с меньшим Z (которые ближе к камере)
        if (zValue < buffer[y][x]) {
            buffer[y][x] = zValue;
            return true;
        }
        return false;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}