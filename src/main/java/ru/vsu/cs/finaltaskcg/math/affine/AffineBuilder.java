package ru.vsu.cs.finaltaskcg.math.affine;

import ru.vsu.cs.finaltaskcg.math.affine.transformation.*;

public class AffineBuilder implements AffineBuilderInterface {
    private CompositeTransformation composite;

    public AffineBuilder() {
        this.composite = new CompositeTransformation();
    }

    public AffineBuilder scale(double sx, double sy, double sz) {
        composite.add(new ScaleTransformation(sx, sy, sz));
        return this;
    }

    @Override
    public AffineBuilder scale(Axis axis, double value) {
        switch (axis) {
            case X -> this.scaleX(value);
            case Y -> this.scaleY(value);
            case Z -> this.scaleZ(value);
            default -> throw new IllegalArgumentException("Неизвестная ось");
        }
        return this;
    }

    @Override
    public AffineBuilder scaleX(double scaleX) {
        composite.add(new ScaleTransformation(scaleX, 1, 1));
        return this;
    }

    @Override
    public AffineBuilder scaleY(double scaleY) {
        composite.add(new ScaleTransformation(1, scaleY, 1));
        return this;
    }

    @Override
    public AffineBuilder scaleZ(double scaleZ) {
        composite.add(new ScaleTransformation(1, 1, scaleZ));
        return this;
    }

    @Override
    public AffineBuilder scaleUniform(double uniformScale) {
        composite.add(new ScaleTransformation(uniformScale));
        return this;
    }

    @Override
    public AffineBuilder rotateX(double rotateX) {
        composite.add(new RotateTransformation(Axis.X, rotateX));
        return this;
    }

    @Override
    public AffineBuilder rotateXQuat(double rotateX) {
        composite.add(new RotateTransformationOnQuad(Axis.X, rotateX));
        return this;
    }

    @Override
    public AffineBuilder rotateY(double rotateY) {
        composite.add(new RotateTransformation(Axis.Y, rotateY));
        return this;
    }

    @Override
    public AffineBuilder rotateYQuat(double rotateY) {
        composite.add(new RotateTransformationOnQuad(Axis.Y, rotateY));
        return this;
    }

    @Override
    public AffineBuilder rotateZ(double rotateZ) {
        composite.add(new RotateTransformation(Axis.Z, rotateZ));
        return this;
    }

    @Override
    public AffineBuilder rotateZQuat(double rotateZ) {
        composite.add(new RotateTransformationOnQuad(Axis.Z, rotateZ));
        return this;
    }

    @Override
    public AffineBuilder rotate(Axis axis, double rotate) {
        switch (axis) {
            case X -> this.rotateX(rotate);
            case Y -> this.rotateY(rotate);
            case Z -> this.rotateZ(rotate);
            default -> throw new IllegalArgumentException("Неизвестная ось");
        }
        return this;
    }

    @Override
    public AffineBuilder rotateQuat(Axis axis, double rotate) {
            switch (axis) {
                case X -> this.rotateXQuat(rotate);
                case Y -> this.rotateYQuat(rotate);
                case Z -> this.rotateZQuat(rotate);
                default -> throw new IllegalArgumentException("Неизвестная ось");
            }
            return this;
    }

    @Override
    public AffineBuilder translateX(double translateX) {
        composite.add(new TranslationTransformation(translateX, 0, 0));
        return this;
    }

    @Override
    public AffineBuilder translateY(double translateY) {
       composite.add(new TranslationTransformation(0, translateY, 0));
       return this;
    }

    @Override
    public AffineBuilder translateZ(double translateZ) {
        composite.add(new TranslationTransformation(0, 0, translateZ));
        return this;
    }

    @Override
    public AffineBuilder translate(Axis axis, double value) {
        switch (axis) {
            case X -> this.translateX(value);
            case Y -> this.translateY(value);
            case Z -> this.translateZ(value);
            default -> throw new IllegalArgumentException("Неизвестная ось");
        }
        return this;
    }

    @Override
    public AffineBuilder translate(double tx, double ty, double tz) {
        composite.add(new TranslationTransformation(tx, ty, tz));
        return this;
    }

    @Override
    public Transformation build() {
        return composite;
    }

    @Override
    public SaveTransformation saveState() {
        return new SaveTransformation(this.composite.getMatrix());
    }

    @Override
    public AffineBuilder restoreState(SaveTransformation transformation) {
        this.composite = new CompositeTransformation();
        composite.add(transformation);
        return this;
    }
}
