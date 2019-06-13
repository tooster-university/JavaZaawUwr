package me.tooster;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.tooster.datamodels.Product;
import me.tooster.datamodels.StorageProduct;
import me.tooster.datamodels.Transaction;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    static Controller instance;

    static Controller getInstance() { return instance; }

    static void setInstance(Controller c) {instance = c;}

    ObservableList<Product> productData = FXCollections.observableArrayList();
    ObservableList<StorageProduct> storageData = FXCollections.observableArrayList();
    ObservableList<Transaction> transactionsData = FXCollections.observableArrayList();

    @FXML
    private TableView<Product> catalogTable;

    @FXML private TableColumn<Product, String> catalogProductColumn;
    @FXML private TableColumn<Product, String> catalogDescriptionColumn;

    @FXML private MenuItem addProductOption;
    @FXML private MenuItem removeProductOption;


    @FXML
    private TableView<StorageProduct> storageTable;

    @FXML private TableColumn<StorageProduct, String> storageProductColumn;
    @FXML private TableColumn<StorageProduct, BigInteger> storageAmountColumn;
    @FXML private TableColumn<StorageProduct, String> storageBuyColumn;
    @FXML private TableColumn<StorageProduct, String> storageSellColumn;

    @FXML
    private TableView<Transaction> transactionsTable;

    @FXML private TableColumn<StorageProduct, Timestamp> transactionsIDColumn;
    @FXML private TableColumn<StorageProduct, String> transactionsProductColumn;
    @FXML private TableColumn<StorageProduct, BigInteger> transactionsAmountColumn;
    @FXML private TableColumn<StorageProduct, String> transactionsPriceColumn;
    @FXML private TableColumn<StorageProduct, String> transactionsCostColumn;

    @FXML private MenuItem addTransactionOption;
    @FXML private MenuItem withdrawTransactionOption;

    @FXML private DatePicker startingDateInput;
    @FXML private DatePicker endingDateInput;

    @FXML private TextField profitField;
    @FXML private TextField expensesField;
    @FXML private TextField totalField;


    void fetchProducts() {
        Connection connection = ShopApp.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT prod_id, description FROM products;");
            ResultSet rs = statement.executeQuery();

            productData.removeAll(productData);
            while (rs.next())
                productData.add(new Product(
                        rs.getString(1),
                        rs.getString(2)
                ));
            catalogTable.getItems().setAll(productData);
        } catch (SQLException e) {
            System.err.println("SQL error while fetching products.");
            e.printStackTrace();
        }
    }

    void fetchStorage() {
        Connection connection = ShopApp.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT prod_id, amount, last_sell_cost_per_measure, last_buy_cost_per_measure, in_kg " +
                            "FROM storage NATURAL JOIN products;");
            ResultSet rs = statement.executeQuery();

            storageData.removeAll(storageData);
            while (rs.next())
                storageData.add(new StorageProduct(
                        rs.getString(1),
                        rs.getDouble(2),
                        rs.getDouble(3),
                        rs.getDouble(4),
                        rs.getBoolean(5)
                ));

            storageTable.getItems().setAll(storageData);
        } catch (SQLException e) {
            System.err.println("SQL error while fetching storage.");
            e.printStackTrace();
        }

    }

    void fetchTransactions() {
        Connection connection = ShopApp.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT transaction_id, prod_id, amount, price_per_measure, in_kg " +
                            "FROM transactions NATURAL JOIN products p;");
            ResultSet rs = statement.executeQuery();

            transactionsData.removeAll(transactionsData);
            while (rs.next())
                transactionsData.add(new Transaction(
                        rs.getTimestamp(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getDouble(4),
                        rs.getBoolean(5)));

            transactionsTable.setItems(transactionsData);

            calculateBalance();
        } catch (SQLException e) {
            System.err.println("SQL error while fetching transactions.");
            e.printStackTrace();
        }

    }


    private void calculateBalance() {
        Connection connection = ShopApp.getConnection();
        PreparedStatement statement;
        LocalDate begin = startingDateInput.getValue();
        LocalDate end = endingDateInput.getValue();
        String gtCondition = "", lsCondition = ""; // additional selection conditions for date
        if (begin != null) gtCondition = " AND transaction_id >= '" + Timestamp.valueOf(begin.atStartOfDay()) + "'";
        if (end != null)
            lsCondition = " AND transaction_id < '" + Timestamp.valueOf(end.atStartOfDay().plusDays(1)) + "'";

        try {
            statement = connection.prepareStatement(
                    "SELECT SUM(CASE WHEN price_per_measure > 0::money THEN amount*price_per_measure ELSE 0::money END)::numeric AS profit, " +
                            "   SUM(CASE WHEN price_per_measure < 0::money THEN amount*price_per_measure ELSE 0::money END)::numeric AS expenses " +
                            "FROM transactions WHERE TRUE " + gtCondition + lsCondition + ";");

            ResultSet rs = statement.executeQuery();
            double profit = 0.00, expenses = 0.00;
            if (rs.next()) {
                profit = rs.getDouble("profit");
                expenses = -rs.getDouble("expenses");
            }
            profitField.setText(String.format("$%.2f", Math.abs(profit)));
            expensesField.setText(String.format("$%.2f", Math.abs(expenses)));
            totalField.setText(String.format("$%.2f", profit - expenses));
            if (profit - expenses >= 0) totalField.setStyle("-fx-text-inner-color: green");
            else totalField.setStyle("-fx-text-inner-color: red");
        } catch (SQLException e) {
            System.err.println("SQL error while calculating balance.");
            e.printStackTrace();
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        catalogProductColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        catalogDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        addProductOption.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddProductDialog.fxml"));
                AnchorPane page = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Add product");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(ShopApp.getPrimaryStage());
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                AddProductController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setConnection(ShopApp.getConnection());
                dialogStage.setResizable(false);


                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        removeProductOption.setOnAction(event -> {
            try {
                Product p = catalogTable.getSelectionModel().getSelectedItem();
                if(p != null){
                    Connection connection = ShopApp.getConnection();
                    PreparedStatement statement = connection.prepareStatement(
                            "DELETE FROM products WHERE prod_id LIKE '"+p.getCode()+"';"
                    );
                    statement.executeUpdate();
                    fetchProducts();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        addTransactionOption.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddTransactionDialog.fxml"));
                AnchorPane page = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Add transaction");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(ShopApp.getPrimaryStage());
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                AddTransactionController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setConnection(ShopApp.getConnection());
                dialogStage.setResizable(false);

                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        withdrawTransactionOption.setOnAction(event -> {
            try {
                Connection connection = ShopApp.getConnection();
                CallableStatement statement = connection.prepareCall("{ call withdraw_last_transaction()}");
                statement.execute();

                fetchTransactions();
                fetchStorage();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        storageProductColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        storageAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        storageSellColumn.setCellValueFactory(new PropertyValueFactory<>("lastSellPrice"));
        storageBuyColumn.setCellValueFactory(new PropertyValueFactory<>("lastBuyPrice"));

        transactionsIDColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        transactionsProductColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        transactionsAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        transactionsPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        transactionsCostColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));

        catalogTable.getItems().setAll(productData);
        storageTable.getItems().setAll(storageData);
        transactionsTable.getItems().setAll(transactionsData);

        startingDateInput.valueProperty().addListener((observable, oldValue, newValue) -> fetchTransactions());
        endingDateInput.valueProperty().addListener((observable, oldValue, newValue) -> fetchTransactions());
    }

    void fetchAll() {
        fetchProducts();
        fetchStorage();
        fetchTransactions();
    }
}
