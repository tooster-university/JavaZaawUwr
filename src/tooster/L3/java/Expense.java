package tooster.L3.java;

import java.math.BigDecimal;
import java.util.Date;

public class Expense {
    private boolean isRecurrent;
    private Date date;
    private BigDecimal value;
    private String description;

    public Expense(boolean isRecurrent, Date date, BigDecimal value, String description) {
        this.isRecurrent = isRecurrent;
        this.date = date;
        this.value = value;
        this.description = description;
    }

    public boolean isRecurrent() { return isRecurrent; }

    public void setRecurrent(boolean recurrent) { isRecurrent = recurrent; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public BigDecimal getValue() { return value; }

    public void setValue(BigDecimal value) { this.value = value; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}
