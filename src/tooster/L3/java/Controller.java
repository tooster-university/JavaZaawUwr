package tooster.L3.java;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller {

    ObservableList<Expense> data = FXCollections.observableArrayList(new Expense(true, new Date(), new BigDecimal(17), "desc"));

    // hold amounts
    private BigDecimal recVal = new BigDecimal(0), constVal = new BigDecimal(0);

    @FXML
    private TableView<Expense> table;

    @FXML
    private TableColumn<Expense, String> typeColumn;

    @FXML
    private TableColumn<Expense, String> amountColumn;

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
            return new SimpleObjectProperty<String>(isRecurrent ? type_recurring_string : type_constant_string);
        });

        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(type_constant_string, type_recurring_string));
        typeColumn.setOnEditCommit((TableColumn.CellEditEvent<Expense, String> t) -> {
            Expense expense = t.getTableView().getItems().get(t.getTablePosition().getRow());
            String oldType = t.getOldValue();
            String newType = t.getNewValue();

            if (oldType.equals(type_recurring_string)) setRecurring(recVal.subtract(expense.getValue()));
            else setConstant(constVal.subtract(expense.getValue()));

            if (newType.equals(type_recurring_string)) setRecurring(recVal.add(expense.getValue()));
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
                    return null;
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
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setOnEditCommit(t -> {
            Expense expense = t.getTableView().getItems().get(t.getTablePosition().getRow());
            String newValueStr = t.getNewValue();

            if (newValueStr.matches("\\d+([.]\\d{2})?")) {  // wrong format
                if (expense.isRecurrent()) setRecurring(expense.getValue());
                else setConstant(expense.getValue());
                expense.setValue(new BigDecimal(newValueStr));
            }
        });

        table.setItems(data);
    }


    @FXML
    void createRow() throws Exception {
        data.add(new Expense(false, new Date(), new BigDecimal(0), ""));
    }

    @FXML
    void removeActive() {
        table.getItems().remove(table.getSelectionModel().getSelectedItem());
    }

    void setRecurring(BigDecimal val) {
        recVal = val;
        rExpensesValueLabel.setText(recVal.toString() + Currency.getInstance(Locale.getDefault()).getSymbol());
    }

    void setConstant(BigDecimal val) {
        constVal = val;
        cExpensesValueLabel.setText(constVal.toString() + Currency.getInstance(Locale.getDefault()).getSymbol());
    }

}
