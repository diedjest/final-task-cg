package ru.vsu.cs.finaltaskcg.render_engine;

public class Camera {

    private Vector3 position;
    private Vector3 target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;

    public Camera(
            final Vector3 position,
            final Vector3 target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setPosition(final Vector3 position) {
        this.position = position;
    }

    public void setTarget(final Vector3 target) {
        this.target = target;
    }

    public void setAspectRatio(final float aspectRatio) {
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

    public void moveForwardBackward(float amount) {
        Vector3 direction = target.sub(position).normalize();
        position = position.add(direction.mul(amount));
    }

    public void moveRightLeft(float amount) {
        Vector3 direction = target.sub(position).normalize();
        Vector3 up = new Vector3(0, 1, 0);
        Vector3 right = up.cross(direction).normalize();
        position = position.add(right.mul(amount));
    }

    public void moveUpDown(float amount) {
        Vector3 up = new Vector3(0, 1, 0);
        position = position.add(up.mul(amount));
        target = target.add(up.mul(amount));
    }

    public void rotateAroundTarget(float deltaX, float deltaY, double sensitivity) {
        Vector3 direction = position.sub(target);
        double radius = direction.length();

        double theta = Math.atan2(direction.x, direction.z);
        double phi = Math.acos(direction.y / radius);

        theta += deltaX * sensitivity;
        phi += deltaY * sensitivity;

        phi = Math.max(0.1, Math.min(Math.PI - 0.1, phi));

        double newX = radius * Math.sin(phi) * Math.sin(theta);
        double newY = radius * Math.cos(phi);
        double newZ = radius * Math.sin(phi) * Math.cos(theta);

        position = new Vector3((float)newX, (float)newY, (float)newZ).add(target);
    }

    public void pan(float deltaX, float deltaY, double sensitivity) {
        Vector3 direction = target.sub(position).normalize();
        Vector3 up = new Vector3(0, 1, 0);
        Vector3 right = up.cross(direction).normalize();
        Vector3 upActual = direction.cross(right).normalize();

        Vector3 translation = right.mul(deltaX * sensitivity).add(upActual.mul(-deltaY * sensitivity));

        position = position.add(translation);
        target = target.add(translation);
    }

    public void zoom(float delta, double sensitivity) {
        Vector3 direction = target.sub(position);
        double distance = direction.length();

        if (delta > 0) {
            distance *= (1 - sensitivity);
        } else {
            distance *= (1 + sensitivity);
        }

        distance = Math.max(1.0, Math.min(10000.0, distance));

        Vector3 newPosition = target.add(direction.normalize().mul(-(float)distance));
        position = newPosition;
    }
}