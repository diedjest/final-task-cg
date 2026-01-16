//package ru.vsu.cs.finaltaskcg.ui;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class DeleteDialog extends JDialog {
//    private JRadioButton vertexRadio;
//    private JRadioButton polygonRadio;
//    private JSpinner indexSpinner;
//    private boolean confirmed = false;
//
//    public DeleteDialog(JFrame parent) {
//        super(parent, "Удаление элемента", true);
//        setSize(300, 200);
//        setLocationRelativeTo(parent);
//
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(10, 10, 10, 10);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2;
//        panel.add(new JLabel("Выберите тип удаления:"), gbc);
//
//        gbc.gridy = 1;
//        ButtonGroup group = new ButtonGroup();
//        vertexRadio = new JRadioButton("Вершина", true);
//        polygonRadio = new JRadioButton("Полигон");
//        group.add(vertexRadio);
//        group.add(polygonRadio);
//
//        JPanel radioPanel = new JPanel(new FlowLayout());
//        radioPanel.add(vertexRadio);
//        radioPanel.add(polygonRadio);
//        panel.add(radioPanel, gbc);
//
//        gbc.gridy = 2;
//        panel.add(new JLabel("Индекс элемента:"), gbc);
//
//        gbc.gridy = 3;
//        indexSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
//        panel.add(indexSpinner, gbc);
//
//        gbc.gridy = 4;
//        gbc.gridwidth = 1;
//        JButton okButton = new JButton("Удалить");
//        JButton cancelButton = new JButton("Отмена");
//
//        okButton.addActionListener(e -> {
//            confirmed = true;
//            dispose();
//        });
//
//        cancelButton.addActionListener(e -> dispose());
//
//        JPanel buttonPanel = new JPanel(new FlowLayout());
//        buttonPanel.add(okButton);
//        buttonPanel.add(cancelButton);
//        panel.add(buttonPanel, gbc);
//
//        add(panel);
//    }
//
//    public boolean isConfirmed() {
//        return confirmed;
//    }
//
//    public boolean isVertexSelected() {
//        return vertexRadio.isSelected();
//    }
//
//    public int getIndex() {
//        return (int) indexSpinner.getValue();
//    }
//}