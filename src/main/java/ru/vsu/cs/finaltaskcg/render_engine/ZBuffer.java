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
                buffer[y][x] = Float.POSITIVE_INFINITY;
            }
        }
    }

    public boolean testAndSet(int x, int y, double z) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        if (z < buffer[y][x]) {
            buffer[y][x] = z;
            return true;
        }
        return false;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}