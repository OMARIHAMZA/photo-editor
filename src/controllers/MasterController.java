package controllers;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXToolbar;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.lang.model.element.Element;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class MasterController implements Initializable {

    private Pane rootView;
    private JFXSnackbar snackbar;


    void showMessage(String message, boolean isError) {
        if (snackbar == null) snackbar = new JFXSnackbar(rootView);
        Label label = new Label(message);
        label.setBackground(new Background(new BackgroundFill(Paint.valueOf(!isError ? "#43a047" : "#b71c1c"), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(800);
        label.setPadding(new Insets(8));
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(label));
    }

    void setRootView(Pane rootView) {
        this.rootView = rootView;
    }

    void showWindow(String path, String title) {
        Parent root;
        Stage stage;
        try {
            root = FXMLLoader.load(getClass().getResource(path));
            stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showMessage(e.getMessage(), true);
        }
    }

    File chooseFile() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("Images", "*.png", "*jpg", "*jpeg");

        fileChooser.getExtensionFilters().add(extensionFilter);

        return fileChooser.showOpenDialog(rootView.getScene().getWindow());
    }

    void hideViews(Node... nodes) {
        for (Node node : nodes) {
            node.setVisible(false);
        }
    }

    private WritableImage pixelScaleSnapshot(Pane pane, double pixelScale) {
        WritableImage writableImage = new WritableImage((int) Math.rint(pixelScale * pane.getWidth()), (int) Math.rint(pixelScale * pane.getHeight()));
        SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(Transform.scale(pixelScale, pixelScale));
        return pane.snapshot(spa, writableImage);
    }

    void exportImage(Pane pane) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(rootView.getScene().getWindow());

        if (file != null) {
            try {
                WritableImage writableImage = pixelScaleSnapshot(pane, 2);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
                showMessage("Image Saved", false);
            } catch (IOException ex) {
                Logger.getLogger(DrawingWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
