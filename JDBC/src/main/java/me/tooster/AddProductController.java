package me.tooster;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddProductController implements Initializable {


    @FXML private TextField IDField;
    @FXML private CheckBox kilogramsCheckbox;
    @FXML private TextArea descriptionField;
    @FXML private Button cancelButton;
    @FXML private Button okButton;

    private Stage dialogStage;
    private Connection connection;

    void setConnection(Connection connection) { this.connection = connection; }

    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }


    @FXML
    private void handleOK() {
        if (IDField.getText().length() > 0) {
            try {
                PreparedStatement statement =
                        connection.prepareStatement("INSERT INTO products VALUES " +
                                "('" + IDField.getText() + "'," +
                                "'" + descriptionField.getText() + "'," +
                                "" + (kilogramsCheckbox.isSelected() ? "TRUE" : "FALSE") + ");");

                statement.executeUpdate();
                dialogStage.close();

                Controller.getInstance().fetchProducts();
            } catch (SQLException e) {
                System.err.println("Error while adding new product");
                System.err.println(e.toString());
            }

        }

    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}
