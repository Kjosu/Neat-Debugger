package de.kjosu.neatdebug.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.neatdebug.util.Point;

public class GenomeRenderer extends Renderable {

	private final ThreadLocalRandom random = ThreadLocalRandom.current();

	// Variables
	public int nodeRadius = 15;
	public int connectionWidth = 2;

	public Color inputNodeColor = new Color(144, 238, 144);
	public Color hiddenNodeColor = new Color(255, 127, 80);
	public Color outputNodeColor = new Color(0, 191, 255);

	public Color connectionEnabledColor = Color.GREEN;
	public Color connectionDisabledColor = Color.RED;
	// ----------------
	private NodeGene selectedNode;
	private NodeGene tempNode;
	private ConnectionGene selectedConnection;
	// ----------------

	private Genome<?> genome;

	private final Map<NodeGene, Point> nodeCoordinates = new HashMap<>();

	public GenomeRenderer(final RenderSettings settings) {
		super(settings);
	}

	public GenomeRenderer(final RenderSettings settings, final Genome<?> genome) {
		super(settings);
		this.genome = genome;
	}

	@Override
	public void update(final double delta) {
		if (genome == null) {
			return;
		}

		for (final NodeGene node : genome.getNodes().values()) {
			if (nodeCoordinates.containsKey(node)) {
				continue;
			}

			final Point point = new Point();

			switch (node.getType()) {
				case Input:
					point.x = .1;
					point.y = (node.getId() + 1D) / (genome.getInputSize() + 1D);
					break;
				case Output:
					point.x = .9;
					point.y = (node.getId() - genome.getInputSize() + 1D) / (genome.getOutputSize() + 1D);
					break;
				default:
					point.x = random.nextDouble(0.15, 0.85);
					point.y = random.nextDouble(0.1, 0.9);
					break;
			}

			if (selectedNode != null && node.getId() == selectedNode.getId()) {
				selectedNode = node;
			}

			nodeCoordinates.put(node, point);
		}

		if (selectedNode != null) {
			selectedNode = genome.getNode(selectedNode.getId());
			selectedConnection = genome.getSelf(selectedNode.getSelf());
		}

		if (selectedConnection != null) {
			selectedConnection = genome.getConnection(selectedConnection.getId());
		}
	}

	@Override
	public void render(final Graphics2D g) {
		if (genome == null) {
			return;
		}

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, settings.renderWidth, settings.renderHeight);

		for (final ConnectionGene c : genome.getGates().keySet()) {
			drawGateConnection(g, c);
		}

		for (final ConnectionGene c : genome.getSelfs().values()) {
			drawSelfConnection(g, c);
		}

		for (final ConnectionGene c : genome.getConnections().values()) {
			drawConnection(g, c);
		}

		for (final NodeGene node : genome.getNodes().values()) {
			drawNode(g, node);
		}

		drawNodeSelectionInfo(g);
		drawConnectionSelectionInfo(g);
	}

	private void drawNodeSelectionInfo(Graphics2D g) {
		if (selectedNode == null) {
			return;
		}

		g.setFont(new Font("Consolas", Font.PLAIN, 12));

		int x = settings.renderWidth - 200;
		int y = settings.renderHeight - 125;

		g.setColor(Color.BLACK);
		g.fillRect(x, y, 200, 125);

		final String[] data = new String[] {
				String.format("Innovation: %s", selectedNode.getId()),
				"",
				String.format("Activation: %.2f", selectedNode.getActivation()),
				String.format("NewState: %.2f", selectedNode.getState()),
				String.format("OldState: %.2f", selectedNode.getOld()),
				String.format("Bias: %.2f", selectedNode.getBias()),
				String.format("Squash: %s", selectedNode.getSquash()),
		};

		x += 10;
		y += 15;

		g.setColor(Color.WHITE);
		for (final String s : data) {
			g.drawString(s, x, y);
			y += 15;
		}
	}

	private void drawConnectionSelectionInfo(Graphics2D g) {
		if (selectedConnection == null) {
			return;
		}

		g.setFont(new Font("Consolas", Font.PLAIN, 12));

		int x = settings.renderWidth - 400;
		int y = settings.renderHeight - 125;

		g.setColor(Color.BLACK);
		g.fillRect(x, y, 200, 125);

		String[] data = new String[] {
			String.format("Innovation: %s", selectedConnection.getId()),
				"",
				String.format("Input Node: %s", selectedConnection.getFromNode()),
				String.format("Output Node: %s", selectedConnection.getToNode()),
				String.format("Gater Node: %s", selectedConnection.getGaterNode()),
				String.format("Weight: %.2f", selectedConnection.getWeight()),
				String.format("Gain: %.2f", selectedConnection.getGain()),
				String.format("Enabled: %s", selectedConnection.isEnabled())
		};

		x += 10;
		y += 15;

		g.setColor(Color.WHITE);
		for (final String s : data) {
			g.drawString(s, x, y);
			y += 15;
		}
	}

	private void drawNode(final Graphics2D g, final NodeGene node) {
		if (!nodeCoordinates.containsKey(node)) {
			return;
		}

		final Point p = createScreenCoordinates(nodeCoordinates.get(node));

		switch (node.getType()) {
			case Input:
				g.setColor(inputNodeColor);
				break;
			case Output:
				g.setColor(outputNodeColor);
				break;
			default:
				g.setColor(hiddenNodeColor);
				break;
		}

		final int x = (int) (p.x - nodeRadius);
		final int y = (int) (p.y - nodeRadius);

		g.setStroke(new BasicStroke(1));
		g.fillOval(x, y, nodeRadius * 2, nodeRadius * 2);

		g.setColor(Color.BLACK);
		g.drawOval(x, y, nodeRadius * 2, nodeRadius * 2);

		g.setFont(new Font("Consolas", Font.BOLD, 25));

		final String text = String.valueOf(node.getId());
		final Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);

		bounds.setRect(x + nodeRadius - bounds.getWidth() / 2, y + nodeRadius + bounds.getHeight() / 4, bounds.getWidth(), bounds.getHeight());

		g.setColor(Color.WHITE);
		g.drawString(String.valueOf(node.getId()), (int) bounds.getX(), (int) bounds.getY());

		g.setColor(Color.BLACK);
		g.drawString(String.valueOf(node.getId()), (int) bounds.getX() - 1, (int) bounds.getY() - 1);
	}

	private void drawConnection(final Graphics2D g, final ConnectionGene c) {
		final Point inNode = createScreenCoordinates(nodeCoordinates.get(genome.getNode(c.getFromNode())));
		final Point outNode = createScreenCoordinates(nodeCoordinates.get(genome.getNode(c.getToNode())));

		if (inNode == null || outNode == null) {
			return;
		}

		g.setColor(c.isEnabled() ? connectionEnabledColor : connectionDisabledColor);
		g.setStroke(new BasicStroke(connectionWidth));

		g.drawLine((int) inNode.x, (int) inNode.y, (int) outNode.x, (int) outNode.y);

		g.setColor(Color.BLACK);
		g.drawString(String.format("%.2f", c.getWeight()), (int) (inNode.x + outNode.x) / 2, (int) (inNode.y + outNode.y) / 2);
	}

	private void drawSelfConnection(final Graphics2D g, final ConnectionGene c) {
		if (c.getFromNode() != c.getToNode()) {
			return;
		}

		final Point p = createScreenCoordinates(nodeCoordinates.get(genome.getNode(c.getFromNode())));

		if (p == null) {
			return;
		}

		g.setColor(c.isEnabled() ? connectionEnabledColor : connectionDisabledColor);
		g.setStroke(new BasicStroke(connectionWidth));
		g.drawOval((int) p.x, (int) p.y, nodeRadius * 2, nodeRadius * 2);

		g.setColor(Color.BLACK);
		g.drawString(String.format("%.2f", c.getWeight()), (int) p.x + nodeRadius, (int) p.y + nodeRadius * 2);
	}

	private void drawGateConnection(final Graphics2D g, final ConnectionGene c) {
		final Point inNode = createScreenCoordinates(nodeCoordinates.get(genome.getNode(c.getFromNode())));
		final Point outNode = createScreenCoordinates(nodeCoordinates.get(genome.getNode(c.getToNode())));
		final Point gater = createScreenCoordinates(nodeCoordinates.get(genome.getNode(c.getGaterNode())));

		if (inNode == null || outNode == null || gater == null) {
			return;
		}

		final double x2 = (inNode.x + outNode.x) / 2;
		final double y2 = (inNode.y + outNode.y) / 2;

		final double ctrlx = (gater.x + x2) / 2;
		final double ctrly = (gater.y + y2) / 2 + 50;

		final QuadCurve2D curve = new QuadCurve2D.Double();
		curve.setCurve(gater.x, gater.y, ctrlx, ctrly, x2, y2);

		g.setColor(c.isEnabled() ? connectionEnabledColor : connectionDisabledColor);
		g.setStroke(new BasicStroke(connectionWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0));

		g.draw(curve);
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		tempNode = null;
		selectedConnection = null;

		for (final Entry<NodeGene, Point> entry : nodeCoordinates.entrySet()) {
			final NodeGene node = entry.getKey();
			final Point point = createScreenCoordinates(entry.getValue());

			if (point.distance(e.getX(), e.getY()) <= nodeRadius) {
				tempNode = node;
				break;
			}
		}

		if (e.getClickCount() == 2) {
			selectedNode = tempNode;

			if (tempNode == null) {
			    List<ConnectionGene> allConnections = new Stack<>();
			    allConnections.addAll(genome.getConnections().values());
			    allConnections.addAll(genome.getGates().keySet());
			    allConnections.addAll(genome.getSelfs().values());

				for (ConnectionGene c : allConnections) {
					Point p1 = nodeCoordinates.get(genome.getNode(c.getFromNode()));
					Point p2 = nodeCoordinates.get(genome.getNode(c.getToNode()));

					if (p1 == null || p2 == null) {
						continue;
					}

					if (isBetweenLine(createScreenCoordinates(p1), createScreenCoordinates(p2),
                            new Point(e.getX(), e.getY()), connectionWidth)) {
                        selectedConnection = c;
                        break;
                    }
				}
			} else {
			    selectedConnection = genome.getSelf(tempNode.getSelf());
            }
		} else if (tempNode == null) {
			selectedNode = null;
		}
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (tempNode == null) {
			return;
		}

		final Point p = nodeCoordinates.get(tempNode);

		p.x = e.getX() / (double) settings.renderWidth;
		p.y = e.getY() / (double) settings.renderHeight;
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		tempNode = null;
	}

	private Point createScreenCoordinates(final Point p) {
		if (p == null) {
			return null;
		}

		return new Point(p.x * settings.renderWidth, p.y * settings.renderHeight);
	}

	public Genome<?> getGenome() {
		return genome;
	}

	public void setGenome(final Genome<?> genome) {
		this.genome = genome;
	}

	private boolean isBetweenLine(Point a, Point b, Point c, double threshold) {
	    double distance = Math.abs((b.x - a.x) * (a.y - c.y) - (a.x - c.x) * (b.y - a.y)) /
                Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));

        return Math.abs(distance) <= threshold;
    }
}
