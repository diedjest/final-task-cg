package scene;

import com.cgvsu.model.Model;
import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private List<Model> models;
    private List<Model> selectedModels;
    private Model activeModel;

    public SceneManager() {
        models = new ArrayList<>();
        selectedModels = new ArrayList<>();
        activeModel = null;
    }

    public void addModel(Model model) {
        models.add(model);
        if (activeModel == null) {
            activeModel = model;
        }
    }

    public void removeModel(int index) {
        if (index >= 0 && index < models.size()) {
            Model model = models.get(index);
            models.remove(index);
            selectedModels.remove(model);
            if (activeModel == model) {
                activeModel = models.isEmpty() ? null : models.get(0);
            }
        }
    }

    public void removeModel(Model model) {
        models.remove(model);
        selectedModels.remove(model);
        if (activeModel == model) {
            activeModel = models.isEmpty() ? null : models.get(0);
        }
    }

    public void setActiveModel(int index) {
        if (index >= 0 && index < models.size()) {
            activeModel = models.get(index);
            if (!selectedModels.contains(activeModel)) {
                selectedModels.clear();
                selectedModels.add(activeModel);
            }
        }
    }

    public void setActiveModel(Model model) {
        activeModel = model;
        if (!selectedModels.contains(model)) {
            selectedModels.clear();
            selectedModels.add(model);
        }
    }

    public void addToSelection(int index) {
        if (index >= 0 && index < models.size()) {
            Model model = models.get(index);
            if (!selectedModels.contains(model)) {
                selectedModels.add(model);
            }
        }
    }

    public void addToSelection(Model model) {
        if (!selectedModels.contains(model)) {
            selectedModels.add(model);
        }
    }

    public void clearSelection() {
        selectedModels.clear();
    }

    public List<Model> getModels() {
        return new ArrayList<>(models);
    }

    public List<Model> getSelectedModels() {
        return new ArrayList<>(selectedModels);
    }

    public Model getActiveModel() {
        return activeModel;
    }

    public Model getModel(int index) {
        if (index >= 0 && index < models.size()) {
            return models.get(index);
        }
        return null;
    }

    public int getModelCount() {
        return models.size();
    }

    public boolean isEmpty() {
        return models.isEmpty();
    }

    public int[] getSelectedIndices() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            if (selectedModels.contains(models.get(i))) {
                indices.add(i);
            }
        }
        return indices.stream().mapToInt(i -> i).toArray();
    }
}