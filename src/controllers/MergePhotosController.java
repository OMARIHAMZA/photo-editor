package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MergePhotosController extends MasterController {

    @FXML
    private VBox rootView;

    @FXML
    private ImageView firstImageView;

    @FXML
    private ImageView secondImageView;

    @FXML
    private JFXButton selectFirstImageButton;

    @FXML
    private JFXButton selectImageButton;

    @FXML
    private HBox toolsBox;

    @FXML
    private JFXSlider firstImageOpacitySlider;

    @FXML
    private JFXSlider secondImageOpacitySlider;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXButton saveButton;


    private Image firstImage, secondImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setRootView(rootView);
        assignActions();
        toolsBox.setVisible(false);
    }

    private void assignActions() {
        selectFirstImageButton.setOnAction(v -> displayFileChooser(selectFirstImageButton, true));
        selectImageButton.setOnAction(v -> displayFileChooser(selectImageButton, false));
        firstImageOpacitySlider.valueProperty().addListener((observable, oldValue, newValue) -> firstImageView.setOpacity(((double) newValue / 100)));
        secondImageOpacitySlider.valueProperty().addListener((observable, oldValue, newValue) -> secondImageView.setOpacity(((double) newValue / 100)));
    }

    private void displayFileChooser(JFXButton button, boolean isFirstImage){
        File file = chooseFile();
        if (file != null) {
            if (isFirstImage) {
                firstImage = new Image(file.toURI().toString());
            } else {
                secondImage = new Image(file.toURI().toString());
            }
            button.setText(isFirstImage ? "FIRST IMAGE SELECTED" : "SECOND IMAGE SELECTED");
            button.setDisable(true);
            if (selectFirstImageButton.isDisabled() && selectImageButton.isDisabled()){
                hideViews(selectFirstImageButton, selectImageButton);
                firstImageView.setImage(firstImage);
                secondImageView.setImage(secondImage);
                toolsBox.setVisible(true);
            }
        } else {
            showMessage("Select a valid image", true);
        }
    }

    @FXML
    void saveImage(ActionEvent event) {
        super.exportImage(stackPane);
    }

}
