package me.tooster.datamodels;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class StorageProduct {
    SimpleStringProperty code;
    SimpleDoubleProperty amount;
    SimpleStringProperty lastSellPrice;
    SimpleStringProperty lastBuyPrice;

    public StorageProduct(String code, double amount, double lastSellPrice, double lastBuyPrice, boolean inKg) {
        this.code = new SimpleStringProperty(code);
        this.amount = new SimpleDoubleProperty(amount);
        String format = "$%.2f" + (inKg ? "/kg" : "/unit");
        this.lastSellPrice = new SimpleStringProperty(String.format(format, lastSellPrice));
        this.lastBuyPrice = new SimpleStringProperty(String.format(format, lastBuyPrice));
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

    public String getLastSellPrice() {
        return lastSellPrice.get();
    }

    public SimpleStringProperty lastSellPriceProperty() {
        return lastSellPrice;
    }

    public void setLastSellPrice(String lastSellPrice) {
        this.lastSellPrice.set(lastSellPrice);
    }

    public String getLastBuyPrice() {
        return lastBuyPrice.get();
    }

    public SimpleStringProperty lastBuyPriceProperty() {
        return lastBuyPrice;
    }

    public void setLastBuyPrice(String lastBuyPrice) {
        this.lastBuyPrice.set(lastBuyPrice);
    }
}
