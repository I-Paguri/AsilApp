package it.uniba.dib.sms232417.asilapp.patientsFragments.placeholder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class OperationItem {
    private String description;
    private int money;
    private String operationDate;
    private String quantity;
    private String category;

    // Constructor, getters and setters...


    public OperationItem(String description, int money, String operationDate, String quantity, String category) {
        this.description = description;
        this.money = money;
        this.operationDate = operationDate;
        this.quantity = quantity;
        this.category = category;


    }

    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantityProduct(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

