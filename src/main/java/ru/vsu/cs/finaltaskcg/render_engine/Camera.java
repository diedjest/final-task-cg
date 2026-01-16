package ru.vsu.cs.finaltaskcg.render_engine;

import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;

public class Camera {

    private Vector3 position;
    private Vector3 target;
    private double fov;
    private double aspectRatio;
    private double nearPlane;
    private double farPlane;
    private Vector3 direction;
    private Vector3 upVector  = new Vector3(0, 1, 0);

    public Camera(
            final Vector3 position,
            final Vector3 target,
            final double fov,
            final double aspectRatio,
            final double nearPlane,
            final double farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;

        updateCameraVectors();
    }

    private void updateCameraVectors() {
        direction = target.sub(position).normalize();
    }

    public void rotateAroundTarget(double deltaX, double deltaY, double sensitivity) {
        Vector3 cameraToTarget = position.sub(target);

        // yaw
        double horizontalAngle = (-deltaX * sensitivity * Math.PI / 180.0);

        // pitch
        double verticalAngle = (-deltaY * sensitivity * Math.PI / 180.0);

        // расстояние от камеры до цели
        double distance = cameraToTarget.length();

        // сферические координаты
        double theta = Math.atan2(cameraToTarget.getX(), cameraToTarget.getZ());
        double phi = Math.atan2(Math.sqrt(cameraToTarget.getX() * cameraToTarget.getX() +
                        cameraToTarget.getZ() * cameraToTarget.getZ()),
                cameraToTarget.getY());

        // применяем вращение
        theta += horizontalAngle;
        phi += verticalAngle;

        // ограничиваем угол phi, чтобы камера не переворачивалась
        double epsilon = 0.01;
        phi = Math.max(epsilon, Math.min(Math.PI - epsilon, phi));

        // декартовы координаты
        double x = distance * (Math.sin(phi) * Math.sin(theta));
        double y = distance * Math.cos(phi);
        double z = distance * (Math.sin(phi) * Math.cos(theta));

        // обновляем позицию камеры
        position = new Vector3(x, y, z).add(target);

        updateCameraVectors();
    }

    public void pan(double deltaX, double deltaY, double sensitivity) {
        // правый вектор
        Vector3 right = direction.cross(upVector).normalize();

        // вычисляем истинный up вектор
        Vector3 realUp = right.cross(direction).normalize();

        // двигаем камеру и цель
        Vector3 translation = right.mul(deltaX * sensitivity)
                .add(realUp.mul(deltaY * sensitivity));

        position = position.add(translation);
        target = target.add(translation);

        updateCameraVectors();
    }

    public void zoom(double delta, double sensitivity) {
        // вектор от камеры к цели
        Vector3 toTarget = target.sub(position);
        double distance = toTarget.length();

        // изменяем расстояние
        double newDistance = Math.max(0.1, distance - delta * sensitivity);

        // новая позиция камеры
        Vector3 newPosition = target.sub(toTarget.normalize().mul(newDistance));

        position = newPosition;

        updateCameraVectors();
    }

    public void moveForwardBackward(double amount) {
        Vector3 forward = direction.normalize().mul(amount);
        position = position.add(forward);
        target = target.add(forward);
        updateCameraVectors();
    }

    public void moveRightLeft(double amount) {
        Vector3 right = direction.cross(upVector).normalize().mul(amount);
        position = position.add(right);
        target = target.add(right);
        updateCameraVectors();
    }

    public void moveUpDown(double amount) {
        Vector3 up = upVector.mul(amount);
        position = position.add(up);
        target = target.add(up);
        updateCameraVectors();
    }

    public void setPosition(final Vector3 position) {
        this.position = position;
    }

    public double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
    }

    public void setTarget(final Vector3 target) {
        this.target = target;
    }

    public void setAspectRatio(final double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getTarget() {
        return target;
    }

    public Matrix4 getViewMatrix() {
        return GraphicConveyor.lookAt(position, target);
    }

    public Matrix4 getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }
}