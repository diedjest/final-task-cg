package ru.vsu.cs.finaltaskcg.ui;

import ru.vsu.cs.finaltaskcg.math.Vector3f;
import ru.vsu.cs.finaltaskcg.model.Model;
import ru.vsu.cs.finaltaskcg.model.Polygon;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RenderPanel extends JPanel {
    private List<Model> models;
    private Color modelColor = Color.GRAY;

    public RenderPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }

    public void setModelColor(Color color) {
        this.modelColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (models == null || models.isEmpty()) {
            g.setColor(Color.BLACK);
            g.drawString("Нет моделей для отображения", getWidth() / 2 - 80, getHeight() / 2);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Model model : models) {
            drawModel(g2d, model);
        }
    }

    private void drawModel(Graphics2D g2d, Model model) {
        if (model.getVertices().isEmpty()) return;

        g2d.setColor(modelColor);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        float scale = 100.0f;

        for (Polygon polygon : model.getPolygons()) {
            List<Integer> vertexIndices = polygon.getVertexIndices();
            if (vertexIndices.size() < 2) continue;

            int[] xPoints = new int[vertexIndices.size()];
            int[] yPoints = new int[vertexIndices.size()];

            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i);
                if (vertexIndex >= 0 && vertexIndex < model.getVertices().size()) {
                    Vector3f vertex = model.getVertices().get(vertexIndex);
                    xPoints[i] = (int) (centerX + vertex.x * scale);
                    yPoints[i] = (int) (centerY - vertex.y * scale);
                }
            }

            g2d.drawPolygon(xPoints, yPoints, vertexIndices.size());
        }
    }
}