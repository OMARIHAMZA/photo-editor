package models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class DrawingOperation {

    public enum DrawingType {

        LINE, DOT, ERASER

    }

    private GraphicsContext context;
    private DrawingType drawingType;
    private double startX, startY;
    private double endX, endY;
    private int width;
    private Paint stroke;

    public DrawingOperation(GraphicsContext context, DrawingType drawingType, double startX, double startY, double endX, double endY, Paint stroke, int width) {
        this.context = context;
        this.startX = startX;
        this.startY = startY;
        this.drawingType = drawingType;
        this.endX = endX;
        this.endY = endY;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public void draw() {
        context.setStroke(stroke);
        context.setLineWidth(width);
            context.strokeLine(startX, startY, endX, endY);
    }
}
