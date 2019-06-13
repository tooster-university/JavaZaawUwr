package me.tooster;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddTransactionController implements Initializable {

    @FXML private TextField idField;
    @FXML private TextField amountField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> transactionTypeCombo;
    @FXML private Button cancelButton;
    @FXML private Button okButton;

    private Stage dialogStage;
    private Connection connection;

    void setConnection(Connection connection) { this.connection = connection; }

    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }


    @FXML
    void handleOK(ActionEvent event) {
        if (idField.getText().length() > 0 &&
                amountField.getText().length() > 0 &&
                priceField.getText().length() > 0)
            try {
                String id = idField.getText();
                double price = Double.parseDouble(priceField.getText());
                double amount = Double.parseDouble(amountField.getText());
                price = (transactionTypeCombo.getValue() == "BUY" ? -price : price);

                PreparedStatement statement =
                        connection.prepareStatement("" +
                                "INSERT INTO transactions(prod_id, amount, price_per_measure)\n" +
                                "VALUES  ('"+id+"', "+amount+", "+price+");");

                statement.executeUpdate();
                dialogStage.close();

                Controller.getInstance().fetchStorage();
                Controller.getInstance().fetchTransactions();
            } catch (SQLException e) {
                System.err.println("Error while adding new product");
                System.err.println(e.toString());
            } catch (NumberFormatException ignored) {
            }


    }


    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transactionTypeCombo.getItems().addAll("SELL", "BUY");
        transactionTypeCombo.setValue("SELL");
    }


}
