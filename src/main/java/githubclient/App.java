package githubclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/githubclient/Main.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("GitHub Viewer");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(750);
        stage.show();
    }

    public static void main(String[] args) {
    launch(args);
    }
}