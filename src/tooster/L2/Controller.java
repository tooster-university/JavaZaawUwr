package tooster.L2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;


public class Controller {

    private static Controller instance;

    public static Controller getInstance() {
        return instance;
    }

    public static void setInstance(Controller instance) {
        Controller.instance = instance;
    }

    @FXML
    private GridPane grid;

    @FXML
    private Button restart;

    @FXML
    public void initialize() {}

    @FXML
    void restartSim(ActionEvent event) {
        Model model = Model.getInstance();
        model.reset();
    }

    GridPane getGrid() { return grid; }
}

