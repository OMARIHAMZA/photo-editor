package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
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
    private MenuItem linesTool;

    @FXML
    private MenuItem eraser;

    @FXML
    private MenuItem pencilTool;

    @FXML
    private MenuItem rectBrush;

    @FXML
    private MenuItem ovalBrush;

    @FXML
    private MenuItem roundedRectBrush;

    @FXML
    private MenuItem rectShape;

    @FXML
    private MenuItem ovalShape;

    @FXML
    private MenuItem roundedRectangleShape;


    @FXML
    private JFXTextField textInput;

    @FXML
    private JFXButton textTool;


    //Drawing Items
    private HashMap<Integer, DrawingOperation> drawingOperations = new HashMap<>();
    private GraphicsContext graphicsContext;
    private int currentFigure = 0;
    private DrawingOperation.Shape currentShape;
    private double startX = 0, startY = 0, endX = 0, endY = 0;
    private Set<Integer> historySet = new LinkedHashSet<>();
    private LinkedHashMap<Integer, DrawingOperation> deletedOperations = new LinkedHashMap<>();
    private DrawingOperation.DrawingType drawingType = DrawingOperation.DrawingType.SHAPE;


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

        pencilTool.setOnAction(v -> {
            drawingType = DrawingOperation.DrawingType.BRUSH;
            currentShape = DrawingOperation.Shape.LINE;
        });
        eraser.setOnAction(v -> {
            drawingType = DrawingOperation.DrawingType.ERASER;
            currentShape = DrawingOperation.Shape.LINE;
        });
        textTool.setOnAction(v -> drawingType = DrawingOperation.DrawingType.TEXT);

        configureShapes();
    }

    private void configureShapes() {
        currentShape = DrawingOperation.Shape.LINE;
        linesTool.setOnAction(v -> {
            drawingType = DrawingOperation.DrawingType.SHAPE;
            currentShape = DrawingOperation.Shape.LINE;
        });
        rectBrush.setOnAction(v -> {
            drawingType = DrawingOperation.DrawingType.BRUSH;
            currentShape = DrawingOperation.Shape.RECTANGLE;
        });
        rectShape.setOnAction(v -> {
            drawingType = DrawingOperation.DrawingType.SHAPE;
            currentShape = DrawingOperation.Shape.RECTANGLE;
        });
        ovalBrush.setOnAction(v -> {
            drawingType = DrawingOperation.DrawingType.BRUSH;
            currentShape = DrawingOperation.Shape.OVAL;
        });
        ovalShape.setOnAction(v -> {
            drawingType = DrawingOperation.DrawingType.SHAPE;
            currentShape = DrawingOperation.Shape.OVAL;
        });
        roundedRectangleShape.setOnAction(v -> {
            drawingType = DrawingOperation.DrawingType.SHAPE;
            currentShape = DrawingOperation.Shape.ROUNDED_RECTANGLE;
        });
        roundedRectBrush.setOnAction(v -> {
            drawingType = DrawingOperation.DrawingType.BRUSH;
            currentShape = DrawingOperation.Shape.ROUNDED_RECTANGLE;
        });
    }

    private void mousePressed(MouseEvent mouseEvent) {
        if (drawingType == DrawingOperation.DrawingType.SHAPE) {
            currentFigure += 1;
        }
        if (drawingType == DrawingOperation.DrawingType.TEXT) {
            this.startX = mouseEvent.getX();
            this.startY = mouseEvent.getY();
            drawText();
        } else if (drawingType == DrawingOperation.DrawingType.SHAPE) {
            this.startX = mouseEvent.getX();
            this.startY = mouseEvent.getY();
        } else {
            this.endX = mouseEvent.getX();
            this.endY = mouseEvent.getY();
        }
    }

    private void mouseReleased(MouseEvent mouseEvent) {

    }

    private void mouseDragged(MouseEvent mouseEvent) {
        if (drawingType == DrawingOperation.DrawingType.SHAPE) {
            this.endX = mouseEvent.getX();
            this.endY = mouseEvent.getY();
            historySet.add(currentFigure);
            drawFigure();
        } else if (drawingType == DrawingOperation.DrawingType.BRUSH || drawingType == DrawingOperation.DrawingType.ERASER) {
            this.startX = endX;
            this.startY = endY;
            this.endX = mouseEvent.getX();
            this.endY = mouseEvent.getY();
            historySet.add(currentFigure);
            currentFigure += 1;
            drawFigure();
        }
    }

    private void drawFigure() {
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        drawingOperations.put(currentFigure, new DrawingOperation(graphicsContext,
                drawingType,
                startX, startY, endX, endY,
                Paint.valueOf(colorPicker.getValue().toString()),
                (int) sizeSlider.getValue(),
                currentShape));
        for (HashMap.Entry<Integer, DrawingOperation> entry : drawingOperations.entrySet()) {
            entry.getValue().draw();
        }
    }

    private void drawText() {
        currentFigure += 1;
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        drawingOperations.put(currentFigure, new DrawingOperation(graphicsContext,
                drawingType,
                startX, startY, startX, startY,
                Paint.valueOf(colorPicker.getValue().toString()),
                (int) sizeSlider.getValue(),
                currentShape,
                textInput.getText()));
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
        } else {
            showMessage("Select a valid image", true);
        }

    }

    @FXML
    void clearAll() {
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        drawingOperations.clear();
    }

    @FXML
    void undoStep(ActionEvent event) {
        if (drawingOperations.isEmpty()) return;
        if (drawingOperations.size() == 1) {
            // TODO: 4/17/2019 DISABLE THE UNDO BUUTTON
        }
        drawingOperations.remove(currentFigure);
        currentFigure -= 1;
        System.err.println(drawingOperations);
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        for (HashMap.Entry<Integer, DrawingOperation> entry : drawingOperations.entrySet()) {
            entry.getValue().draw();
        }
    }

    private String getImageSize(final File file) {
        Image image = new Image(file.toURI().toString());
        return image.getWidth() + " " + image.getHeight();
    }

    @FXML
    private void rgb2hsv() {
        Image rgbImage = imageView.getImage();
        int width = (int) rgbImage.getWidth();
        int height = (int) rgbImage.getHeight();
        WritableImage hsvkImage = new WritableImage(width, height);
        PixelReader rgbPixels = rgbImage.getPixelReader();
        PixelWriter hsvkPixel = hsvkImage.getPixelWriter();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color = rgbPixels.getColor(i, j);
                double r = color.getRed(), b = color.getBlue(), g = color.getGreen();
                double  H, V, S;

                double cmax = (r > g) ? r : g;
                if (b > cmax) cmax = b;
                double cmin = (r < g) ? r : g;
                if (b < cmin) cmin = b;

                V = cmax;
                if (cmax != 0)
                    S = (double) (cmax - cmin) / cmax;
                else
                    S = 0;

                if (S == 0) {
                    H = 0;
                } else {
                    double redc = (cmax - r) / (cmax - cmin);
                    double greenc = (cmax - g) / (cmax - cmin);
                    double bluec = (cmax - b) / (cmax - cmin);
                    if (r == cmax)
                        H = bluec - greenc;
                    else if (g == cmax)
                        H = 2.0 + redc - bluec;
                    else
                        H = 4.0 + greenc - redc;
                    H = H / 6.0;
                    if (H < 0)
                        H = H + 1.0;
                }
                hsvkPixel.setColor(i, j, Color.hsb(H, S, V));
            }
        }
        imageView.setImage(hsvkImage);
    }

    @FXML
    private void argb2Cmyk() {
        Image rgbImage = imageView.getImage();
        int width = (int) rgbImage.getWidth();
        int height = (int) rgbImage.getHeight();
        WritableImage cmykImage = new WritableImage(width, height);
        PixelReader rgbPixels = rgbImage.getPixelReader();
        PixelWriter cmykPixel = cmykImage.getPixelWriter();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color = rgbPixels.getColor(i, j);
                double red = color.getRed(), blue = color.getBlue(), green = color.getGreen();
                double alpha = color.getOpacity();
                double cyan, magenta, yellow, key;
                double cyanPixel, magentaPixel, yellowPixel, keyPixel;
                double cyanVis, magentaVis, yellowVis, keyVis;
                cyan = 1. - red / 255.;
                magenta = 1. - green / 255.;
                yellow = 1. - blue / 255.;
                key = Math.min(cyan, Math.min(magenta, yellow));
                if (key >= 1.) {
                    cyanPixel = magentaPixel = yellowPixel = 0.;
                } else {
                    final double s = 1. - key;
                    cyanPixel = (cyan - key) / s;
                    magentaPixel = (magenta - key) / s;
                    yellowPixel = (yellow - key) / s;

                }
                keyPixel = key;
                cyanVis = 255 - Math.round(cyanPixel * 255);
                magentaVis = 255 - Math.round(magentaPixel * 255);
                yellowVis = 255 - Math.round(yellowPixel * 255);
                keyVis = 255 - Math.round(keyPixel * 255);
                cmykPixel.setColor(i, j, Color.rgb((int) cyanVis, (int) magentaVis, (int) yellowVis, (int) keyVis));
            }
        }
        imageView.setImage(cmykImage);
    }

    @FXML
    private void hsv2rgb(){
        Image hsvImage = imageView.getImage();
        int width = (int) hsvImage.getWidth();
        int height = (int) hsvImage.getHeight();
        WritableImage rgbImage = new WritableImage(width, height);
        PixelReader hsvPixels = hsvImage.getPixelReader();
        PixelWriter rgbPixel = rgbImage.getPixelWriter();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color = hsvPixels.getColor(i, j);
                double H=color.getHue(), S=color.getSaturation(), V=color.getBrightness();
                double normalizedHue = ((H % 360) + 360) % 360;
                H = normalizedHue/360;

                double r = 0, g = 0, b = 0;
                if (S == 0) {
                    r = g = b = V;
                } else {
                    double h = (H - Math.floor(H)) * 6.0;
                    double f = h - java.lang.Math.floor(h);
                    double p = V * (1.0 - S);
                    double q = V * (1.0 - S * f);
                    double t = V * (1.0 - (S * (1.0 - f)));
                    switch ((int) h) {
                        case 0:
                            r = V;
                            g = t;
                            b = p;
                            break;
                        case 1:
                            r = q;
                            g = V;
                            b = p;
                            break;
                        case 2:
                            r = p;
                            g = V;
                            b = t;
                            break;
                        case 3:
                            r = p;
                            g = q;
                            b = V;
                            break;
                        case 4:
                            r = t;
                            g = p;
                            b = V;
                            break;
                        case 5:
                            r = V;
                            g = p;
                            b = q;
                            break;
                    }
                }
                rgbPixel.setColor(i,j,Color.color(r,g,b));
            }
        }
        imageView.setImage(rgbImage);
    }
}
