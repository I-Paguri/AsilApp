package it.uniba.dib.sms232417.asilapp.entity;

public class Expenses {

    private String category;
    private float amount;

    // Costruttore
    public Expenses(String category, float amount) {
        this.category = category;
        this.amount = amount;
    }

    // Getter per category
    public String getCategory() {
        return category;
    }

    // Getter per amount
    public float getAmount() {
        return amount;
    }
}