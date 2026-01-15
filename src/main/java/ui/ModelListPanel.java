package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class ModelListPanel extends JPanel {
    private DefaultListModel<String> modelListModel;
    private JList<String> modelList;
    private JButton addButton;
    private JButton removeButton;
    private JButton selectAllButton;
    private JButton clearSelectionButton;

    public ModelListPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Модели на сцене"));

        modelListModel = new DefaultListModel<>();
        modelList = new JList<>(modelListModel);
        modelList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(modelList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        addButton = new JButton("Добавить модель");
        removeButton = new JButton("Удалить выбранные");
        selectAllButton = new JButton("Выбрать все");
        clearSelectionButton = new JButton("Снять выделение");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(selectAllButton);
        buttonPanel.add(clearSelectionButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void addModel(String modelName) {
        modelListModel.addElement(modelName);
    }

    public void removeSelectedModels() {
        int[] selectedIndices = modelList.getSelectedIndices();
        for (int i = selectedIndices.length - 1; i >= 0; i--) {
            modelListModel.remove(selectedIndices[i]);
        }
    }

    public int[] getSelectedIndices() {
        return modelList.getSelectedIndices();
    }

    public void setSelectedIndices(int[] indices) {
        modelList.setSelectedIndices(indices);
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        modelList.addListSelectionListener(listener);
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    public JButton getSelectAllButton() {
        return selectAllButton;
    }

    public JButton getClearSelectionButton() {
        return clearSelectionButton;
    }

    public JList<String> getModelList() {
        return modelList;
    }

    public void updateModelName(int index, String newName) {
        if (index >= 0 && index < modelListModel.size()) {
            modelListModel.set(index, newName);
        }
    }
}