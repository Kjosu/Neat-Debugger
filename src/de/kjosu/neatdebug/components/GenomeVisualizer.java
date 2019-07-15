package de.kjosu.neatdebug.components;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.neatdebug.util.Point;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GenomeVisualizer extends RenderCanvas {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private int nodeRadius = 20;
    private int connectionWidth = 2;

    private Map<NodeGene, Point> coordinates = new HashMap<>();

    private NodeInspector nodeInspector;
    private ConnectionInspector connectionInspector;

    private Genome<?> genome;

    public GenomeVisualizer() {

    }

    public GenomeVisualizer(Genome<?> genome) {
        this.genome = genome;
    }

    @Override
    public void onUpdate(double delta) {
        if (genome == null) {
            return;
        }

        for (NodeGene node : genome.getNodes().values()) {
            Point p = new Point();

            switch (node.getType()) {
                case Input:
                    p.x = .1;
                    p.y = (node.getId() + 1D) / (genome.getInputSize() + 1D);
                    break;
                case Output:
                    p.x = .9;
                    p.y = (node.getId() - genome.getInputSize() + 1D) / (genome.getOutputSize() + 1D);
                    break;
                default:
                    p.x = random.nextDouble(0.15, 0.85);
                    p.y = random.nextDouble(0.1, 0.9);
                    break;
            }

            coordinates.put(node, p);

            if (nodeInspector != null && nodeInspector.getNode() != null &&
                    nodeInspector.getNode().getId() == node.getId()) {
                nodeInspector.set(genome, node);
            }
        }

        if (connectionInspector == null || connectionInspector.getConnection() == null) {
            return;
        }

        for (ConnectionGene c : genome.getConnections().values()) {
            if (c.getId() == connectionInspector.getConnection().getId()) {
                connectionInspector.set(genome, c);
                break;
            }
        }
    }

    @Override
    public void onRender(GraphicsContext g) {
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFill(Color.WHITE);
        g.setTextAlign(TextAlignment.LEFT);
        g.setTextBaseline(VPos.BASELINE);
        g.setFont(new Font("Consolas", 12));
        g.fillText(String.format("UPS: %s", getTicksPerSecond()), 5, 15);
        g.fillText(String.format("FPS: %s", getFramesPerSecond()), 5, 30);

        if (genome == null) {
            return;
        }

        renderGates(g);
        renderConnections(g);
        renderNodes(g);
    }

    private void renderNodes(GraphicsContext g) {
        int nodeDiameter = nodeRadius * 2;

        g.setFont(new Font("Consolas", 15));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);

        for (Map.Entry<NodeGene, Point> entry : coordinates.entrySet()) {
            NodeGene node = entry.getKey();
            Point point = getScreenCoordinates(entry.getValue());

            switch (node.getType()) {
                case Input:
                    g.setFill(Color.DEEPSKYBLUE);
                    break;
                case Output:
                    g.setFill(Color.ORANGE);
                    break;
                default:
                    g.setFill(Color.LIGHTGRAY);
                    break;
            }

            g.fillOval(point.x - nodeRadius, point.y - nodeRadius, nodeDiameter, nodeDiameter);

            g.setFill(Color.WHITE);
            g.fillText(String.valueOf(node.getId()), point.x, point.y);
        }
    }

    private void renderConnections(GraphicsContext g) {
        g.setLineWidth(connectionWidth);
        g.setLineDashes(null);

        for (ConnectionGene c : genome.getConnections().values()) {
            Point inNode = coordinates.get(genome.getNode(c.getFromNode()));
            Point outNode = coordinates.get(genome.getNode(c.getToNode()));

            if (inNode == null || outNode == null) {
                continue;
            }

            Point p1 = getScreenCoordinates(inNode);
            Point p2 = getScreenCoordinates(outNode);

            g.setStroke((c.isEnabled()) ? Color.GREEN : Color.RED);

            g.strokeLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void renderGates(GraphicsContext g) {
        g.setLineWidth(connectionWidth);
        g.setLineDashes(9, 8 ,7);

        for (ConnectionGene c : genome.getConnections().values()) {
            Point inNode = coordinates.get(genome.getNode(c.getFromNode()));
            Point outNode = coordinates.get(genome.getNode(c.getToNode()));
            Point gater = coordinates.get(genome.getNode(c.getGaterNode()));

            if (inNode == null || outNode == null || gater == null) {
                continue;
            }

            Point p1 = getScreenCoordinates(inNode);
            Point p2 = getScreenCoordinates(outNode);
            Point g1 = getScreenCoordinates(gater);

            double mx = (p1.x + p2.x) / 2;
            double my = (p1.y + p2.y) / 2;

            double ctrlx = (g1.x + mx) / 2;
            double ctrly = (g1.y + my) / 2 - 20;

            final QuadCurve2D curve = new QuadCurve2D.Double();
            curve.setCurve(gater.x, gater.y, ctrlx, ctrly, mx, my);

            g.setStroke(c.isEnabled() ? Color.GREEN : Color.RED);
            g.beginPath();
            g.moveTo(g1.x, g1.y);
            g.bezierCurveTo(ctrlx, ctrly, mx, my, mx, my);
            g.stroke();
        }
    }

    private Point getScreenCoordinates(Point p) {
        return new Point(p.x * getWidth(), p.y * getHeight());
    }

    public NodeInspector getNodeInspector() {
        return nodeInspector;
    }

    public void setNodeInspector(NodeInspector nodeInspector) {
        this.nodeInspector = nodeInspector;
    }

    public ConnectionInspector getConnectionInspector() {
        return connectionInspector;
    }

    public void setConnectionInspector(ConnectionInspector connectionInspector) {
        this.connectionInspector = connectionInspector;
    }

    public Genome<?> getGenome() {
        return genome;
    }

    public void setGenome(Genome<?> genome) {
        this.genome = genome;
    }
}
