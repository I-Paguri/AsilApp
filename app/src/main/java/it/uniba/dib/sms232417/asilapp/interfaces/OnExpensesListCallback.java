package it.uniba.dib.sms232417.asilapp.interfaces;

import java.util.List;

import it.uniba.dib.sms232417.asilapp.entity.Expenses;

public interface OnExpensesListCallback {
    void onCallback(List<Expenses> expensesList);
    void onCallbackError(Exception e);
}
