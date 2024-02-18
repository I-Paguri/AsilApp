package it.uniba.dib.sms232417.asilapp.interfaces;

public interface OnTotalExpensesCallback {

    void onCallback(double totalExpenses);

    void onCallbackError(Exception e);
}