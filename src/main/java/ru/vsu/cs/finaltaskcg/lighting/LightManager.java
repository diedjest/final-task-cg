package ru.vsu.cs.finaltaskcg.lighting;

import ru.vsu.cs.finaltaskcg.math.vector.Vector3;

public class LightManager {

    private Vector3 position;
    private Vector3 direction;
    private LightType type;
    private LightColor color;
    private double intensity;

    public enum LightType {
        DIRECTIONAL,
        POINT,
        SPOT
    }

    public static class LightColor {
        public double r, g, b;

        public LightColor(double r, double g, double b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public javafx.scene.paint.Color toJavaFXColor() {
            return new javafx.scene.paint.Color(r, g, b, 1.0);
        }
    }

    public LightManager() {
        this.position = new Vector3(0, 100, 100);
        this.direction = new Vector3(0, -1, -1).normalize();
        this.type = LightType.POINT;
        this.color = new LightColor(1.0, 1.0, 1.0);
        this.intensity = 1.0;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction.normalize();
    }

    public Vector3 getDirection() {
        return direction;
    }

    public void setType(LightType type) {
        this.type = type;
    }

    public LightType getType() {
        return type;
    }

    public void setColor(LightColor color) {
        this.color = color;
    }

    public LightColor getColor() {
        return color;
    }

    public void setIntensity(double intensity) {
        this.intensity = Math.max(0, Math.min(1, intensity));
    }

    public double getIntensity() {
        return intensity;
    }
}