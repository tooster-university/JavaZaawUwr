package tooster.L3.java;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class Controller {

    private ObservableList<Expense> data = FXCollections.observableArrayList();

    // hold amounts
    private BigDecimal recVal = new BigDecimal(0), constVal = new BigDecimal(0);

    @FXML
    private TableView<Expense> table;

    @FXML
    private TableColumn<Expense, String> typeColumn;

    @FXML
    private TableColumn<Expense, BigDecimal> amountColumn;

    @FXML
    private TableColumn<Expense, Date> dateColumn;

    @FXML
    private TableColumn<Expense, String> descColumn;


    @FXML
    private Label rExpensesLabel;

    @FXML
    private Label rExpensesValueLabel;

    @FXML
    private Label cExpensesLabel;

    @FXML
    private Label cExpensesValueLabel;

    @FXML
    private Button addBtn;

    @FXML
    private Button subBtn;

    @FXML
    void initialize() {
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'Excel.fxml'.";
        assert typeColumn != null : "fx:id=\"typeColumn\" was not injected: check your FXML file 'Excel.fxml'.";
        assert amountColumn != null : "fx:id=\"amountColumn\" was not injected: check your FXML file 'Excel.fxml'.";
        assert descColumn != null : "fx:id=\"descColumn\" was not injected: check your FXML file 'Excel.fxml'.";
        assert rExpensesLabel != null : "fx:id=\"rExpensesLabel\" was not injected: check your FXML file 'Excel.fxml'.";
        assert rExpensesValueLabel != null : "fx:id=\"rExpensesValueLabel\" was not injected: check your FXML file 'Excel.fxml'.";
        assert cExpensesLabel != null : "fx:id=\"cExpensesLabel\" was not injected: check your FXML file 'Excel.fxml'.";
        assert cExpensesValueLabel != null : "fx:id=\"cExpensesValueLabel\" was not injected: check your FXML file 'Excel.fxml'.";
        assert addBtn != null : "fx:id=\"addBtn\" was not injected: check your FXML file 'Excel.fxml'.";
        assert subBtn != null : "fx:id=\"subBtn\" was not injected: check your FXML file 'Excel.fxml'.";

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        ResourceBundle bundle = Main.getBundle();
        String type_recurring_string = bundle.getString("type_recurring");
        String type_constant_string = bundle.getString("type_constant");


        // type column
        typeColumn.setCellValueFactory(param -> {
            Expense expense = param.getValue();

            boolean isRecurrent = expense.isRecurrent();
            return new SimpleObjectProperty<>(isRecurrent ? type_recurring_string : type_constant_string);
        });

        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(type_constant_string, type_recurring_string));
        typeColumn.setOnEditCommit((TableColumn.CellEditEvent<Expense, String> t) -> {
            Expense expense = t.getTableView().getItems().get(t.getTablePosition().getRow());


            if (expense.isRecurrent()) setRecurring(recVal.subtract(expense.getValue()));
            else setConstant(constVal.subtract(expense.getValue()));

            expense.setRecurrent(t.getNewValue().equals(type_recurring_string));
            if (expense.isRecurrent()) setRecurring(recVal.add(expense.getValue()));
            else setConstant(constVal.add(expense.getValue()));
        });


        // date column
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Date>() {
            @Override
            public String toString(Date object) {
                return DateFormat.getDateInstance(DateFormat.DEFAULT).format(object);
            }

            @Override
            public Date fromString(String string) {
                try {
                    return DateFormat.getDateInstance(DateFormat.DEFAULT).parse(string);
                } catch (ParseException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, bundle.getString("invalid_date"));
                    alert.setHeaderText(null);
                    alert.showAndWait();
                    return new Date(); // on wrong date input
                }

            }
        }));

        dateColumn.setOnEditCommit(t -> {
            Expense expense = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Date newDate = t.getNewValue();
            if (newDate != null)
                expense.setDate(newDate);
        });


        // amount column
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<BigDecimal>() {
            @Override
            public String toString(BigDecimal object) {return object.toString();}

            @Override
            public BigDecimal fromString(String string) {
                if (string.matches("\\d+([.]\\d{2})?"))
                    return new BigDecimal(string);
                else return new BigDecimal(0);
            }
        }));

        amountColumn.setOnEditCommit(t -> {
            Expense expense = t.getTableView().getItems().get(t.getTablePosition().getRow());

            if (expense.isRecurrent()) setRecurring(recVal.subtract(expense.getValue()));
            else setConstant(constVal.subtract(expense.getValue()));

            expense.setValue(t.getNewValue());
            if (expense.isRecurrent()) setRecurring(recVal.add(expense.getValue()));
            else setConstant(constVal.add(expense.getValue()));
        });


        // description column
        descColumn.setCellValueFactory(new PropertyValueFactory<>("desc"));
        descColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        descColumn.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setDescription(t.getNewValue()));

        table.setItems(data);

        table.setPlaceholder(new Label(bundle.getString("table_empty")));
    }


    @FXML
    void createRow() {
        data.add(new Expense(false, new Date(), new BigDecimal(0), ""));
    }

    @FXML
    void removeActive() {
        Expense expense = table.getSelectionModel().getSelectedItem();
        if(expense == null) return;

        if(expense.isRecurrent()) setRecurring(recVal.subtract(expense.getValue()));
        else setConstant(constVal.subtract(expense.getValue()));
        table.getItems().remove(expense);
    }

    private void setRecurring(BigDecimal val) {
        recVal = val;
        rExpensesValueLabel.setText(recVal.toString() + Currency.getInstance(Locale.getDefault()).getSymbol());
    }

    private void setConstant(BigDecimal val) {
        constVal = val;
        cExpensesValueLabel.setText(constVal.toString() + Currency.getInstance(Locale.getDefault()).getSymbol());
    }

}
