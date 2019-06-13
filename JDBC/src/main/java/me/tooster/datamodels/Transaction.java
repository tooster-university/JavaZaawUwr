package me.tooster.datamodels;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Timestamp;

public class Transaction {
    SimpleStringProperty timestamp;
    SimpleStringProperty code;
    SimpleDoubleProperty amount;
    SimpleStringProperty price;
    SimpleStringProperty cost;

    public Transaction(Timestamp timestamp, String code, double amount, double price, boolean inKg) {
        this.timestamp = new SimpleStringProperty(timestamp.toString());
        this.code = new SimpleStringProperty(code);
        this.amount = new SimpleDoubleProperty(amount);
        String format = "$%.2f" + (inKg ? "/kg" : "/unit");
        this.price = new SimpleStringProperty(String.format(format, Math.abs(price)));
        this.cost = new SimpleStringProperty(String.format("$%.2f", amount * price));
    }

    public String getTimestamp() {
        return timestamp.get();
    }

    public SimpleStringProperty timestampProperty() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp.set(timestamp);
    }

    public String getCode() {
        return code.get();
    }

    public SimpleStringProperty codeProperty() {
        return code;
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public double getAmount() {
        return amount.get();
    }

    public SimpleDoubleProperty amountProperty() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public String getPrice() {
        return price.get();
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public void setPrice(String price) {
        this.price.set(price);
    }

    public String getCost() {
        return cost.get();
    }

    public SimpleStringProperty costProperty() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost.set(cost);
    }
}
