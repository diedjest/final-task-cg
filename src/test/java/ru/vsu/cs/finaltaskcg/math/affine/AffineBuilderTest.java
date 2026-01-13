package ru.vsu.cs.finaltaskcg.math.affine;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.finaltaskcg.math.affine.transformation.*;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import ru.vsu.cs.finaltaskcg.math.vector.Vector4;

import static org.junit.jupiter.api.Assertions.*;
import static ru.vsu.cs.finaltaskcg.math.Config.EPSILON;

public class AffineBuilderTest {

    @Test
    void testScaleX() {
        Vector3 point = new Vector3(2, 3, 4);
        Transformation transformation = new AffineBuilder().scaleX(5).build();
        Vector3 result = transformation.apply(point);
        assertEquals(10, result.getX(), EPSILON);
        assertEquals(3, result.getY(), EPSILON);
        assertEquals(4, result.getZ(), EPSILON);
    }

    @Test
    void testScaleY() {
        Vector3 point = new Vector3(2, 3, 4);
        Transformation transformation = new AffineBuilder().scaleY(5).build();
        Vector3 result = transformation.apply(point);
        assertEquals(2, result.getX(), EPSILON);
        assertEquals(15, result.getY(), EPSILON);
        assertEquals(4, result.getZ(), EPSILON);
    }

    @Test
    void testScaleZ() {
        Vector3 point = new Vector3(2, 3, 4);
        Transformation transformation = new AffineBuilder().scaleZ(5).build();
        Vector3 result = transformation.apply(point);
        assertEquals(2, result.getX(), EPSILON);
        assertEquals(3, result.getY(), EPSILON);
        assertEquals(20, result.getZ(), EPSILON);
    }

    @Test
    void testScaleUniform() {
        Vector3 point = new Vector3(1, 2, 3);
        Transformation transformation = new AffineBuilder().scaleUniform(5).build();
        Vector3 result = transformation.apply(point);
        assertEquals(5, result.getX(), EPSILON);
        assertEquals(10, result.getY(), EPSILON);
        assertEquals(15, result.getZ(), EPSILON);
    }

    @Test
    void testScaleZero() {
        Vector3 point = new Vector3(1, 1, 1);
        Transformation transformation = new AffineBuilder().scale(0, 0, 0).build();
        Vector3 result = transformation.apply(point);
        assertEquals(0, result.getX(), EPSILON);
        assertEquals(0, result.getY(), EPSILON);
        assertEquals(0, result.getZ(), EPSILON);
    }

    @Test
    void testRotateOnX() {
        Vector3 point = new Vector3(0, 1, 0);
        Transformation transformation = new AffineBuilder().rotateX(Math.PI/2).build();
        Vector3 result = transformation.apply(point);
        assertEquals(0, result.getX(), EPSILON);
        assertEquals(0, result.getY(), EPSILON);
        assertEquals(1, result.getZ(), EPSILON); // Проверьте знак! В вашей системе может быть по-другому
    }

    @Test
    void testRotateOnXQuat() {
        Vector3 point = new Vector3(0, 1, 0);
        Transformation transformation = new AffineBuilder().rotateXQuat(Math.PI / 2).build();
        Vector3 result = transformation.apply(point);
        assertEquals(0, result.getX(), EPSILON);
        assertEquals(0, result.getY(), EPSILON);
        assertEquals(1, result.getZ(), EPSILON); // Проверьте знак!
    }

    @Test
    void testRotateOnY() {
        Vector3 point = new Vector3(0, 0, 1);
        Transformation transformation = new AffineBuilder().rotateY(Math.PI/2).build();
        Vector3 result = transformation.apply(point);
        assertEquals(1, result.getX(), EPSILON);
        assertEquals(0, result.getY(), EPSILON);
        assertEquals(0, result.getZ(), EPSILON); // Проверьте знак!
    }

    @Test
    void testRotateOnYQuat() {
        Vector3 point = new Vector3(0, 0, 1);
        Transformation transformation = new AffineBuilder().rotateYQuat(Math.PI/2).build();
        Vector3 result = transformation.apply(point);
        assertEquals(1, result.getX(), EPSILON);
        assertEquals(0, result.getY(), EPSILON);
        assertEquals(0, result.getZ(), EPSILON); // Проверьте знак!
    }

    @Test
    void testRotateOnZ() {
        Vector3 point = new Vector3(1, 0, 0);
        Transformation transformation = new AffineBuilder().rotateZ(Math.PI/2).build();
        Vector3 result = transformation.apply(point);
        assertEquals(0, result.getX(), EPSILON);
        assertEquals(1, result.getY(), EPSILON); // Проверьте знак!
        assertEquals(0, result.getZ(), EPSILON);
    }

    @Test
    void testRotateOnZQuat() {
        Vector3 point = new Vector3(1, 0, 0);
        Transformation transformation = new AffineBuilder().rotateZQuat(Math.PI/2).build();
        Vector3 result = transformation.apply(point);
        assertEquals(0, result.getX(), EPSILON);
        assertEquals(1, result.getY(), EPSILON); // Проверьте знак!
        assertEquals(0, result.getZ(), EPSILON);
    }

    @Test
    void testVerySmallAngles() {
        Vector3 point = new Vector3(1, 0, 0);
        Transformation transformation = new AffineBuilder().rotateX(0.001).rotateY(0.001).rotate(Axis.Z, 0.001).build();
        Vector3 result = transformation.apply(point);

        assertTrue(Math.abs(result.getX() - 1) < 0.01);
        assertTrue(Math.abs(result.getY()) < 0.01);
        assertTrue(Math.abs(result.getZ()) < 0.01);
    }

    @Test
    void testLargeAngles() {
        Vector3 point = new Vector3(1, 0, 0);
        Transformation transformation = new AffineBuilder().rotateX(Math.PI*3).rotateY(Math.PI * 2).build();
        Vector3 result = transformation.apply(point);

        assertFalse(Double.isNaN(result.getX()));
        assertFalse(Double.isNaN(result.getY()));
        assertFalse(Double.isNaN(result.getZ()));
    }

    @Test
    void testScaleThenTranslate() {
        Vector3 point = new Vector3(1, 1, 1);
        Transformation transformation = new AffineBuilder().scale(2, 2, 2).translate(10, 10, 10).build();
        Vector3 result = transformation.apply(point);

        assertEquals(12, result.getX(), EPSILON);
        assertEquals(12, result.getY(), EPSILON);
        assertEquals(12, result.getZ(), EPSILON);
    }

    @Test
    void testTranslateThenScale() {
        Vector3 point = new Vector3(1, 1, 1);
        Transformation transformation = new AffineBuilder().translate(10, 10, 10).scale(2, 2, 2).build();
        Vector3 result = transformation.apply(point);

        assertEquals(22, result.getX(), EPSILON);
        assertEquals(22, result.getY(), EPSILON);
        assertEquals(22, result.getZ(), EPSILON);
    }

    @Test
    void testComplexTransformation() {
        Vector3 point = new Vector3(1, 2, 3);

        Transformation transformation = new AffineBuilder()
                .translate(5, 10, 15)
                .rotateX(Math.PI / 4)
                .rotateY(Math.PI / 3)
                .scale(2, 0.5, 3)
                .translate(-1, -2, -3)
                .build();

        Vector3 result = transformation.apply(point);

        assertFalse(Double.isNaN(result.getX()));
        assertFalse(Double.isNaN(result.getY()));
        assertFalse(Double.isNaN(result.getZ()));
    }

    @Test
    void testMultipleScaleOperations() {
        Vector3 point = new Vector3(2, 3, 4);

        Transformation transformation = new AffineBuilder()
                .scaleX(2)
                .scaleY(3)
                .scaleZ(4)
                .scaleUniform(0.5)
                .build();
        Vector3 result = transformation.apply(point);

        assertEquals(2, result.getX(), EPSILON);  // 2 * 2 * 0.5 = 2
        assertEquals(4.5, result.getY(), EPSILON); // 3 * 3 * 0.5 = 4.5
        assertEquals(8, result.getZ(), EPSILON);   // 4 * 4 * 0.5 = 8
    }

    @Test
    void testMatrixQuaternionEquivalence() {
        Vector3 point = new Vector3(2, 3, 4);

        Transformation transformation = new AffineBuilder()
                .rotateX(Math.PI / 3)
                .rotateY(Math.PI / 4)
                .rotateZ(Math.PI / 6)
                .build();

        Vector3 matrixResult = transformation.apply(point);

        Transformation transformation1 = new AffineBuilder()
                .rotateXQuat(Math.PI / 3)
                .rotateYQuat(Math.PI / 4)
                .rotateZQuat(Math.PI / 6)
                .build();

        Vector3 quatResult = transformation1.apply(point);

        assertEquals(matrixResult.getX(), quatResult.getX(), EPSILON);
        assertEquals(matrixResult.getY(), quatResult.getY(), EPSILON);
        assertEquals(matrixResult.getZ(), quatResult.getZ(), EPSILON);
    }

    @Test
    void testIdentity() {
        Vector3 point = new Vector3(1, 2, 3);
        Vector3 result = new AffineBuilder().build().apply(point);

        assertEquals(1, result.getX(), EPSILON);
        assertEquals(2, result.getY(), EPSILON);
        assertEquals(3, result.getZ(), EPSILON);
    }

    @Test
    void testConsistency() {
        CompositeTransformation composite = new CompositeTransformation();
        composite.add(new TranslationTransformation(10, 0, 0));
        composite.add(new ScaleTransformation(2, 2, 2));

        Vector3 point = new Vector3(1, 1, 1);

        Vector3 viaApply = composite.apply(point);

        Vector4 homogeneousPoint = new Vector4(1, 1, 1, 1);
        Vector4 transformedHomogeneous = composite.getMatrix().mul(homogeneousPoint);

        double w = transformedHomogeneous.getW();
        Vector3 viaMatrix = new Vector3(
                transformedHomogeneous.getX() / w,
                transformedHomogeneous.getY() / w,
                transformedHomogeneous.getZ() / w
        );

        assertEquals(viaApply.getX(), viaMatrix.getX(), EPSILON);
        assertEquals(viaApply.getY(), viaMatrix.getY(), EPSILON);
        assertEquals(viaApply.getZ(), viaMatrix.getZ(), EPSILON);
    }

    @Test
    void testEmptyComposite() {
        CompositeTransformation composite = new CompositeTransformation();
        Vector3 point = new Vector3(1, 2, 3);
        Vector3 result = composite.apply(point);

        assertEquals(1, result.getX(), EPSILON);
        assertEquals(2, result.getY(), EPSILON);
        assertEquals(3, result.getZ(), EPSILON);
    }

    @Test
    public void testSaveRestoreBasic() {
        AffineBuilder builder = new AffineBuilder();

        builder.translateX(10).translateY(20).scaleX(2).scaleY(3);
        ru.vsu.cs.finaltaskcg.math.matrix.Matrix4 originalMatrix = builder.build().getMatrix();

        SaveTransformation savedState = builder.saveState();

        assertMatrixEquals(originalMatrix, savedState.getMatrix());

        builder.rotateY(45).translate(Axis.X, -5);

        builder.restoreState(savedState);
        ru.vsu.cs.finaltaskcg.math.matrix.Matrix4 restoredMatrix = builder.build().getMatrix();

        assertMatrixEquals(originalMatrix, restoredMatrix);
    }

    private void assertMatrixEquals(ru.vsu.cs.finaltaskcg.math.matrix.Matrix4 a, ru.vsu.cs.finaltaskcg.math.matrix.Matrix4 b) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(a.get(i, j), b.get(i, j), EPSILON);
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
        ru.vsu.cs.finaltaskcg.math.matrix.Matrix4 scaledAfterRestore = builder.build().getMatrix();

        AffineBuilder reference = new AffineBuilder();
        reference.translateX(10).translateY(20).scaleX(2).scaleY(2);
        ru.vsu.cs.finaltaskcg.math.matrix.Matrix4 referenceMatrix = reference.build().getMatrix();

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