import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class containing the UI
 */

public class AssetController implements Initializable {
    int top_width;
    int top_height;
    int front_width;
    int front_height;
    int side_width;
    int side_height;
    WritableImage top_image;
    WritableImage side_image;
    WritableImage front_image;
    ImageView topImgView;
    ImageView sideImgView;
    ImageView frontImgView;
    private Volume v;
    @FXML
    private Pane topView;
    @FXML
    private Pane sideView;
    @FXML
    private Pane frontView;
    @FXML
    private Slider topDownSlider;
    @FXML
    private Slider sideSlider;
    @FXML
    private Slider frontSlider;
    @FXML
    private Button sliceButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        top_width = Volume.CT_x_axis;
        top_height = Volume.CT_y_axis;
        front_width = Volume.CT_x_axis;
        front_height = Volume.CT_z_axis;
        side_width = Volume.CT_y_axis;
        side_height = Volume.CT_z_axis;
        top_image = new WritableImage(top_width, top_height);
        side_image = new WritableImage(side_width, side_height);
        front_image = new WritableImage(front_width, front_height);
        topImgView = new ImageView(top_image);
        sideImgView = new ImageView(side_image);
        frontImgView = new ImageView(front_image);
        displaySliders();
        displaySlices(topImgView, sideImgView, frontImgView);
    }

    public void setVolume(Volume v) {
        this.v = v;
    }

    public void displaySlices(ImageView top, ImageView side, ImageView front) {
        top.setFitHeight(256);
        side.setFitHeight(256);
        front.setFitHeight(256);
        topView.getChildren().add(top);
        sideView.getChildren().add(side);
        frontView.getChildren().add(front);

    }

    public void redrawSlices() {

    }

    public void displaySliders() {
        topDownSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        v.slices(top_image, Volume.Axis.Z, newValue.intValue());
                    }
                });

        frontSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        v.slices(front_image, Volume.Axis.X, newValue.intValue());
                    }
                });

        sideSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        v.slices(side_image, Volume.Axis.Y, newValue.intValue());

                    }
                });
    }

}
