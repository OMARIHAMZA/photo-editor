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
import javafx.scene.paint.Paint;

abstract class MasterController implements Initializable {

    private Pane rootView;
    private JFXSnackbar snackbar;


    void setupToolbar(JFXToolbar mToolbar) {
        if (mToolbar == null) return;
        mToolbar.setLeftItems(new Label("Left"));
        mToolbar.setRightItems(new Label("Right"));
    }

    void showSnackBar(String message) {
        if (snackbar == null) snackbar = new JFXSnackbar(rootView);
        Label label = new Label(message);
        label.setBackground(new Background(new BackgroundFill(Paint.valueOf("#ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setPrefWidth(600);
        label.setPadding(new Insets(8));
        label.setAlignment(Pos.CENTER);
        snackbar.enqueue(new JFXSnackbar.SnackbarEvent(label));
    }

    void setRootView(Pane rootView) {
        this.rootView = rootView;
    }
}
