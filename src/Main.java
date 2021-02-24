import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {
    Volume v;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("CThead Viewer");
        try {
            v = new Volume("CThead");
        } catch (IOException e) {
            System.out.println("File not Found");
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getClassLoader().getResource("AssetController.fxml").openStream());
            AssetController mainControl = loader.getController();
            mainControl.setVolume(v);
            Scene scene = new Scene(root, WIDTH, HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        launch(args);
    }
}