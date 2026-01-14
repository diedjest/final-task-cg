package ru.vsu.cs.finaltaskcg.lighting;

import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import javafx.scene.paint.Color;

public class LightCalculator {

    public static Color calculateLight(
            Vector3 vertex,
            Vector3 normal,
            Vector3 lightPosition,
            Color baseColor) {

        try {
            // Вектор от вершины к источнику света
            Vector3 lightDir = lightPosition.sub(vertex).normalize();

            // Нормализуем нормаль
            Vector3 norm = normal.normalize();

            // Диффузное освещение (косинус угла между нормалью и направлением к свету)
            double diff = Math.max(norm.dot(lightDir), 0.0);

            // Фоновое освещение
            double ambient = 0.2;

            // Зеркальное освещение (простая модель)
            Vector3 viewDir = new Vector3(0, 0, -1).normalize(); // Взгляд по умолчанию

            // Отражаем вектор (lightDir.negate() заменяем на lightDir.mul(-1))
            Vector3 reflectDir = reflect(lightDir.mul(-1), norm);

            double spec = Math.pow(Math.max(viewDir.dot(reflectDir), 0.0), 32);
            double specularStrength = 0.5;

            // Итоговая интенсивность
            double intensity = ambient + (diff * 0.7) + (spec * specularStrength);
            intensity = Math.min(intensity, 1.0);

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

    private static Vector3 reflect(Vector3 incident, Vector3 normal) {
        double dot = incident.dot(normal);
        // Используем методы из вашего Vector3: sub() и mul()
        return incident.sub(normal.mul(2 * dot));
    }
}