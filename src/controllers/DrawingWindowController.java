package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import models.DrawingOperation;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.scene.input.MouseEvent.*;

public class DrawingWindowController extends MasterController {


    public VBox rootView;

    @FXML
    private Canvas drawingCanvas;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private JFXSlider sizeSlider;

    @FXML
    private StackPane stackPane;

    @FXML
    private ImageView imageView;

    @FXML
    private JFXButton chooseImageButton;

    @FXML
    private VBox getStartedLayout;

    @FXML
    private JFXButton freeDrawingButton;

    @FXML
    private JFXButton pencilTool;

    @FXML
    private JFXButton linesTool;

    @FXML
    private JFXButton eraserTool;

    //Drawing Items
    private HashMap<Integer, DrawingOperation> drawingOperations = new HashMap<>();
    private GraphicsContext graphicsContext;
    private int currentFigure = 1;
    private double startX = 0, startY = 0, endX = 0, endY = 0;
    private Set<Integer> historySet = new LinkedHashSet<>();
    private HashMap<Integer, DrawingOperation> deletedOperations = new HashMap<>();
    private DrawingOperation.DrawingType drawingType = DrawingOperation.DrawingType.LINE;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setRootView(rootView);
        graphicsContext = drawingCanvas.getGraphicsContext2D();
        drawingCanvas.toFront();
        imageView.fitWidthProperty().bind(stackPane.widthProperty());
        drawingCanvas.addEventFilter(MOUSE_PRESSED, this::mousePressed);
        drawingCanvas.addEventFilter(MOUSE_RELEASED, this::mouseReleased);
        drawingCanvas.addEventFilter(MOUSE_DRAGGED, this::mouseDragged);
        getStartedLayout.toFront();

        freeDrawingButton.setOnMouseClicked(v -> getStartedLayout.setVisible(false));

        linesTool.setOnMouseClicked(v -> drawingType = DrawingOperation.DrawingType.LINE);
        pencilTool.setOnMouseClicked(v -> drawingType = DrawingOperation.DrawingType.DOT);
        eraserTool.setOnMouseClicked(v -> drawingType = DrawingOperation.DrawingType.ERASER);
    }

    private void mousePressed(MouseEvent mouseEvent) {
        if (drawingType == DrawingOperation.DrawingType.LINE) {
            this.startX = mouseEvent.getX();
            this.startY = mouseEvent.getY();
        }
    }

    private void mouseReleased(MouseEvent mouseEvent) {
        currentFigure += 1;
    }

    private void mouseDragged(MouseEvent mouseEvent) {
        if (drawingType == DrawingOperation.DrawingType.LINE) {
            this.endX = mouseEvent.getX();
            this.endY = mouseEvent.getY();
        } else if (drawingType == DrawingOperation.DrawingType.DOT || drawingType == DrawingOperation.DrawingType.ERASER) {
            this.startX = mouseEvent.getX();
            this.startY = mouseEvent.getY();
            this.endX = mouseEvent.getX();
            this.endY = mouseEvent.getY();
            currentFigure += 1;
        }
        historySet.add(currentFigure);
        drawLine();
    }

    private void drawLine() {
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        drawingOperations.put(currentFigure, new DrawingOperation(graphicsContext,
                drawingType,
                startX, startY, endX, endY,
                Paint.valueOf(colorPicker.getValue().toString()),
                (int) sizeSlider.getValue()));
        for (HashMap.Entry<Integer, DrawingOperation> entry : drawingOperations.entrySet()) {
            entry.getValue().draw();
        }
    }


    @FXML
    void closeApplication() {
        Platform.exit();
    }

    @FXML
    void exportImage() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(rootView.getScene().getWindow());

        if (file != null) {
            try {
                WritableImage writableImage = pixelScaleSnapshot(stackPane, 2);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
                showMessage("Image Saved", false);
            } catch (IOException ex) {
                Logger.getLogger(DrawingWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private WritableImage pixelScaleSnapshot(StackPane pane, double pixelScale) {
        WritableImage writableImage = new WritableImage((int) Math.rint(pixelScale * pane.getWidth()), (int) Math.rint(pixelScale * pane.getHeight()));
        SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(Transform.scale(pixelScale, pixelScale));
        return pane.snapshot(spa, writableImage);
    }


    @FXML
    void loadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("Images", "*.png", "*jpg", "*jpeg");

        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showOpenDialog(rootView.getScene().getWindow());

        if (file != null) {
            imageView.setImage(new Image(file.toURI().toString()));
            getStartedLayout.setVisible(false);

            /*int pixels[] = new int[(int) (imageView.getImage().getWidth() * imageView.getImage().getHeight())];

            imageView.getImage().getPixelReader().getPixels(0,0,
                    (int) imageView.getImage().getWidth(),
                    (int) imageView.getImage().getHeight(),
                    imageView.getImage().getPixelReader().getPixelFormat(),
                    pixels,
                    1);*/
        } else {
            showMessage("Select a valid image", true);
        }

    }

    @FXML
    void redoStep(ActionEvent event) {

    }

    @FXML
    void undoStep(ActionEvent event) {
        if (drawingOperations.isEmpty()) return;
        if (drawingOperations.size() == 1) {
            // TODO: 4/17/2019 DISABLE THE UNDO BUUTTON
        }
        currentFigure -= 1;
        int figureToDelete = drawingOperations.size();
        System.err.println("DD" + figureToDelete);
        DrawingOperation drawingOperation = drawingOperations.remove(figureToDelete);
        System.err.println(drawingOperations);
        deletedOperations.put(figureToDelete, drawingOperation);
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        for (HashMap.Entry<Integer, DrawingOperation> entry : drawingOperations.entrySet()) {
            entry.getValue().draw();
        }
    }

    private String getImageSize(final File file) {
        Image image = new Image(file.toURI().toString());
        return image.getWidth() + " " + image.getHeight();
    }
}
