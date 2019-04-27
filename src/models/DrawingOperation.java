package models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class DrawingOperation {

    public enum DrawingType {

        SHAPE, BRUSH, ERASER, TEXT

    }

    public enum Shape {
        RECTANGLE, OVAL, ROUNDED_RECTANGLE, LINE
    }

    private GraphicsContext context;
    private DrawingType drawingType;
    private double startX, startY;
    private double endX, endY;
    private int width;
    private Paint stroke;
    private String text;
    private Shape shape;

    public DrawingOperation(GraphicsContext context, DrawingType drawingType, double startX, double startY, double endX, double endY, Paint stroke, int width, Shape shape, String... params) {
        this.context = context;
        this.startX = startX;
        this.startY = startY;
        this.drawingType = drawingType;
        this.endX = endX;
        this.endY = endY;
        this.width = width;
        this.stroke = stroke;
        this.shape = shape;
        this.text = params.length == 0 ? "" : params[0];
    }

    public void draw() {
        context.setStroke(stroke);
        context.setLineWidth(width);
        if (drawingType == DrawingType.TEXT) {
            context.strokeText(text, startX, startY);
        } else {
            switch (shape) {
                case LINE:
                    context.strokeLine(startX, startY, endX, endY);
                    break;

                case OVAL:
                    context.strokeOval(startX, startY, endX, endY);
                    break;

                case RECTANGLE:
                    context.strokeRect(startX, startY, endX, endY);
                    break;

                case ROUNDED_RECTANGLE:
                    context.strokeRoundRect(startX, startY, endX, endY, 10, 10);
                    break;

            }
        }
    }
}
