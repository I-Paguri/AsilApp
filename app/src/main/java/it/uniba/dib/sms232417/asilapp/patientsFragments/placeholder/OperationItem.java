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
    private String money; // Changed from int to String
    private String operationDate; // Changed from Date to String

    // Constructor, getters and setters...


    public OperationItem(String description, String money, String operationDate) {
        this.description = description;
        this.money = money;
        this.operationDate = operationDate;


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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}

