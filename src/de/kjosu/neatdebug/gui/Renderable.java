package de.kjosu.neatdebug.gui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class Renderable implements KeyListener, MouseListener, MouseMotionListener {

	protected RenderSettings settings;

	public Renderable(final RenderSettings settings) {
		if (settings == null) {
			throw new IllegalArgumentException("Settings can't be null");
		}

		this.settings = settings;
	}

	public abstract void update(double delta);

	public abstract void render(Graphics2D g);

	@Override
	public void keyTyped(final KeyEvent e) {

	}

	@Override
	public void keyPressed(final KeyEvent e) {

	}

	@Override
	public void keyReleased(final KeyEvent e) {

	}

	@Override
	public void mouseClicked(final MouseEvent e) {

	}

    @Override
	public void mousePressed(final MouseEvent e) {

    }

    @Override
	public void mouseReleased(final MouseEvent e) {

    }

    @Override
	public void mouseEntered(final MouseEvent e) {

    }

    @Override
	public void mouseExited(final MouseEvent e) {

    }

    @Override
	public void mouseDragged(final MouseEvent e) {

    }

    @Override
	public void mouseMoved(final MouseEvent e) {

    }

	public RenderSettings getSettings() {
		return settings;
	}
}
