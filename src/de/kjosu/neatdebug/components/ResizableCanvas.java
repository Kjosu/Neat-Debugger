package de.kjosu.neatdebug.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {

    private BooleanProperty resizableProperty = new SimpleBooleanProperty(true);

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    @Override
    public boolean isResizable() {
        return resizableProperty.get();
    }

    @Override
    public void resize(double width, double height) {
        super.setWidth(width);
        super.setHeight(height);
    }
}
