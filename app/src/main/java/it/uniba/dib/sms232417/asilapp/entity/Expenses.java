package it.uniba.dib.sms232417.asilapp.entity;

import java.util.Date;

public class Expenses {

    public enum Category {
        FARMACI,
        TERAPIE,
        ALTRO,
        ESAMI
    }

    private Category category;
    private double amount;
    private Date date;

    // Costruttore
    public Expenses(Category category, double amount, Date date) {
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    // Getter per category
    public Category getCategory() {
        return category;
    }

    // Getter per amount
    public double getAmount() {
        return amount;
    }

    // Getter per date
    public Date getDate() {
        return date;
    }


}