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
                return false;
            }

            textureImage = new Image(new FileInputStream(file));
            pixelReader = textureImage.getPixelReader();
            textureWidth = (int) textureImage.getWidth();
            textureHeight = (int) textureImage.getHeight();
            return true;
        } catch (Exception e) {
            System.err.println("Error loading texture: " + e.getMessage());
            return false;
        }
    }

    public javafx.scene.paint.Color getColor(float u, float v) {
        if (pixelReader == null) {
            return javafx.scene.paint.Color.GRAY;
        }

        // Приводим UV координаты к диапазону [0, 1]
        u = u - (float) Math.floor(u);
        v = v - (float) Math.floor(v);

        int x = (int) (u * (textureWidth - 1));
        int y = (int) ((1 - v) * (textureHeight - 1)); // Flip V coordinate

        return pixelReader.getColor(x, y);
    }

    public boolean isLoaded() {
        return textureImage != null;
    }
}