package ru.vsu.cs.finaltaskcg.render_engine;

public class ZBuffer {
    private double[][] buffer;
    private int width;
    private int height;

    public ZBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new double[height][width];
        clear();
    }

    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer[y][x] = 1.0; // Инициализируем дальнюю плоскость (z = 1.0)
            }
        }
    }

    public boolean testAndSet(int x, int y, double z) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        // Z-буфер: чем МЕНЬШЕ z, тем БЛИЖЕ объект
        if (z < buffer[y][x] && z >= 0) {
            buffer[y][x] = z;
            return true;
        }
        return false;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}