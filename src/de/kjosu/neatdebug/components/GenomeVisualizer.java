package de.kjosu.neatdebug.components;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.jnstinct.util.MapUtils;
import de.kjosu.neatdebug.util.Point;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class GenomeVisualizer extends RenderCanvas {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private int nodeRadius = 20;
    private int connectionWidth = 2;

    private Map<NodeGene, Point> coordinates = new HashMap<>();

    private NodeInspector nodeInspector;
    private ConnectionInspector connectionInspector;

    private Genome<?> genome;
    private NodeGene selectedNode;

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
            getCoordinates(node);

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

        for (NodeGene node : genome.getNodes().values()) {
            renderSelfConnection(g, genome.getSelf(node.getSelf()));
        }

        for (ConnectionGene c : genome.getConnections().values()) {
            renderGate(g, c);
        }

        for (ConnectionGene c : genome.getConnections().values()) {
            renderConnection(g, c);
        }

        for (NodeGene node : genome.getNodes().values()) {
            renderNode(g, node);
        }
    }

    @Override
    protected void onMouseDown(MouseEvent e) {
        for (Map.Entry<NodeGene, Point> entry : coordinates.entrySet()) {
            Point p = getScreenCoordinates(entry.getValue());

            if (p.distance(e.getX(), e.getY()) > nodeRadius) {
                continue;
            }

            selectedNode = entry.getKey();

            if (nodeInspector != null && e.getClickCount() == 2) {
                nodeInspector.set(genome, entry.getKey());
            }

            break;
        }
    }

    @Override
    protected void onMouseUp(MouseEvent e) {
        selectedNode = null;
    }

    @Override
    protected void onMouseMoved(MouseEvent e) {

    }

    @Override
    protected void onMouseDragged(MouseEvent e) {
        if (selectedNode == null) {
            return;
        }

        Point p = coordinates.get(selectedNode);

        p.x = e.getX() / getWidth();
        p.y = e.getY() / getHeight();
    }

    @Override
    protected void onKeyDown(KeyEvent e) {

    }

    @Override
    protected void onKeyUp(KeyEvent e) {

    }

    private void renderNode(GraphicsContext g, NodeGene node) {
        int nodeDiameter = nodeRadius * 2;

        g.setFont(new Font("Consolas", 15));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);

        Point point = getCoordinates(node);

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

    private void renderSelfConnection(final GraphicsContext g, final ConnectionGene c) {
        if (c == null || c.getFromNode() != c.getToNode()) {
            return;
        }

        Point node = coordinates.get(genome.getNode(c.getFromNode()));

        if (node == null) {
            return;
        }

        Point p = getScreenCoordinates(node);
        int nodeDiameter = nodeRadius * 2;

        g.setStroke(c.isEnabled() ? Color.GREEN : Color.RED);
        g.setLineWidth(connectionWidth);
        g.strokeOval(p.x, p.y, nodeDiameter, nodeDiameter);
    }

    private void renderConnection(GraphicsContext g, ConnectionGene c) {
        g.setLineWidth(connectionWidth);
        g.setLineDashes(null);

        Point p1 = getCoordinates(genome.getNode(c.getFromNode()));
        Point p2 = getCoordinates(genome.getNode(c.getToNode()));

        if (p1 == null || p2 == null) {
            return;
        }

        g.setStroke((c.isEnabled()) ? Color.GREEN : Color.RED);

        g.strokeLine(p1.x, p1.y, p2.x, p2.y);
    }

    private void renderGate(GraphicsContext g, ConnectionGene c) {
        g.setLineWidth(connectionWidth);
        g.setLineDashes(9, 8 ,7);

        Point p1 = getCoordinates(genome.getNode(c.getFromNode()));
        Point p2 = getCoordinates(genome.getNode(c.getToNode()));
        Point g1 = getCoordinates(genome.getNode(c.getGaterNode()));

        if (p1 == null || p2 == null || g1 == null) {
            return;
        }

        double mx = (p1.x + p2.x) / 2;
        double my = (p1.y + p2.y) / 2;

        double ctrlx = (g1.x + mx) / 2;
        double ctrly = (g1.y + my) / 2 - 20;

        final QuadCurve2D curve = new QuadCurve2D.Double();
        curve.setCurve(g1.x, g1.y, ctrlx, ctrly, mx, my);

        g.setStroke(c.isEnabled() ? Color.GREEN : Color.RED);
        g.beginPath();
        g.moveTo(g1.x, g1.y);
        g.bezierCurveTo(ctrlx, ctrly, mx, my, mx, my);
        g.stroke();
    }

    private Point getCoordinates(NodeGene node) {
        if (node == null) {
            return null;
        }

        Point p = coordinates.get(node);

        if (p != null) {
            return getScreenCoordinates(p);
        }

        p = new Point();

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

        return getScreenCoordinates(p);
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
