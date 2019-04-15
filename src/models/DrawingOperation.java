package models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

import java.io.File;
import java.net.URI;

public class DrawingOperation {

    private GraphicsContext context;
    private double startX, startY;
    private double endX, endY;
    private Paint paint;
    private int width;
    private int stroke;

    public DrawingOperation(GraphicsContext context, double startX, double startY, double endX, double endY, Paint paint, int width, int stroke) {
        this.context = context;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.paint = paint;
        this.width = width;
        this.stroke = stroke;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getStroke() {
        return stroke;
    }

    public void setStroke(int stroke) {
        this.stroke = stroke;
    }

    public void draw() {
        context.setStroke(paint);
        context.setLineWidth(width);
        context.strokeLine(startX, startY, endX, endY);
    }
}
