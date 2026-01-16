package ru.vsu.cs.finaltaskcg.managers;

import ru.vsu.cs.finaltaskcg.math.affine.AffineBuilder;
import ru.vsu.cs.finaltaskcg.math.affine.transformation.Axis;
import ru.vsu.cs.finaltaskcg.math.affine.transformation.SaveTransformation;
import ru.vsu.cs.finaltaskcg.math.affine.transformation.Transformation;
import ru.vsu.cs.finaltaskcg.math.matrix.Matrix4;
import ru.vsu.cs.finaltaskcg.math.vector.Vector3;
import ru.vsu.cs.finaltaskcg.model.Model;
import java.util.HashMap;
import java.util.Map;

public class TransformManager {
    private Map<Model, AffineBuilder> modelBuilders = new HashMap<>();
    private Model activeModel;

    public void addModel(Model model) {
        modelBuilders.put(model, new AffineBuilder());
    }

    public void removeModel(Model model) {
        modelBuilders.remove(model);
    }

    public void setActiveModel(Model model) {
        this.activeModel = model;
    }

    public Model getActiveModel() {
        return activeModel;
    }

    public Matrix4 getTransformationMatrix(Model model) {
        AffineBuilder builder = modelBuilders.get(model);
        return builder != null ? builder.build().getMatrix() : Matrix4.identity();
    }

    public void translateModel(Model model, double x, double y, double z) {
        AffineBuilder builder = modelBuilders.get(model);
        if (builder != null) {
            builder.translate(x, y, z);
        }
    }

    public void translateActiveModel(double x, double y, double z) {
        if (activeModel != null) {
            translateModel(activeModel, x, y, z);
        }
    }

    public void rotateModel(Model model, double x, double y, double z) {
        AffineBuilder builder = modelBuilders.get(model);
        if (builder != null) {
            builder.rotateX(Math.toRadians(x))
                    .rotateY(Math.toRadians(y))
                    .rotateZ(Math.toRadians(z));
        }
    }

    public void rotateActiveModel(double x, double y, double z) {
        if (activeModel != null) {
            rotateModel(activeModel, x, y, z);
        }
    }

    public void rotateModel(Model model, Axis axis, double angleDegrees) {
        AffineBuilder builder = modelBuilders.get(model);
        if (builder != null) {
            builder.rotate(axis, Math.toRadians(angleDegrees));
        }
    }

    public void scaleModel(Model model, double x, double y, double z) {
        AffineBuilder builder = modelBuilders.get(model);
        if (builder != null) {
            builder.scale(x, y, z);
        }
    }

    public void scaleActiveModel(double x, double y, double z) {
        if (activeModel != null) {
            scaleModel(activeModel, x, y, z);
        }
    }

    public void scaleModelUniform(Model model, double factor) {
        AffineBuilder builder = modelBuilders.get(model);
        if (builder != null) {
            builder.scaleUniform(factor);
        }
    }

    public void resetModelTransform(Model model) {
        modelBuilders.put(model, new AffineBuilder());
    }

    public void resetActiveModelTransform() {
        if (activeModel != null) {
            resetModelTransform(activeModel);
        }
    }

    public SaveTransformation saveState(Model model) {
        AffineBuilder builder = modelBuilders.get(model);
        return builder != null ? builder.saveState() : null;
    }

    public void restoreState(Model model, SaveTransformation state) {
        AffineBuilder builder = modelBuilders.get(model);
        if (builder != null && state != null) {
            builder.restoreState(state);
        }
    }

    public boolean hasTransform(Model model) {
        return modelBuilders.containsKey(model);
    }
}