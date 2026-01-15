package ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JMenuBar menuBar;
    private JPanel mainPanel;
    private ModelListPanel modelListPanel;
    private PropertiesPanel propertiesPanel;
    private RenderPanel renderPanel;
    private JPanel toolsPanel;
    private JButton moveButton;
    private JButton rotateButton;
    private JButton scaleButton;
    private JButton deleteButton;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem lightTheme;
    private JMenuItem darkTheme;
    private JMenuItem aboutItem;

    public MainWindow() {
        setTitle("3D Model Viewer Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        initMenuBar();
        initMainPanel();

        setJMenuBar(menuBar);
        add(mainPanel);

        applyTheme("light");
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        openItem = new JMenuItem("Открыть модель");
        saveItem = new JMenuItem("Сохранить модель");
        JMenuItem exitItem = new JMenuItem("Выход");

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("Вид");
        lightTheme = new JMenuItem("Светлая тема");
        darkTheme = new JMenuItem("Темная тема");

        viewMenu.add(lightTheme);
        viewMenu.add(darkTheme);

        JMenu helpMenu = new JMenu("Справка");
        aboutItem = new JMenuItem("О программе");
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        exitItem.addActionListener(e -> System.exit(0));
    }

    private void initMainPanel() {
        mainPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));

        modelListPanel = new ModelListPanel();
        propertiesPanel = new PropertiesPanel();

        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, modelListPanel, propertiesPanel);
        leftSplit.setDividerLocation(400);
        leftSplit.setResizeWeight(0.5);

        leftPanel.add(leftSplit, BorderLayout.CENTER);

        renderPanel = new RenderPanel();

        toolsPanel = new JPanel();
        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.Y_AXIS));
        toolsPanel.setPreferredSize(new Dimension(250, 0));
        toolsPanel.setBorder(BorderFactory.createTitledBorder("Инструменты трансформаций"));

        moveButton = new JButton("Перемещение");
        rotateButton = new JButton("Вращение");
        scaleButton = new JButton("Масштаб");
        deleteButton = new JButton("Удалить часть");

        JPanel transformPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        transformPanel.setBorder(BorderFactory.createTitledBorder("Параметры"));

        JLabel xLabel = new JLabel("X:");
        JTextField xField = new JTextField("0.0");
        JLabel yLabel = new JLabel("Y:");
        JTextField yField = new JTextField("0.0");
        JLabel zLabel = new JLabel("Z:");
        JTextField zField = new JTextField("0.0");
        JButton applyButton = new JButton("Применить");

        transformPanel.add(xLabel);
        transformPanel.add(xField);
        transformPanel.add(yLabel);
        transformPanel.add(yField);
        transformPanel.add(zLabel);
        transformPanel.add(zField);
        transformPanel.add(new JLabel());
        transformPanel.add(applyButton);

        toolsPanel.add(moveButton);
        toolsPanel.add(Box.createVerticalStrut(5));
        toolsPanel.add(rotateButton);
        toolsPanel.add(Box.createVerticalStrut(5));
        toolsPanel.add(scaleButton);
        toolsPanel.add(Box.createVerticalStrut(5));
        toolsPanel.add(deleteButton);
        toolsPanel.add(Box.createVerticalStrut(15));
        toolsPanel.add(transformPanel);
        toolsPanel.add(Box.createVerticalGlue());

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(renderPanel, BorderLayout.CENTER);
        mainPanel.add(toolsPanel, BorderLayout.EAST);
    }

    public void applyTheme(String themeName) {
        try {
            Color bgColor = themeName.equals("dark") ? new Color(45, 45, 48) : Color.WHITE;
            Color fgColor = themeName.equals("dark") ? Color.WHITE : Color.BLACK;
            Color panelBg = themeName.equals("dark") ? new Color(60, 63, 65) : new Color(240, 240, 240);
            Color buttonBg = themeName.equals("dark") ? new Color(75, 75, 75) : new Color(225, 225, 225);
            Color textFieldBg = themeName.equals("dark") ? new Color(43, 43, 43) : Color.WHITE;

            UIManager.put("Panel.background", panelBg);
            UIManager.put("MenuBar.background", bgColor);
            UIManager.put("Menu.background", bgColor);
            UIManager.put("MenuItem.background", bgColor);
            UIManager.put("Menu.foreground", fgColor);
            UIManager.put("MenuItem.foreground", fgColor);
            UIManager.put("List.background", textFieldBg);
            UIManager.put("List.foreground", fgColor);
            UIManager.put("TextField.background", textFieldBg);
            UIManager.put("TextField.foreground", fgColor);
            UIManager.put("Label.foreground", fgColor);
            UIManager.put("CheckBox.foreground", fgColor);
            UIManager.put("Button.background", buttonBg);
            UIManager.put("Button.foreground", fgColor);
            UIManager.put("TitledBorder.titleColor", fgColor);
            UIManager.put("SplitPane.background", panelBg);

            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            showErrorDialog("Ошибка применения темы: " + e.getMessage());
        }
    }

    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }


    public void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }

    public ModelListPanel getModelListPanel() {
        return modelListPanel;
    }

    public PropertiesPanel getPropertiesPanel() {
        return propertiesPanel;
    }

    public RenderPanel getRenderPanel() {
        return renderPanel;
    }

    public JPanel getToolsPanel() {
        return toolsPanel;
    }

    public JMenuBar getCustomMenuBar() {
        return menuBar;
    }

    public JButton getMoveButton() {
        return moveButton;
    }

    public JButton getRotateButton() {
        return rotateButton;
    }

    public JButton getScaleButton() {
        return scaleButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JMenuItem getOpenItem() {
        return openItem;
    }

    public JMenuItem getSaveItem() {
        return saveItem;
    }

    public JMenuItem getLightTheme() {
        return lightTheme;
    }

    public JMenuItem getDarkTheme() {
        return darkTheme;
    }

    public JMenuItem getAboutItem() {
        return aboutItem;
    }
}