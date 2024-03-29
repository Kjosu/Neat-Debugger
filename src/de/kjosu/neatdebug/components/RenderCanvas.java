package de.kjosu.neatdebug.components;


import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.time.Duration;
import java.time.Instant;

public abstract class RenderCanvas extends ResizableCanvas implements Runnable {

    private int maxTicksPerSecond = 10;
    private int maxFramesPerSecond = 30;

    private int ticksPerSecond = 0;
    private int framesPerSecond = 0;

    private Thread thread;
    private boolean running;

    public RenderCanvas() {
        setOnMousePressed(event -> onMouseDown(event));
        setOnMouseReleased(event -> onMouseUp(event));
        setOnMouseMoved(event -> onMouseMoved(event));
        setOnMouseDragged(event -> onMouseDragged(event));

        setOnKeyPressed(event -> onKeyDown(event));
        setOnKeyReleased(event -> onKeyUp(event));
    }

    @Override
    public void run() {
        Instant lastRefresh = Instant.now();
        Instant lastTick = lastRefresh;

        int tick = 0;
        int frame = 0;

        while (running) {
            Instant now = Instant.now();
            double delta = Duration.between(lastRefresh, now).toMillis();

            if (delta >= 1000D / maxTicksPerSecond * tick) {
                double updateDelta = Duration.between(lastTick, now).toMillis() / 1000D;
                onUpdate(updateDelta);

                lastTick = now;
                tick++;
            }

            if (delta >= 1000D / maxFramesPerSecond * frame) {
                render();

                frame++;
            }

            if (delta >= 1000D) {
                ticksPerSecond = tick;
                framesPerSecond = frame;

                tick = 0;
                frame = 0;

                lastRefresh = now;
            }
        }
    }

    private void render() {
        GraphicsContext g = getGraphicsContext2D();
        onRender(g);
    }

    public abstract void onUpdate(double delta);

    public abstract void onRender(GraphicsContext g);

    protected abstract void onMouseDown(MouseEvent e);
    protected abstract void onMouseUp(MouseEvent e);
    protected abstract void onMouseMoved(MouseEvent e);
    protected abstract void onMouseDragged(MouseEvent e);

    protected abstract void onKeyDown(KeyEvent e);
    protected abstract void onKeyUp(KeyEvent e);

    public void start() {
        if (running) {
            throw new IllegalStateException("Already rendering");
        }

        thread = new Thread(this);
        running = true;

        thread.start();
    }

    public void stop() {
        running = false;
    }

    public int getMaxTicksPerSecond() {
        return maxTicksPerSecond;
    }

    public void setMaxTicksPerSecond(int maxTicksPerSecond) {
        this.maxTicksPerSecond = maxTicksPerSecond;
    }

    public int getMaxFramesPerSecond() {
        return maxFramesPerSecond;
    }

    public void setMaxFramesPerSecond(int maxFramesPerSecond) {
        this.maxFramesPerSecond = maxFramesPerSecond;
    }

    public int getTicksPerSecond() {
        return ticksPerSecond;
    }

    public int getFramesPerSecond() {
        return framesPerSecond;
    }

    public boolean isRunning() {
        return running;
    }
}
