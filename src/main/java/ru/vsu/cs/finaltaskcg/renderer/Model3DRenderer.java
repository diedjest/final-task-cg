package ru.vsu.cs.finaltaskcg.renderer;

import ru.vsu.cs.finaltaskcg.model.Model;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.geometry.Point3D;
import java.util.List;


public class Model3DRenderer {
    private Group modelGroup;
    private PerspectiveCamera camera;
    private Rotate rotateX;
    private Rotate rotateY;
    private Rotate rotateZ;
    private Translate translate;

    public Model3DRenderer() {
        modelGroup = new Group();

        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-1000);

        rotateX = new Rotate(0, Rotate.X_AXIS);
        rotateY = new Rotate(0, Rotate.Y_AXIS);
        rotateZ = new Rotate(0, Rotate.Z_AXIS);
        translate = new Translate(0, 0, 0);

        modelGroup.getTransforms().addAll(rotateX, rotateY, rotateZ, translate);
    }

    public Group renderModel(Model model, Color color) {
        modelGroup.getChildren().clear();

        if (model == null || model.getPolygons().isEmpty()) {
            return modelGroup;
        }

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(Color.WHITE);

        for (ru.vsu.cs.finaltaskcg.model.Polygon polygon : model.getPolygons()) {
            List<Integer> vertexIndices = polygon.getVertexIndices();

            if (vertexIndices.size() >= 3) {
                for (int i = 1; i < vertexIndices.size() - 1; i++) {
                    int idx1 = vertexIndices.get(0);
                    int idx2 = vertexIndices.get(i);
                    int idx3 = vertexIndices.get(i + 1);

                    if (idx1 < model.getVertices().size() &&
                            idx2 < model.getVertices().size() &&
                            idx3 < model.getVertices().size()) {

                        ru.vsu.cs.finaltaskcg.math.Vector3f v1 = model.getVertices().get(idx1);
                        ru.vsu.cs.finaltaskcg.math.Vector3f v2 = model.getVertices().get(idx2);
                        ru.vsu.cs.finaltaskcg.math.Vector3f v3 = model.getVertices().get(idx3);

                        TriangleMesh mesh = createTriangleMesh(
                                v1.getX(), v1.getY(), v1.getZ(),
                                v2.getX(), v2.getY(), v2.getZ(),
                                v3.getX(), v3.getY(), v3.getZ()
                        );

                        MeshView meshView = new MeshView(mesh);
                        meshView.setMaterial(material);
                        modelGroup.getChildren().add(meshView);
                    }
                }
            }
        }

        return modelGroup;
    }

    private TriangleMesh createTriangleMesh(float x1, float y1, float z1,
                                            float x2, float y2, float z2,
                                            float x3, float y3, float z3) {
        TriangleMesh mesh = new TriangleMesh();

        mesh.getPoints().addAll(
                x1, y1, z1,
                x2, y2, z2,
                x3, y3, z3
        );

        mesh.getTexCoords().addAll(0, 0, 1, 0, 0, 1);

        mesh.getFaces().addAll(0, 0, 1, 1, 2, 2);

        return mesh;
    }

    public void rotateModel(double angleX, double angleY, double angleZ) {
        rotateX.setAngle(angleX);
        rotateY.setAngle(angleY);
        rotateZ.setAngle(angleZ);
    }

    public void translateModel(double x, double y, double z) {
        translate.setX(x);
        translate.setY(y);
        translate.setZ(z);
    }

    public void scaleModel(double scale) {
        modelGroup.setScaleX(scale);
        modelGroup.setScaleY(scale);
        modelGroup.setScaleZ(scale);
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public Group getModelGroup() {
        return modelGroup;
    }
}
