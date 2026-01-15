//package ui;
//
//import com.cgvsu.model.Model;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Scene {
//    private List<Model> models = new ArrayList<>();
//    private List<Integer> selectedModelIndices = new ArrayList<>();
//
//    public void addModel(Model model) {
//        models.add(model);
//    }
//
//    public void removeModel(int index) {
//        if (index >= 0 && index < models.size()) {
//            models.remove(index);
//            selectedModelIndices.removeIf(i -> i == index);
//        }
//    }
//
//    public Model getModel(int index) {
//        if (index >= 0 && index < models.size()) {
//            return models.get(index);
//        }
//        return null;
//    }
//
//    public List<Model> getModels() {
//        return new ArrayList<>(models);
//    }
//
//    public List<Model> getSelectedModels() {
//        List<Model> selected = new ArrayList<>();
//        for (int index : selectedModelIndices) {
//            if (index < models.size()) {
//                selected.add(models.get(index));
//            }
//        }
//        return selected;
//    }
//
//    public void selectModel(int index) {
//        if (index >= 0 && index < models.size()) {
//            if (!selectedModelIndices.contains(index)) {
//                selectedModelIndices.add(index);
//            }
//        }
//    }
//
//    public void clearSelection() {
//        selectedModelIndices.clear();
//    }
//
//    public int getModelCount() {
//        return models.size();
//    }
//}