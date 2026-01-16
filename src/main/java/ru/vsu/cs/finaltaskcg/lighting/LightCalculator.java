package ru.vsu.cs.finaltaskcg.lighting;

import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import javafx.scene.paint.Color;

public class LightCalculator {

    // Параметры освещения
    private static double ambientStrength = 0.3;  // Увеличим ambient, чтобы не было полной темноты
    private static double diffuseStrength = 0.7;
    private static double specularStrength = 0.3;  // Уменьшим specular, чтобы не было резких бликов
    private static int specularShininess = 16;     // Уменьшим shininess

    // Для отладки: включаем/выключаем разные компоненты освещения
    private static boolean useAmbient = true;
    private static boolean useDiffuse = true;
    private static boolean useSpecular = false;    // По умолчанию выключим зеркальную компоненту

    public static void setLightingParameters(double ambient, double diffuse, double specular, int shininess) {
        ambientStrength = Math.max(0, Math.min(1, ambient));
        diffuseStrength = Math.max(0, Math.min(1, diffuse));
        specularStrength = Math.max(0, Math.min(1, specular));
        specularShininess = Math.max(1, shininess);
    }

    public static void setLightingComponents(boolean ambient, boolean diffuse, boolean specular) {
        useAmbient = ambient;
        useDiffuse = diffuse;
        useSpecular = specular;
    }

    /**
     * Расчет освещения для вершины с нормалью (для плавного затенения)
     * Все координаты должны быть в мировом пространстве
     */
    public static Color calculateLight(
            Vector3 vertex,
            Vector3 normal,
            Vector3 lightPosition,
            Vector3 cameraPosition,
            Color baseColor) {

        try {
            // Нормализуем нормаль
            Vector3 norm = normal.normalize();

            // Вектор от вершины к источнику света
            Vector3 lightDir = lightPosition.sub(vertex).normalize();

            // Вектор от вершины к камере
            Vector3 viewDir = cameraPosition.sub(vertex).normalize();

            // Ambient компонента
            double ambient = useAmbient ? ambientStrength : 0.0;

            // Diffuse компонента (косинус угла между нормалью и направлением к свету)
            double diff = 0.0;
            if (useDiffuse) {
                diff = Math.max(norm.dot(lightDir), 0.0);
                // Проверяем, не смотрит ли нормаль в обратную сторону
                if (diff < 0) {
                    // Если нормаль направлена от света, переворачиваем ее
                    norm = norm.mul(-1);
                    diff = Math.max(norm.dot(lightDir), 0.0);
                }
            }

            // Specular компонента (зеркальное отражение)
            double spec = 0.0;
            if (useSpecular) {
                // Отражаем вектор света относительно нормали
                Vector3 reflectDir = reflect(lightDir.mul(-1), norm);
                spec = Math.pow(Math.max(viewDir.dot(reflectDir), 0.0), specularShininess);
            }

            // Итоговая интенсивность
            double intensity = ambient + (diff * diffuseStrength) + (spec * specularStrength);

            // Ограничиваем интенсивность
            intensity = Math.max(0.0, Math.min(1.0, intensity));

            // Если интенсивность очень низкая, добавляем немного ambient, чтобы не было полной темноты
            if (intensity < 0.1) {
                intensity = 0.1;
            }

            // Применяем освещение к цвету
            return new Color(
                    Math.min(baseColor.getRed() * intensity, 1.0),
                    Math.min(baseColor.getGreen() * intensity, 1.0),
                    Math.min(baseColor.getBlue() * intensity, 1.0),
                    baseColor.getOpacity()
            );

        } catch (Exception e) {
            System.err.println("Error in LightCalculator: " + e.getMessage());
            // В случае ошибки возвращаем цвет с минимальной освещенностью
            return new Color(
                    Math.min(baseColor.getRed() * 0.3, 1.0),
                    Math.min(baseColor.getGreen() * 0.3, 1.0),
                    Math.min(baseColor.getBlue() * 0.3, 1.0),
                    baseColor.getOpacity()
            );
        }
    }

    /**
     * Расчет освещения для треугольника (для плоского затенения)
     * Использует нормаль треугольника и его центр
     */
    public static Color calculateLightForTriangle(
            Vector3 v0, Vector3 v1, Vector3 v2,
            Vector3 lightPosition,
            Vector3 cameraPosition,
            Color baseColor) {

        try {
            // Вычисляем нормаль треугольника (плоское затенение)
            Vector3 edge1 = v1.sub(v0);
            Vector3 edge2 = v2.sub(v0);
            Vector3 normal = edge1.cross(edge2);

            // Проверяем, не нулевая ли нормаль
            double length = normal.length();
            if (length < 1e-10) {
                return baseColor; // Дегенеративный треугольник
            }

            normal = normal.normalize();

            // Проверяем направление нормали (должна быть в сторону камеры)
            Vector3 center = new Vector3(
                    (v0.getX() + v1.getX() + v2.getX()) / 3.0,
                    (v0.getY() + v1.getY() + v2.getY()) / 3.0,
                    (v0.getZ() + v1.getZ() + v2.getZ()) / 3.0
            );

            Vector3 viewDir = cameraPosition.sub(center).normalize();

            // Если нормаль направлена от камеры, переворачиваем ее
            if (normal.dot(viewDir) < 0) {
                normal = normal.mul(-1);
            }

            return calculateLight(center, normal, lightPosition, cameraPosition, baseColor);

        } catch (Exception e) {
            System.err.println("Error in LightCalculator (triangle): " + e.getMessage());
            return baseColor;
        }
    }

    /**
     * Расчет зеркальной компоненты
     */
    private static Vector3 reflect(Vector3 incident, Vector3 normal) {
        double dot = incident.dot(normal);
        return incident.sub(normal.mul(2 * dot));
    }

    /**
     * Упрощенный расчет освещения (только ambient и diffuse)
     */
    public static Color calculateSimpleLight(
            Vector3 vertex,
            Vector3 normal,
            Vector3 lightPosition,
            Vector3 cameraPosition,
            Color baseColor) {

        try {
            Vector3 norm = normal.normalize();
            Vector3 lightDir = lightPosition.sub(vertex).normalize();

            // Проверяем направление нормали
            double dot = norm.dot(lightDir);
            if (dot < 0) {
                norm = norm.mul(-1);
                dot = -dot;
            }

            double diff = Math.max(dot, 0.0);
            double ambient = ambientStrength;

            double intensity = ambient + (diff * diffuseStrength);
            intensity = Math.max(0.1, Math.min(1.0, intensity)); // Минимум 0.1, чтобы не было черного

            return new Color(
                    Math.min(baseColor.getRed() * intensity, 1.0),
                    Math.min(baseColor.getGreen() * intensity, 1.0),
                    Math.min(baseColor.getBlue() * intensity, 1.0),
                    baseColor.getOpacity()
            );

        } catch (Exception e) {
            System.err.println("Error in LightCalculator (simple): " + e.getMessage());
            // Возвращаем цвет с базовой освещенностью
            return new Color(
                    Math.min(baseColor.getRed() * 0.3, 1.0),
                    Math.min(baseColor.getGreen() * 0.3, 1.0),
                    Math.min(baseColor.getBlue() * 0.3, 1.0),
                    baseColor.getOpacity()
            );
        }
    }

    /**
     * Проверка видимости треугольника (back-face culling)
     */
    public static boolean isTriangleVisible(Vector3 v0, Vector3 v1, Vector3 v2, Vector3 cameraPosition) {
        try {
            Vector3 edge1 = v1.sub(v0);
            Vector3 edge2 = v2.sub(v0);
            Vector3 normal = edge1.cross(edge2).normalize();

            Vector3 viewDir = cameraPosition.sub(v0).normalize();

            // Если нормаль и направление взгляда смотрят в разные стороны, треугольник виден
            return normal.dot(viewDir) > 0;
        } catch (Exception e) {
            return true; // В случае ошибки считаем треугольник видимым
        }
    }
}