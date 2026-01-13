package ru.vsu.cs.finaltaskcg.math.affine;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.finaltaskcg.math.affine.transformation.*;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import static org.junit.jupiter.api.Assertions.*;
import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

public class AffineBuilderTest {

    @Test
    void testScaleX() {
        Point3d point = new Point3d(2, 3, 4);
        Transformation transformation = new AffineBuilder().scaleX(5).build();
        Point3d result = transformation.apply(point);
        assertEquals(10, result.x, EPSILON);
        assertEquals(3, result.y, EPSILON);
        assertEquals(4, result.z, EPSILON);
    }

    @Test
    void testScaleY() {
        Point3d point = new Point3d(2, 3, 4);
        Transformation transformation = new AffineBuilder().scaleY(5).build();
        Point3d result = transformation.apply(point);
        assertEquals(2, result.x, EPSILON);
        assertEquals(15, result.y, EPSILON);
        assertEquals(4, result.z, EPSILON);
    }

    @Test
    void testScaleZ() {
        Point3d point = new Point3d(2, 3, 4);
        Transformation transformation = new AffineBuilder().scaleZ(5).build();
        Point3d result = transformation.apply(point);
        assertEquals(2, result.x, EPSILON);
        assertEquals(3, result.y, EPSILON);
        assertEquals(20, result.z, EPSILON);
    }

    @Test
    void testScaleUniform() {
        Point3d point = new Point3d(1, 2, 3);
        Transformation transformation = new AffineBuilder().scaleUniform(5).build();
        Point3d result = transformation.apply(point);
        assertEquals(5, result.x, EPSILON);
        assertEquals(10, result.y, EPSILON);
        assertEquals(15,  result.z, EPSILON);
    }

    @Test
    void testScaleZero() {
        Point3d point = new Point3d(1, 1, 1);
        Transformation transformation = new AffineBuilder().scale(0, 0, 0).build();
        Point3d result = transformation.apply(point);
        assertEquals(0, result.x, EPSILON);
        assertEquals(0, result.y, EPSILON);
        assertEquals(0, result.z, EPSILON);
    }

    @Test
    void testRotateOnX() {
        Point3d point = new Point3d(0, 1, 0);
        Transformation transformation = new AffineBuilder().rotateX(Math.PI/2).build();
        Point3d result = transformation.apply(point);
        assertEquals(0, result.x, EPSILON);
        assertEquals(0, result.y, EPSILON);
        assertEquals(-1, result.z, EPSILON);
    }

    @Test
    void testRotateOnXQuat() {
        Point3d point = new Point3d(0, 1, 0);
        Transformation transformation = new AffineBuilder().rotateXQuat(Math.PI / 2).build();
        Point3d result = transformation.apply(point);
        assertEquals(0, result.x, EPSILON);
        assertEquals(0, result.y, EPSILON);
        assertEquals(-1, result.z, EPSILON);
    }

    @Test
    void testRotateOnY() {
        Point3d point = new Point3d(0, 0, 1);
        Transformation transformation = new AffineBuilder().rotateY(Math.PI/2).build();
        Point3d result = transformation.apply(point);
        assertEquals(1, result.x, EPSILON);
        assertEquals(0, result.y, EPSILON);
        assertEquals(0, result.z, EPSILON);
    }

    @Test
    void testRotateOnYQuat() {
        Point3d point = new Point3d(0, 0, 1);
        Transformation transformation = new AffineBuilder().rotateYQuat(Math.PI/2).build();
        Point3d result = transformation.apply(point);
        assertEquals(1, result.x, EPSILON);
        assertEquals(0, result.y, EPSILON);
        assertEquals(0, result.z, EPSILON);
    }

    @Test
    void testRotateOnZ() {
        Point3d point = new Point3d(1, 0, 0);
        Transformation transformation = new AffineBuilder().rotateZ(Math.PI/2).build();
        Point3d result = transformation.apply(point);
        assertEquals(0, result.x, EPSILON);
        assertEquals(-1, result.y, EPSILON);
        assertEquals(0, result.z, EPSILON);
    }

    @Test
    void testRotateOnZQuat() {
        Point3d point = new Point3d(1, 0, 0);
        Transformation transformation = new AffineBuilder().rotateZQuat(Math.PI/2).build();
        Point3d result = transformation.apply(point);
        assertEquals(0, result.x, EPSILON);
        assertEquals(-1, result.y, EPSILON);
        assertEquals(0, result.z, EPSILON);
    }

    @Test
    void testVerySmallAngles() {
        Point3d point = new Point3d(1, 0, 0);
        Transformation transformation = new AffineBuilder().rotateX(0.001).rotateY(0.001).rotate(Axis.Z, 0.001).build();
        Point3d result = transformation.apply(point);

        assertTrue(Math.abs(result.x - 1) < 0.01);
        assertTrue(Math.abs(result.y) < 0.01);
        assertTrue(Math.abs(result.z) < 0.01);
    }

    @Test
    void testLargeAngles() {
        Point3d point = new Point3d(1, 0, 0);
        Transformation transformation = new AffineBuilder().rotateX(Math.PI*3).rotateY(Math.PI * 2).build();
        Point3d result = transformation.apply(point);

        assertFalse(Double.isNaN(result.x));
        assertFalse(Double.isNaN(result.y));
        assertFalse(Double.isNaN(result.z));
    }

    @Test
    void testScaleThenTranslate() {
        Point3d point = new Point3d(1, 1, 1);
        Transformation transformation = new AffineBuilder().scale(2, 2, 2).translate(10, 10, 10).build();
        Point3d result = transformation.apply(point);

        assertEquals(12, result.x, EPSILON);
        assertEquals(12, result.y, EPSILON);
        assertEquals(12, result.z, EPSILON);
    }

    @Test
    void testTranslateThenScale() {
        Point3d point = new Point3d(1, 1, 1);
        Transformation transformation = new AffineBuilder().translate(10, 10, 10).scale(2, 2, 2).build();
        Point3d result = transformation.apply(point);

        assertEquals(22, result.x, EPSILON);
        assertEquals(22, result.y, EPSILON);
        assertEquals(22, result.z, EPSILON);
    }

    @Test
    void testComplexTransformation() {
        Point3d point = new Point3d(1, 2, 3);

        Transformation transformation = new AffineBuilder()
                .translate(5, 10, 15)
                .rotateX(Math.PI / 4)
                .rotateY(Math.PI / 3)
                .scale(2, 0.5, 3)
                .translate(-1, -2, -3)
                .build();

        Point3d result = transformation.apply(point);

        assertFalse(Double.isNaN(result.x));
        assertFalse(Double.isNaN(result.y));
        assertFalse(Double.isNaN(result.z));
    }

    @Test
    void testMultipleScaleOperations() {
        Point3d point = new Point3d(2, 3, 4);

        Transformation transformation = new AffineBuilder()
                .scaleX(2)
                .scaleY(3)
                .scaleZ(4)
                .scaleUniform(0.5)
                .build();
        Point3d result = transformation.apply(point);

        assertEquals(2, result.x, EPSILON);  // 2 * 2 * 0.5 = 2
        assertEquals(4.5, result.y, EPSILON); // 3 * 3 * 0.5 = 4.5
        assertEquals(8, result.z, EPSILON);   // 4 * 4 * 0.5 = 8
    }

    @Test
    void testMatrixQuaternionEquivalence() {
        Point3d point = new Point3d(2, 3, 4);

        Transformation transformation = new AffineBuilder()
                .rotateX(Math.PI / 3)
                .rotateY(Math.PI / 4)
                .rotateZ(Math.PI / 6)
                .build();

        Point3d matrixResult = transformation.apply(point);

        Transformation transformation1 = new AffineBuilder()
                .rotateXQuat(Math.PI / 3)
                .rotateYQuat(Math.PI / 4)
                .rotateZQuat(Math.PI / 6)
                .build();

        Point3d quatResult = transformation1.apply(point);

        assertEquals(matrixResult.x, quatResult.x, EPSILON);
        assertEquals(matrixResult.y, quatResult.y, EPSILON);
        assertEquals(matrixResult.z, quatResult.z, EPSILON);
    }

    @Test
    void testIdentity() {
        Point3d point = new Point3d(1, 2, 3);
        Point3d result = new AffineBuilder().build().apply(point);

        assertEquals(1, result.x, EPSILON);
        assertEquals(2, result.y, EPSILON);
        assertEquals(3, result.z, EPSILON);
    }

    @Test
    void testConsistency() {
        CompositeTransformation composite = new CompositeTransformation();
        composite.add(new TranslationTransformation(10, 0, 0));
        composite.add(new ScaleTransformation(2, 2, 2));

        Point3d point = new Point3d(1, 1, 1);

        Point3d viaApply = composite.apply(point);
        Point3d viaMatrix = new Point3d();
        composite.getMatrix().transform(point, viaMatrix);

        assertEquals(viaApply.x, viaMatrix.x, EPSILON);
        assertEquals(viaApply.y, viaMatrix.y, EPSILON);
        assertEquals(viaApply.z, viaMatrix.z, EPSILON);
    }

    @Test
    void testEmptyComposite() {
        CompositeTransformation composite = new CompositeTransformation();
        Point3d point = new Point3d(1, 2, 3);
        Point3d result = composite.apply(point);

        assertEquals(1, result.x, EPSILON);
        assertEquals(2, result.y, EPSILON);
        assertEquals(3, result.z, EPSILON);
    }

    @Test
    public void testSaveRestoreBasic() {
        AffineBuilder builder = new AffineBuilder();

        builder.translateX(10).translateY(20).scaleX(2).scaleY(3);
        Matrix4d originalMatrix = builder.build().getMatrix();

        SaveTransformation savedState = builder.saveState();

        assertMatrixEquals(originalMatrix, savedState.getMatrix());

        builder.rotateY(45).translate(Axis.X, -5);

        builder.restoreState(savedState);
        Matrix4d restoredMatrix = builder.build().getMatrix();

        assertMatrixEquals(originalMatrix, restoredMatrix);
    }

    private void assertMatrixEquals(Matrix4d a, Matrix4d b) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(a.getElement(i, j), b.getElement(i, j), EPSILON);
            }
        }
    }

    @Test
    public void testOperationsAfterRestore() {
        AffineBuilder builder = new AffineBuilder();

        builder.translateX(10).translateY(20);
        SaveTransformation saved = builder.saveState();

        builder.restoreState(saved);
        builder.scaleX(2).scaleY(2);
        Matrix4d scaledAfterRestore = builder.build().getMatrix();

        AffineBuilder reference = new AffineBuilder();
        reference.translateX(10).translateY(20).scaleX(2).scaleY(2);
        Matrix4d referenceMatrix = reference.build().getMatrix();

        assertMatrixEquals(referenceMatrix, scaledAfterRestore);
    }

    @Test
    public void testMultipleSaveRestoreCycles() {
        AffineBuilder builder = new AffineBuilder();

        builder.translateX(10);
        SaveTransformation state1 = builder.saveState();

        builder.scaleX(2);
        SaveTransformation state2 = builder.saveState();

        builder.restoreState(state1);
        assertMatrixEquals(state1.getMatrix(), builder.build().getMatrix());

        builder.rotateY(30);

        builder.restoreState(state2);
        assertMatrixEquals(state2.getMatrix(), builder.build().getMatrix());

        assertMatrixEquals(state1.getMatrix(), state1.getMatrix());
    }

}
