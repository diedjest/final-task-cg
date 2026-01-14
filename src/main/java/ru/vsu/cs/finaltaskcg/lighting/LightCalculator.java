package ru.vsu.cs.finaltaskcg.lighting;

import com.cgvsu.math.Vector3f;
import javafx.scene.paint.Color;

public class LightCalculator {

    public static Color calculateLight(
            Vector3f vertex,
            Vector3f normal,
            Vector3f lightPosition,
            Color baseColor) {

        try {
            // Вектор от вершины к источнику света
            Vector3f lightDir = lightPosition.subtract(vertex).normalize();

            // Нормализуем нормаль
            Vector3f norm = normal.normalize();

            // Диффузное освещение (косинус угла между нормалью и направлением к свету)
            float diff = Math.max(norm.dot(lightDir), 0.0f);

            // Фоновое освещение
            float ambient = 0.2f;

            // Зеркальное освещение (простая модель)
            Vector3f viewDir = new Vector3f(0, 0, -1).normalize(); // Взгляд по умолчанию
            Vector3f reflectDir = reflect(lightDir.negate(), norm);
            float spec = (float) Math.pow(Math.max(viewDir.dot(reflectDir), 0.0), 32);
            float specularStrength = 0.5f;

            // Итоговая интенсивность
            float intensity = ambient + (diff * 0.7f) + (spec * specularStrength);
            intensity = Math.min(intensity, 1.0f);

            // Применяем освещение к цвету
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

    private static Vector3f reflect(Vector3f incident, Vector3f normal) {
        float dot = incident.dot(normal);
        return incident.subtract(normal.multiply(2 * dot));
    }
}