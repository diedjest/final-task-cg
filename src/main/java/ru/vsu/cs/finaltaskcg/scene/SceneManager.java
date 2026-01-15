//package ru.vsu.cs.finaltaskcg.scene;
//
//import model.Model;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SceneManager {
//    private List<Model> models;
//    private List<Model> selectedModels;
//    private Model activeModel;
//
//    public SceneManager() {
//        models = new ArrayList<>();
//        selectedModels = new ArrayList<>();
//    }
//
//    public void addModel(Model model) {
//        models.add(model);
//        if (activeModel == null) {
//            activeModel = model;
//        }
//    }
//
//    public void removeModel(Model model) {
//        models.remove(model);
//        selectedModels.remove(model);
//        if (activeModel == model) {
//            activeModel = models.isEmpty() ? null : models.get(0);
//        }
//    }
//
//    public void setActiveModel(Model model) {
//        activeModel = model;
//        if (!selectedModels.contains(model)) {
//            selectedModels.clear();
//            selectedModels.add(model);
//        }
//    }
//
//    public void addToSelection(Model model) {
//        if (!selectedModels.contains(model)) {
//            selectedModels.add(model);
//        }
//    }
//
//    public void clearSelection() {
//        selectedModels.clear();
//    }
//
//    public List<Model> getModels() {
//        return new ArrayList<>(models);
//    }
//
//    public List<Model> getSelectedModels() {
//        return new ArrayList<>(selectedModels);
//    }
//
//    public Model getActiveModel() {
//        return activeModel;
//    }
//}