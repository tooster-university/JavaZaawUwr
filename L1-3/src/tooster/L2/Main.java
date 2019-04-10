package tooster.L2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sim.fxml"));
        setUserAgentStylesheet(STYLESHEET_MODENA);
        Scene scene = new Scene(loader.load());
        Controller.setInstance(loader.getController());

        primaryStage.setScene(scene);
        Controller.getInstance().restartSim(null);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
