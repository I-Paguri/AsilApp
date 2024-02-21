package it.uniba.dib.sms232417.asilapp.entity;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.Map;

public class Expenses {

    public Expenses() {
    }

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


    public static Expenses fromMap(Map<String, Object> map) {
        Expenses.Category category = Expenses.Category.valueOf((String) map.get("Category"));
        double amount = (double) map.get("amount");
        Timestamp timestamp = (Timestamp) map.get("date");
        Date date = timestamp.toDate();

        return new Expenses(category, amount, date);
    }




}