package it.uniba.dib.sms232417.asilapp.interfaces;

import java.util.ArrayList;

public interface OnGetValueFromDBInterface {
    void onCallback(ArrayList<?> listOfValue);
    void onCallbackError(Exception exception, String message);
}
