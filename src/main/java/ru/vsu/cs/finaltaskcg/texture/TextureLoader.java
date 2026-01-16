package ru.vsu.cs.finaltaskcg.texture;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.io.File;
import java.io.FileInputStream;

public class TextureLoader {
    private Image textureImage;
    private PixelReader pixelReader;
    private int textureWidth;
    private int textureHeight;

    public boolean loadTexture(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("Texture file not found: " + filePath);
                return false;
            }

            System.out.println("Loading texture from: " + filePath);
            textureImage = new Image(new FileInputStream(file));
            pixelReader = textureImage.getPixelReader();
            textureWidth = (int) textureImage.getWidth();
            textureHeight = (int) textureImage.getHeight();

            System.out.println("Texture loaded: " + textureWidth + "x" + textureHeight);
            return true;
        } catch (Exception e) {
            System.err.println("Error loading texture: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public javafx.scene.paint.Color getColor(double u, double v) {
        if (pixelReader == null) {
            System.err.println("TextureLoader: No texture loaded");
            return javafx.scene.paint.Color.GRAY;
        }

        // Приводим UV координаты к диапазону [0, 1]
        u = u - Math.floor(u);
        v = v - Math.floor(v);

        // Проверяем границы
        if (u < 0) u = 0;
        if (u > 1) u = 1;
        if (v < 0) v = 0;
        if (v > 1) v = 1;

        // В OpenGL/OBJ: (0,0) - левый нижний угол, (1,1) - правый верхний
        // В JavaFX: (0,0) - левый верхний угол
        // Поэтому НЕ инвертируем V!
        int x = (int) (u * (textureWidth - 1));
        int y = (int) ((1 - v) * (textureHeight - 1)); // Обратная V координата

        // Проверяем границы массива
        if (x < 0) x = 0;
        if (x >= textureWidth) x = textureWidth - 1;
        if (y < 0) y = 0;
        if (y >= textureHeight) y = textureHeight - 1;

        return pixelReader.getColor(x, y);
    }

    public boolean isLoaded() {
        return textureImage != null;
    }

    // Добавим метод для отладки
    public void printInfo() {
        if (isLoaded()) {
            System.out.println("Texture info: " + textureWidth + "x" + textureHeight);
        } else {
            System.out.println("Texture not loaded");
        }
    }
}