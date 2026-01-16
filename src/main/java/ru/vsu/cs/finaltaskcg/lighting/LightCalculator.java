package ru.vsu.cs.finaltaskcg.lighting;

import ru.vsu.cs.finaltaskcg.render_engine.Vector3;
import javafx.scene.paint.Color;

public class LightCalculator {

    public static Color calculateLight(
            Vector3 vertex,
            Vector3 normal,
            Vector3 lightPosition,
            Color baseColor) {

        try {
            Vector3 lightDir = lightPosition.sub(vertex).normalize();
            Vector3 norm = normal.normalize();

            double diff = Math.max(norm.dot(lightDir), 0.0);
            double ambient = 0.2;

            Vector3 viewDir = new Vector3(0, 0, -1).normalize();
            Vector3 reflectDir = reflect(lightDir.mul(-1), norm);

            double spec = Math.pow(Math.max(viewDir.dot(reflectDir), 0.0), 32);
            double specularStrength = 0.5;

            double intensity = ambient + (diff * 0.7) + (spec * specularStrength);
            intensity = Math.min(intensity, 1.0);

            return new Color(
                    Math.min(baseColor.getRed() * intensity, 1.0),
                    Math.min(baseColor.getGreen() * intensity, 1.0),
                    Math.min(baseColor.getBlue() * intensity, 1.0),
                    baseColor.getOpacity()
            );

        } catch (Exception e) {
            System.err.println("Error in LightCalculator: " + e.getMessage());
            return baseColor;
        }
    }

    private static Vector3 reflect(Vector3 incident, Vector3 normal) {
        double dot = incident.dot(normal);
        return incident.sub(normal.mul(2 * dot));
    }
}