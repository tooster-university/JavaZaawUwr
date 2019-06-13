package me.tooster.datamodels;

import javafx.beans.property.SimpleStringProperty;

public class Product {
    SimpleStringProperty code;
    SimpleStringProperty description;

    public Product(String code, String description) {
        this.code = new SimpleStringProperty(code);
        this.description = new SimpleStringProperty(description);
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

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
