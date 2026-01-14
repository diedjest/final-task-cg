package ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JMenuBar menuBar;
    private JPanel mainPanel;
    private ModelListPanel modelListPanel;
    private PropertiesPanel propertiesPanel;

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
        JMenuItem openItem = new JMenuItem("Открыть модель");
        JMenuItem saveItem = new JMenuItem("Сохранить модель");
        JMenuItem exitItem = new JMenuItem("Выход");

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("Вид");
        JMenuItem lightTheme = new JMenuItem("Светлая тема");
        JMenuItem darkTheme = new JMenuItem("Темная тема");

        viewMenu.add(lightTheme);
        viewMenu.add(darkTheme);

        JMenu helpMenu = new JMenu("Справка");
        JMenuItem aboutItem = new JMenuItem("О программе");
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        exitItem.addActionListener(e -> System.exit(0));
        lightTheme.addActionListener(e -> applyTheme("light"));
        darkTheme.addActionListener(e -> applyTheme("dark"));
    }

    private void initMainPanel() {
        mainPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));

        modelListPanel = new ModelListPanel();
        propertiesPanel = new PropertiesPanel();

        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, modelListPanel, propertiesPanel);
        leftSplit.setDividerLocation(400);

        leftPanel.add(leftSplit, BorderLayout.CENTER);

        JPanel renderPanel = new JPanel();
        renderPanel.setBackground(Color.BLACK);
        renderPanel.setLayout(new BorderLayout());

        JLabel placeholder = new JLabel("3D View", SwingConstants.CENTER);
        placeholder.setForeground(Color.WHITE);
        placeholder.setFont(new Font("Arial", Font.BOLD, 24));
        renderPanel.add(placeholder, BorderLayout.CENTER);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.Y_AXIS));
        toolsPanel.setPreferredSize(new Dimension(250, 0));
        toolsPanel.setBorder(BorderFactory.createTitledBorder("Инструменты"));

        JButton moveButton = new JButton("Перемещение");
        JButton rotateButton = new JButton("Вращение");
        JButton scaleButton = new JButton("Масштаб");
        JButton deleteButton = new JButton("Удалить часть");

        toolsPanel.add(moveButton);
        toolsPanel.add(Box.createVerticalStrut(10));
        toolsPanel.add(rotateButton);
        toolsPanel.add(Box.createVerticalStrut(10));
        toolsPanel.add(scaleButton);
        toolsPanel.add(Box.createVerticalStrut(10));
        toolsPanel.add(deleteButton);
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

            UIManager.put("Panel.background", panelBg);
            UIManager.put("MenuBar.background", bgColor);
            UIManager.put("Menu.background", bgColor);
            UIManager.put("MenuItem.background", bgColor);
            UIManager.put("Menu.foreground", fgColor);
            UIManager.put("MenuItem.foreground", fgColor);
            UIManager.put("List.background", themeName.equals("dark") ? new Color(43, 43, 43) : Color.WHITE);
            UIManager.put("List.foreground", fgColor);
            UIManager.put("TextField.background", themeName.equals("dark") ? new Color(43, 43, 43) : Color.WHITE);
            UIManager.put("TextField.foreground", fgColor);
            UIManager.put("Label.foreground", fgColor);
            UIManager.put("CheckBox.foreground", fgColor);
            UIManager.put("Button.background", themeName.equals("dark") ? new Color(75, 75, 75) : new Color(225, 225, 225));
            UIManager.put("Button.foreground", fgColor);
            UIManager.put("TitledBorder.titleColor", fgColor);

            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            showErrorDialog("Ошибка применения темы: " + e.getMessage());
        }
    }

    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public ModelListPanel getModelListPanel() {
        return modelListPanel;
    }

    public PropertiesPanel getPropertiesPanel() {
        return propertiesPanel;
    }
}