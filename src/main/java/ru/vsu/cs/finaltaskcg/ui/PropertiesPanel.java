package ru.vsu.cs.finaltaskcg.ui;

import javax.swing.*;
import java.awt.*;

public class PropertiesPanel extends JPanel {
    private JTextField nameField;
    private JLabel vertexCountLabel;
    private JLabel polygonCountLabel;
    private JCheckBox visibleCheckBox;
    private JCheckBox wireframeCheckBox;
    private JButton colorButton;
    private Color currentColor = Color.GRAY;

    public PropertiesPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Свойства модели"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Название:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(15);
        nameField.setEditable(false);
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        vertexCountLabel = new JLabel("Вершин: 0");
        add(vertexCountLabel, gbc);

        gbc.gridx = 1;
        polygonCountLabel = new JLabel("Полигонов: 0");
        add(polygonCountLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        visibleCheckBox = new JCheckBox("Видимость", true);
        add(visibleCheckBox, gbc);

        gbc.gridy = 3;
        wireframeCheckBox = new JCheckBox("Каркасный режим");
        add(wireframeCheckBox, gbc);

        gbc.gridy = 4;
        colorButton = new JButton("Цвет модели");
        colorButton.setBackground(currentColor);
        add(colorButton, gbc);
    }

    public void updateProperties(String name, int vertices, int polygons) {
        nameField.setText(name);
        vertexCountLabel.setText("Вершин: " + vertices);
        polygonCountLabel.setText("Полигонов: " + polygons);
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JCheckBox getVisibleCheckBox() {
        return visibleCheckBox;
    }

    public JCheckBox getWireframeCheckBox() {
        return wireframeCheckBox;
    }

    public JButton getColorButton() {
        return colorButton;
    }

    public Color getCurrentColor() {
        return currentColor;
    }
}