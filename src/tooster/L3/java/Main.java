package tooster.L3.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    private static ResourceBundle bundle;

    public static ResourceBundle getBundle() { return bundle; }

    @Override
    public void start(Stage primateStage) throws Exception {
        bundle = ResourceBundle.getBundle("tooster.L3.resources.bundles.translations");
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/tooster/L3/resources/fxml/Excel.fxml"), bundle);

        Scene scene = new Scene(loader.load());
        primateStage.setScene(scene);
        primateStage.setTitle("tittle");
        primateStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
