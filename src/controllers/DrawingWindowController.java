package controllers;

import com.jfoenix.controls.JFXToolbar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import models.DrawingOperation;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;

import static javafx.scene.input.MouseEvent.*;

public class DrawingWindowController extends MasterController {


    public VBox rootView;

    private JFXToolbar mToolbar;

    @FXML
    private Canvas drawingCanvas;

    //Drawing Items
    private HashMap<Integer, DrawingOperation> drawingOperations = new HashMap<>();
    private GraphicsContext graphicsContext;
    private int currentFigure = 0;
    private double startX = 0, startY = 0, endX = 0, endY = 0;
    @FXML
    private ImageView imageView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setRootView(rootView);
        setupToolbar(mToolbar);
        graphicsContext = drawingCanvas.getGraphicsContext2D();
        testPerformance(50);
        imageView.setImage(new Image(new File("C:/Users/Asus/Pictures/tuf.jpg").toURI().toString()));
        drawingCanvas.toFront();
        drawingCanvas.addEventFilter(MOUSE_PRESSED, this::mousePressed);
        drawingCanvas.addEventFilter(MOUSE_RELEASED, this::mouseReleased);
        drawingCanvas.addEventFilter(MOUSE_DRAGGED, this::mouseDragged);
    }

    private void mousePressed(MouseEvent mouseEvent) {
        this.startX = mouseEvent.getX();
        this.startY = mouseEvent.getY();
    }

    private void mouseReleased(MouseEvent mouseEvent) {
        currentFigure += 1;
    }

    private void mouseDragged(MouseEvent mouseEvent) {
        this.endX = mouseEvent.getX();
        this.endY = mouseEvent.getY();
        drawLine();
    }

    private void drawLine() {
        System.out.println(drawingOperations.size());
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        drawingOperations.put(currentFigure, new DrawingOperation(graphicsContext, startX, startY, endX, endY, Paint.valueOf("#000000"), 1, 0));
        for (HashMap.Entry<Integer, DrawingOperation> entry : drawingOperations.entrySet()) {
            entry.getValue().draw();
        }
    }

    private void testPerformance(int count) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < count; i++) {
                drawingOperations.put(i, new DrawingOperation(
                        graphicsContext,
                        new Random().nextInt(600),
                        new Random().nextInt(400),
                        new Random().nextInt(600),
                        new Random().nextInt(400),
                        Paint.valueOf("#000000"),
                        1,
                        1
                ));
                drawingOperations.get(i).draw();
            }
            Platform.runLater(() -> showSnackBar(count + " Lines were successfully drawn"));

        }).start();
    }


}
