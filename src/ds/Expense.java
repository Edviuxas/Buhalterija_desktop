package ds;

import java.io.Serializable;
import java.time.LocalDate;

public class Expense implements Serializable {
    String belongsTo;
    String name;
    String description;
    double amount;
    LocalDate dateCreated;

    public Expense(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }

    public Expense(String name, String description, double amount) {
        this.name = name;
        this.description = description;
        this.amount = amount;
    }

    public Expense(String belongsTo, String name, String description, double amount) {
        this.belongsTo = belongsTo;
        this.name = name;
        this.description = description;
        this.amount = amount;
    }

    public Expense(String belongsTo, String name, String description, double amount, LocalDate dateCreated) {
        this.belongsTo = belongsTo;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(String belongsTo) {
        this.belongsTo = belongsTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
