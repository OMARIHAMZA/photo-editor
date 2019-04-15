package controllers;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXToolbar;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

abstract class MasterController implements Initializable {

    private Pane rootView;
    private JFXSnackbar snackbar;


    void showMessage(String message, boolean isError) {
        if (snackbar == null) snackbar = new JFXSnackbar(rootView);
        Label label = new Label(message);
        label.setBackground(new Background(new BackgroundFill(Paint.valueOf(!isError ? "#43a047" : "#b71c1c"), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(600);
        label.setPadding(new Insets(8));
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(label));
    }

    void setRootView(Pane rootView) {
        this.rootView = rootView;
    }
}
