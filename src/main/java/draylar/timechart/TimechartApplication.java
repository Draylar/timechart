package draylar.timechart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TimechartApplication extends Application {

    public static Stage STAGE;

    @Override
    public void start(Stage stage) throws Exception {
        STAGE = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/main.fxml"));
        Scene scene = new Scene(root, 800, 550);
        scene.getStylesheets().add("ui/style.css");
        stage.setTitle("timechart");
        stage.setScene(scene);
        stage.show();
        Platform.setImplicitExit(false);

        stage.setOnCloseRequest(event -> {
            stage.hide();
        });
    }
}
