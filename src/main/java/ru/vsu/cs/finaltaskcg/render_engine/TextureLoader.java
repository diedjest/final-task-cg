package ru.vsu.cs.finaltaskcg.render_engine;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
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

            if (textureImage.isError()) {
                System.err.println("Error loading texture: " + textureImage.getException());
                return false;
            }

            pixelReader = textureImage.getPixelReader();
            textureWidth = (int) textureImage.getWidth();
            textureHeight = (int) textureImage.getHeight();

            System.out.println("Texture loaded: " + filePath);
            System.out.println("Size: " + textureWidth + "x" + textureHeight);

            return true;
        } catch (Exception e) {
            System.err.println("Error loading texture: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Color getColor(float u, float v) {
        if (pixelReader == null) {
            return Color.GRAY;
        }

        u = Math.max(0, Math.min(1, u));
        v = Math.max(0, Math.min(1, v));

        int x = (int) (u * (textureWidth - 1));
        int y = (int) ((1 - v) * (textureHeight - 1));

        x = Math.max(0, Math.min(textureWidth - 1, x));
        y = Math.max(0, Math.min(textureHeight - 1, y));

        return pixelReader.getColor(x, y);
    }

    public boolean isLoaded() {
        return textureImage != null;
    }

    public Image getTextureImage() {
        return textureImage;
    }
}