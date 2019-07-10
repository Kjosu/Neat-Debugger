package de.kjosu.neatdebug.old;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.time.Duration;
import java.time.Instant;

public class RenderEngine extends Canvas implements Runnable {

	private final Renderable renderable;
	private final RenderSettings settings;

	private Thread thread;
	private boolean running;

	public RenderEngine(final Renderable renderable) {
		this.renderable = renderable;
		this.settings = renderable.getSettings();

		addKeyListener(renderable);
		addMouseListener(renderable);
		addMouseMotionListener(renderable);
	}

	@Override
	public void run() {
		Instant lastRefresh = Instant.now();
		Instant lastTick = lastRefresh;

		int tick = 0;
		int frame = 0;

		while (running) {
			final Instant now = Instant.now();
			final double delta = Duration.between(lastRefresh, now).toMillis();

			if (delta >= 1000D / settings.maxTicksPerSecond * tick) {
				final double updateDelta = Duration.between(lastTick, now).toMillis() / 1000D;
				renderable.update(updateDelta);

				lastTick = now;
				tick++;
			}

			if (delta >= 1000D / settings.maxFramesPerSecond * frame) {
				render();

				frame++;
			}

			if (delta >= 1000D) {
				settings.ticksPerSecond = tick;
				settings.framesPerSecond = frame;

				tick = 0;
				frame = 0;

				lastRefresh = now;
			}
		}
	}

	private void render() {
		if (!isDisplayable()) {
			return;
		}

		final BufferStrategy bs = getBufferStrategy();

		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		settings.renderWidth = getWidth();
		settings.renderHeight = getHeight();

		final Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, settings.renderWidth, settings.renderHeight);

		renderable.render(g);

		g.dispose();
		bs.show();
	}

	public void start() {
		if (running) {
			throw new IllegalStateException("Engine is already running");
		}

		thread = new Thread(this);
		running = true;

		thread.start();
	}

	public void stop() {
		running = false;
	}

	public Renderable getRenderable() {
		return renderable;
	}
}
