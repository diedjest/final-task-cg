package ru.vsu.cs.finaltaskcg.math.affine;

import vsu.cs.transformations.Axis;
import vsu.cs.transformations.SaveTransformation;
import vsu.cs.transformations.Transformation;

public interface AffineBuilderInterface {
    Transformation build();
    SaveTransformation saveState();
    AffineBuilder restoreState(SaveTransformation transformation);

    AffineBuilder scaleX(double scaleX);
    AffineBuilder scaleY(double scaleY);
    AffineBuilder scaleZ(double scaleZ);
    AffineBuilder scaleUniform(double uniformScale);
    AffineBuilder scale(double scaleX, double scaleY, double scaleZ);
    AffineBuilder scale(Axis axis, double value);
    AffineBuilder rotateX(double rotateX);
    AffineBuilder rotateXQuat(double rotateX);
    AffineBuilder rotateY(double rotateY);
    AffineBuilder rotateYQuat(double rotateY);
    AffineBuilder rotateZ(double rotateZ);
    AffineBuilder rotateZQuat(double rotateZ);
    AffineBuilder rotate(Axis axis, double rotate);
    AffineBuilder rotateQuat(Axis axis, double rotate);
    AffineBuilder translateX(double translateX);
    AffineBuilder translateY(double translateY);
    AffineBuilder translateZ(double translateZ);
    AffineBuilder translate(Axis axis, double value);
    AffineBuilder translate(double translateX, double translateY, double translateZ);
}
